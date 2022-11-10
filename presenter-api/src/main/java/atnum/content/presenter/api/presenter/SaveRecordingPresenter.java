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

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.configuration.ConfigurationService;
import atnum.content.core.app.configuration.JsonConfigurationService;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.model.RecentDocument;
import atnum.content.core.presenter.Presenter;
import atnum.content.core.util.FileUtils;
import atnum.content.core.view.Action;
import atnum.content.core.view.FileChooserView;
import atnum.content.core.view.ViewContextFactory;
import atnum.content.core.view.ViewLayer;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.service.RecordingService;
import atnum.content.presenter.api.view.SaveRecordingView;

public class SaveRecordingPresenter extends Presenter<SaveRecordingView> {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm");

	private final ViewContextFactory viewFactory;

	private final RecordingService recordingService;

	/** The action that is executed when the saving process has been aborted. */
	private Action abortAction;


	@Inject
	SaveRecordingPresenter(ApplicationContext context, SaveRecordingView view,
			ViewContextFactory viewFactory, RecordingService recordingService) {
		super(context, view);

		this.viewFactory = viewFactory;
		this.recordingService = recordingService;
	}

	@Override
	public void initialize() {
		setCloseable(false);

		view.setOnViewShown(this::chooseFile);
		view.setOnClose(this::close);
	}

	@Override
	public ViewLayer getViewLayer() {
		return ViewLayer.Dialog;
	}

	@Override
	public void setOnClose(Action action) {
		setOnAbort(action);

		super.setOnClose(action);
	}

	public void setOnAbort(Action action) {
		abortAction = Action.concatenate(abortAction, action);
	}

	private void chooseFile() {
		final String pathContext = PresenterContext.RECORDING_CONTEXT;
		Configuration config = context.getConfiguration();
		Dictionary dict = context.getDictionary();
		String recordingPath = config.getAudioConfig().getRecordingPath();

		Path dirPath = FileUtils.getContextPath(config, pathContext, recordingPath);

		String date = dateFormat.format(new Date());
		String fileName = recordingService.getBestRecordingName() + "-" + date;

		FileChooserView fileChooser = viewFactory.createFileChooserView();
		fileChooser.addExtensionFilter(dict.get("file.description.recording"),
				PresenterContext.RECORDING_EXTENSION);
		fileChooser.setInitialFileName(fileName);
		fileChooser.setInitialDirectory(dirPath.toFile());

		File selectedFile = fileChooser.showSaveFile(view);

		if (nonNull(selectedFile)) {
			config.getContextPaths().put(PresenterContext.RECORDING_CONTEXT,
					selectedFile.getParent());
			config.getAudioConfig().setRecordingPath(selectedFile.getParent());

			File recFile = FileUtils.ensureExtension(selectedFile, "." + PresenterContext.RECORDING_EXTENSION);

			view.setDestinationFile(recFile);

			recordingService.writeRecording(recFile, this::onProgress)
				.thenRun(() -> {
					writeRecordedDocumentMetadata(recFile);

					setCloseable(true);
					view.setSuccess();
				})
				.exceptionally(throwable -> {
					logException(throwable, "Write recording failed");

					view.setError(context.getDictionary().get("recording.save.error"));
					return null;
				});
		}
		else {
			try {
				recordingService.discardRecording();

				setCloseable(true);
				onAbort();
			}
			catch (ExecutableException e) {
				logException(e, "Discard recording failed");

				view.setError(context.getDictionary().get("recording.discard.error"));
			}
		}
	}

	private void onAbort() {
		if (nonNull(abortAction)) {
			abortAction.execute();
		}
	}

	private void onProgress(float progress) {
		view.setProgress(progress);
	}

	private void writeRecordedDocumentMetadata(File selectedFile) {
		ConfigurationService<RecentDocument> configService = new JsonConfigurationService<>();

		File docFile = new File(context.getDataLocator().toAppDataPath("last-recording.dat"));
		RecentDocument recentDoc = new RecentDocument();
		recentDoc.setLastModified(new Date());
		recentDoc.setDocumentName(FileUtils.stripExtension(selectedFile.getName()));
		recentDoc.setDocumentPath(selectedFile.getAbsolutePath());

		try {
			configService.save(docFile, recentDoc);
		}
		catch (IOException e) {
			logException(e, "Save recent recording metadata failed");
		}
	}
}