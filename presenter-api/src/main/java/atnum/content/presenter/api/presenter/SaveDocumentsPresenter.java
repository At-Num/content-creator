/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package atnum.content.presenter.api.presenter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.beans.StringProperty;
import atnum.content.core.model.Document;
import atnum.content.core.model.Page;
import atnum.content.core.presenter.Presenter;
import atnum.content.core.presenter.ProgressPresenter;
import atnum.content.core.presenter.command.ShowPresenterCommand;
import atnum.content.core.recording.DocumentRecorder;
import atnum.content.core.util.FileUtils;
import atnum.content.core.view.FileChooserView;
import atnum.content.core.view.ProgressView;
import atnum.content.core.view.ViewContextFactory;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.view.SaveDocumentOptionView;
import atnum.content.presenter.api.view.SaveDocumentsView;
import atnum.content.swing.renderer.PdfDocumentRenderer;

public class SaveDocumentsPresenter extends Presenter<SaveDocumentsView> {

	private final DocumentRecorder documentRecorder;

	private final ViewContextFactory viewFactory;

	private final List<Document> selectedDocuments;

	private final SimpleDateFormat dateFormat;

	private final StringProperty savePath;

	private Document messagesDoc;


	@Inject
	SaveDocumentsPresenter(ApplicationContext context, SaveDocumentsView view,
			ViewContextFactory viewFactory, DocumentRecorder documentRecorder) {
		super(context, view);

		this.documentRecorder = documentRecorder;
		this.viewFactory = viewFactory;
		this.dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm");
		this.selectedDocuments = new ArrayList<>();
		this.savePath = new StringProperty();
	}

	@Override
	public void initialize() {
		PresenterContext pContext = (PresenterContext) context;

		for (Document doc : documentRecorder.getRecordedDocuments()) {
			SaveDocumentOptionView optionView = createDocumentOptionView(doc);

			view.addDocumentOptionView(optionView);

			optionView.select();
		}

		final String pathContext = PresenterContext.SLIDES_TO_PDF_CONTEXT;
		Configuration config = context.getConfiguration();
		Path dirPath = FileUtils.getContextPath(config, pathContext);

		savePath.set(dirPath.resolve(getFileName(null)).toString());
		view.setOnClose(this::close);

	}

	private void saveSelectedDocuments() {
		File file = new File(savePath.get());

		saveDocuments(selectedDocuments, file, true);
	}

	private void saveDocument(Document doc) {
		final String pathContext = PresenterContext.SLIDES_TO_PDF_CONTEXT;
		Configuration config = context.getConfiguration();
		Dictionary dict = context.getDictionary();
		Path dirPath = FileUtils.getContextPath(config, pathContext);

		FileChooserView fileChooser = viewFactory.createFileChooserView();
		fileChooser.addExtensionFilter(dict.get("file.description.pdf"),
				PresenterContext.SLIDES_EXTENSION);
		fileChooser.setInitialFileName(getFileName(doc.getName()));
		fileChooser.setInitialDirectory(dirPath.toFile());

		File selectedFile = fileChooser.showSaveFile(view);

		if (nonNull(selectedFile)) {
			saveDocuments(List.of(doc), selectedFile, false);
		}
	}

	private void selectDocument(Document doc) {
		selectedDocuments.add(doc);
	}

	private void deselectDocument(Document doc) {
		selectedDocuments.remove(doc);
	}

	private void selectSavePath() {
		Dictionary dict = context.getDictionary();
		File initPath = new File(savePath.get());

		FileChooserView fileChooser = viewFactory.createFileChooserView();
		fileChooser.addExtensionFilter(dict.get("file.description.pdf"),
				PresenterContext.SLIDES_EXTENSION);
		fileChooser.setInitialFileName(initPath.getName());
		fileChooser.setInitialDirectory(initPath.getParentFile());

		File selectedFile = fileChooser.showSaveFile(view);

		if (nonNull(selectedFile)) {
			savePath.set(selectedFile.getAbsolutePath());
		}
	}

	private String getFileName(String docName) {
		if (isNull(docName)) {
			docName = context.getDictionary().get("document.save.lecture");
		}

		String date = dateFormat.format(new Date());

		return docName + "-" + date + ".pdf";
	}

	private void saveDocuments(List<Document> documents, File file, boolean autoClose) {
		Configuration config = context.getConfiguration();
		config.getContextPaths().put(PresenterContext.SLIDES_TO_PDF_CONTEXT,
				file.getParent());

		context.getEventBus().post(new ShowPresenterCommand<>(ProgressPresenter.class) {
			@Override
			public void execute(ProgressPresenter presenter) {
				ProgressView progressView = presenter.getView();
				progressView.setTitle(context.getDictionary().get("save.documents.saving"));
				progressView.setMessage(file.getAbsolutePath());
				progressView.setOnViewShown(() -> {
					saveAsync(progressView, documents, file);
				});

				if (autoClose) {
					progressView.setOnClose(() -> close());
				}
			}
		});
	}

	private SaveDocumentOptionView createDocumentOptionView(Document doc) {
		SaveDocumentOptionView optionView = viewFactory.getInstance(SaveDocumentOptionView.class);
		optionView.setDocumentTitle(doc.getName());
		optionView.setOnSaveDocument(() -> {
			saveDocument(doc);
		});
		optionView.setOnSelectDocument(() -> {
			selectDocument(doc);
		});
		optionView.setOnDeselectDocument(() -> {
			deselectDocument(doc);
		});

		return optionView;
	}

	private void saveAsync(ProgressView progressView, List<Document> documents, File file) {
		CompletableFuture.runAsync(() -> {
			boolean hasMessages = nonNull(messagesDoc) && documents.contains(messagesDoc);

			List<Page> pages = documentRecorder.getRecordedPages();

			if (hasMessages) {
				// Use unrecorded message pages as well.
				pages = Stream.concat(pages.stream(), messagesDoc.getPages().stream())
						.collect(Collectors.toList());
			}

			PdfDocumentRenderer documentRenderer = new PdfDocumentRenderer();
			documentRenderer.setDocuments(documents);
			documentRenderer.setPages(pages);
			documentRenderer.setParameterProvider(documentRecorder.getRecordedParamProvider());
			documentRenderer.setProgressCallback(progressView::setProgress);
			documentRenderer.setOutputFile(file);

			try {
				documentRenderer.start();
			}
			catch (ExecutableException e) {
				throw new CompletionException(e);
			}
		})
		.thenRun(() -> {
			PresenterContext presenterContext = (PresenterContext) context;
			presenterContext.setHasRecordedChanges(false);

			progressView.setTitle(context.getDictionary().get("save.documents.success"));
		})
		.exceptionally(throwable -> {
			logException(throwable, "Write document to PDF failed");

			progressView.setError(context.getDictionary().get("document.save.error"));
			return null;
		});
	}
}