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

import atnum.content.core.view.*;
import com.google.common.eventbus.Subscribe;
import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.audio.AudioDeviceNotConnectedException;
import atnum.content.core.bus.EventBus;
import atnum.content.core.bus.event.CustomizeToolbarEvent;
import atnum.content.core.bus.event.DocumentEvent;
import atnum.content.core.bus.event.PageEvent;
import atnum.content.core.bus.event.ViewVisibleEvent;
import atnum.content.core.controller.ToolController;
import atnum.content.core.model.Document;
import atnum.content.core.model.Page;
import atnum.content.core.model.RecentDocument;
import atnum.content.core.model.listener.PageEditEvent;
import atnum.content.core.model.listener.ParameterChangeListener;
import atnum.content.core.presenter.AboutPresenter;
import atnum.content.core.presenter.Presenter;
import atnum.content.core.presenter.command.CloseApplicationCommand;
import atnum.content.core.presenter.command.ShowPresenterCommand;
import atnum.content.core.service.DocumentService;
import atnum.content.core.util.FileUtils;
import atnum.content.core.util.ListChangeListener;
import atnum.content.core.util.ObservableList;
import atnum.content.presenter.api.config.PresenterConfiguration;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.service.RecordingService;
import atnum.content.presenter.api.view.MenuView;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MenuPresenter extends Presenter<MenuView> {

	private final DateTimeFormatter timeFormatter;

	private final Timer timer;

	private final EventBus eventBus;


	@Inject
	private ToolController toolController;

	@Inject
	private ViewContextFactory viewFactory;


	@Inject
	private DocumentService documentService;

	@Inject
	private RecordingService recordingService;



	@Inject
	MenuPresenter(ApplicationContext context, MenuView view) {
		super(context, view);

		this.eventBus = context.getEventBus();
		this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm", getPresenterConfig().getLocale());
		this.timer = new Timer("MenuTime", true);
	}

	@Subscribe
	public void onEvent(final DocumentEvent event) {
		Document doc = event.closed() ? null : event.getDocument();
		Page page = isNull(doc) ? null : doc.getCurrentPage();

		if (event.selected() && nonNull(page)) {
			page.addPageEditedListener(this::pageEdited);
		}

		view.setDocument(doc);

		pageChanged(page);
	}

	@Subscribe
	public void onEvent(final PageEvent event) {
		final Page page = event.getPage();

		if (event.isRemoved()) {
			page.removePageEditedListener(this::pageEdited);
		} else if (event.isSelected()) {
			Page oldPage = event.getOldPage();

			if (nonNull(oldPage)) {
				oldPage.removePageEditedListener(this::pageEdited);
			}

			page.addPageEditedListener(this::pageEdited);

			pageChanged(page);
		}
	}

	@Subscribe
	public void onEvent(final ViewVisibleEvent event) {
		Class<? extends View> viewClass = event.getViewClass();
		boolean visible = event.isVisible();

	}

	public void openDocument(File documentFile) {
		documentService.openDocument(documentFile)
				.exceptionally(throwable -> {
					handleException(throwable, "Open document failed", "open.document.error", documentFile.getPath());
					return null;
				});
	}

	public void closeSelectedDocument() {
		documentService.closeSelectedDocument();
	}

	public void saveDocuments() {
		eventBus.post(new ShowPresenterCommand<>(SaveDocumentsPresenter.class));
	}

	public void exit() {
		eventBus.post(new CloseApplicationCommand());
	}

	public void undo() {
		toolController.undo();
	}

	public void redo() {
		toolController.redo();
	}

	public void showSettingsView() {
		eventBus.post(new ShowPresenterCommand<>(SettingsPresenter.class));
	}

	public void customizeToolbar() {
		eventBus.post(new CustomizeToolbarEvent());
	}


	public void newWhiteboard() {
		PresenterConfiguration config = getPresenterConfig();
		String template = config.getTemplateConfig()
				.getWhiteboardTemplateConfig().getTemplatePath();

		documentService.addWhiteboard(template);
	}

	public void newWhiteboardPage() {
		documentService.createWhiteboardPage();
	}

	public void deleteWhiteboardPage() {
		documentService.deleteWhiteboardPage();
	}

	public void showGrid(boolean show) {
		toolController.toggleGrid();
	}

	public void startRecording() {
		try {
			if (recordingService.started()) {
				recordingService.suspend();
			} else {
				recordingService.start();
			}
		} catch (ExecutableException e) {
			Throwable cause = nonNull(e.getCause()) ? e.getCause().getCause() : null;

			if (cause instanceof AudioDeviceNotConnectedException) {
				var ex = (AudioDeviceNotConnectedException) cause;
				showError("recording.start.error", "recording.start.device.error", ex.getDeviceName());
				logException(e, "Start recording failed");
			} else {
				handleException(e, "Start recording failed", "recording.start.error");
			}
		}
	}

	public void stopRecording() {
		PresenterConfiguration config = getPresenterConfig();

		if (config.getConfirmStopRecording()) {
			eventBus.post(new ShowPresenterCommand<>(ConfirmStopRecordingPresenter.class));
		} else {
			try {
				recordingService.stop();

				eventBus.post(new ShowPresenterCommand<>(SaveRecordingPresenter.class));
			} catch (ExecutableException e) {
				handleException(e, "Stop recording failed", "recording.stop.error");
			}
		}
	}

	public void showLog() {
		try {
			Desktop.getDesktop().open(new File(
					context.getDataLocator().getAppDataPath()));
		} catch (IOException e) {
			handleException(e, "Open log path failed", "generic.error");
		}
	}

	public void showAboutView() {
		eventBus.post(new ShowPresenterCommand<>(AboutPresenter.class));
	}

	private void selectNewDocument() {
		final String pathContext = PresenterContext.SLIDES_CONTEXT;
		Configuration config = getPresenterConfig();
		Dictionary dict = context.getDictionary();
		Map<String, String> contextPaths = config.getContextPaths();
		Path dirPath = FileUtils.getContextPath(config, pathContext);

		FileChooserView fileChooser = viewFactory.createFileChooserView();
		fileChooser.setInitialDirectory(dirPath.toFile());
		fileChooser.addExtensionFilter(dict.get("file.description.pdf"),
				PresenterContext.SLIDES_EXTENSION);

		File selectedFile = fileChooser.showOpenFile(view);

		if (nonNull(selectedFile)) {
			contextPaths.put(pathContext, selectedFile.getParent());

			openDocument(selectedFile);
		}
	}

	private void pageChanged(Page page) {
		PresentationParameter parameter = null;

		if (nonNull(page)) {
			PresentationParameterProvider ppProvider = context.getPagePropertyProvider(ViewType.User);
			parameter = ppProvider.getParameter(page);
		}

		view.setPage(page, parameter);
	}

	private void pageEdited(final PageEditEvent event) {
		if (event.shapedChanged()) {
			return;
		}

		// Update undo/redo etc. items.
		Page page = event.getPage();
		PresentationParameter parameter = null;

		if (nonNull(page)) {
			PresentationParameterProvider ppProvider = context.getPagePropertyProvider(ViewType.User);
			parameter = ppProvider.getParameter(page);
		}

		view.setPage(page, parameter);
	}

	private PresenterConfiguration getPresenterConfig() {
		return (PresenterConfiguration) context.getConfiguration();
	}

	@Override
	public void initialize() {
		final PresenterContext presenterContext = (PresenterContext) context;
		final PresenterConfiguration config = getPresenterConfig();

		eventBus.register(this);
		view.setDocument(null);
		view.setPage(null, null);

		view.setOnOpenDocument(this::selectNewDocument);
		view.setOnOpenDocument(this::openDocument);
		view.setOnCloseDocument(this::closeSelectedDocument);
		view.setOnSaveDocuments(this::saveDocuments);
		view.setOnExit(this::exit);

		view.setOnUndo(this::undo);
		view.setOnRedo(this::redo);
		view.setOnSettings(this::showSettingsView);
		view.bindFullscreen(presenterContext.fullscreenProperty());
		view.setOnCustomizeToolbar(this::customizeToolbar);
		view.setOnNewWhiteboard(this::newWhiteboard);
		view.setOnNewWhiteboardPage(this::newWhiteboardPage);
		view.setOnDeleteWhiteboardPage(this::deleteWhiteboardPage);
		view.setOnShowGrid(this::showGrid);
		view.setOnOpenLog(this::showLog);
		view.setOnOpenAbout(this::showAboutView);

		// Register for page parameter change updates.
		PresentationParameterProvider ppProvider = context.getPagePropertyProvider(ViewType.User);
		ppProvider.addParameterChangeListener(new ParameterChangeListener() {

			@Override
			public Page forPage() {
				Document selectedDocument = documentService.getDocuments().getSelectedDocument();
				return nonNull(selectedDocument) ? selectedDocument.getCurrentPage() : null;
			}

			@Override
			public void parameterChanged(Page page, PresentationParameter parameter) {
				view.setPage(page, parameter);
			}
		});

		// Set file menu.
		ObservableList<RecentDocument> recentDocs = getPresenterConfig().getRecentDocuments();

		// Add new (sorted) recent document items.
		if (!recentDocs.isEmpty()) {
			Iterator<RecentDocument> iter = recentDocs.iterator();

			while (iter.hasNext()) {
				final String path = iter.next().getDocumentPath();

				File file = new File(path);
				if (!file.exists()) {
					// Skip and remove missing document.
					iter.remove();
				}
			}

			view.setRecentDocuments(recentDocs);
		}

		// Subscribe to document changes.
		recentDocs.addListener(new ListChangeListener<>() {

			@Override
			public void listChanged(ObservableList<RecentDocument> list) {
				view.setRecentDocuments(list);
			}

		});


		// Update current time every 30 seconds.
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				view.setCurrentTime(LocalDateTime.now().format(timeFormatter));
			}
		}, 0, 30000);
	}
}
