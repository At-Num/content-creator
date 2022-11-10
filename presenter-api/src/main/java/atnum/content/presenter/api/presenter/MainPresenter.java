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
import atnum.content.presenter.api.recording.RecordingBackup;
import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.app.util.SaveConfigurationHandler;
import atnum.content.core.audio.bus.event.AudioDeviceHotplugEvent;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.bus.event.DocumentEvent;
import atnum.content.core.bus.event.ViewVisibleEvent;
import atnum.content.core.controller.PresentationController;
import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.input.KeyEvent;
import atnum.content.core.model.Document;
import atnum.content.core.presenter.NotificationPresenter;
import atnum.content.core.presenter.Presenter;
import atnum.content.core.presenter.command.CloseApplicationCommand;
import atnum.content.core.presenter.command.ClosePresenterCommand;
import atnum.content.core.presenter.command.ShowPresenterCommand;
import atnum.content.core.service.DocumentService;
import atnum.content.core.util.ObservableHashMap;
import atnum.content.core.util.ObservableMap;
import atnum.content.core.util.ShutdownHandler;
import atnum.content.presenter.api.config.PresenterConfiguration;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.input.Shortcut;
import atnum.content.presenter.api.service.RecordingService;
import atnum.content.presenter.api.util.SaveDocumentsHandler;
import atnum.content.presenter.api.util.SaveRecordingHandler;
import atnum.content.presenter.api.view.MainView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Predicate;

import static java.util.Objects.*;

public class MainPresenter extends atnum.content.core.presenter.MainPresenter<MainView> implements ViewHandler {
	private final static Logger LOG = LogManager.getLogger(MainPresenter.class);
	private final ObservableMap<Class<? extends View>, BooleanProperty> viewMap;

	private final Map<KeyEvent, Predicate<KeyEvent>> shortcutMap;

	private final List<ShutdownHandler> shutdownHandlers;

	private final List<Presenter<?>> contexts;

	private final PresentationController presentationController;

	private final NotificationPopupManager popupManager;

	private final ViewContextFactory contextFactory;

	private final DocumentService documentService;

	private final RecordingService recordingService;


	private SlidesPresenter slidesPresenter;

	/** The waiting notification. */
	private NotificationPresenter notificationPresenter;


	@Inject
	MainPresenter(ApplicationContext context, MainView view,
			PresentationController presentationController,
			NotificationPopupManager popupManager,
			ViewContextFactory contextFactory,
			DocumentService documentService,
			RecordingService recordingService) {
		super(context, view);

		this.presentationController = presentationController;
		this.popupManager = popupManager;
		this.contextFactory = contextFactory;
		this.documentService = documentService;
		this.recordingService = recordingService;
		this.viewMap = new ObservableHashMap<>();
		this.shortcutMap = new HashMap<>();
		this.contexts = new ArrayList<>();
		this.shutdownHandlers = new ArrayList<>();
	}

	@Override
	public void openFile(File file) {
		// No file associations yet.
	}

	@Override
	public void setArgs(String[] args) {

	}

	@Override
	public void initialize() {
		registerShortcut(Shortcut.CLOSE_VIEW, this::closeView);
		registerShortcut(Shortcut.PAUSE_RECORDING, this::pauseRecording);
		registerShortcut(Shortcut.PAUSE_RECORDING_P, this::pauseRecording);

		PresenterContext presenterContext = (PresenterContext) context;
		PresenterConfiguration config = (PresenterConfiguration) context.getConfiguration();

		config.setAdvancedUIMode(true);

		addShutdownHandler(new SaveRecordingHandler(presenterContext));
		addShutdownHandler(new SaveDocumentsHandler(presenterContext));
		addShutdownHandler(new SaveConfigurationHandler(presenterContext));
		addShutdownHandler(new ShutdownHandler() {

			@Override
			public boolean execute() {
				view.hideView();
				return true;
			}
		});
		addShutdownHandler(new ShutdownHandler() {

			@Override
			public boolean execute() {
				if (nonNull(closeAction)) {
					closeAction.execute();
				}
				return true;
			}
		});

		context.setFullscreen(config.getStartFullscreen());
		context.fullscreenProperty().addListener((observable, oldValue, newValue) -> {
			setFullscreen(newValue);
		});

		config.extendedFullscreenProperty().addListener((observable, oldValue, newValue) -> {
			view.setMenuVisible(!newValue);
		});

		config.getAudioConfig().recordingFormatProperty().addListener((observable, oldFormat, newFormat) -> {
			recordingService.setAudioFormat(newFormat);
		});

		slidesPresenter = createPresenter(SlidesPresenter.class);

		if (nonNull(slidesPresenter)) {
			slidesPresenter.initialize();
		}

		view.setMenuVisible(!config.getExtendedFullscreen());
		view.setOnClose(this::closeWindow);
		view.setOnShown(this::onViewShown);
		view.setOnBounds(this::onViewBounds);
		view.setOnKeyEvent(this::keyEvent);

		context.getEventBus().register(this);

		// Create settings asynchronously.
		CompletableFuture.runAsync(() -> {
			try {
				SettingsPresenter settingsPresenter = createPresenter(SettingsPresenter.class);

				if (nonNull(settingsPresenter)) {
					settingsPresenter.initialize();
					settingsPresenter.setOnClose(() -> destroy(settingsPresenter));

					addContext(settingsPresenter);
				}
			}
			catch (Exception e) {
				throw new CompletionException(e);
			}
		})
		.exceptionally(throwable -> {
			logException(throwable, "Create settings failed");
			return null;
		});

	}

	@Subscribe
	public void onAudioDeviceHotplug(AudioDeviceHotplugEvent event) {
		String devName = event.getDeviceName();
		Dictionary dict = context.getDictionary();

		switch (event.getType()) {
			case Connected:
				showNotificationPopup(MessageFormat.format(dict.get("audio.device.connected"), devName));
				break;

			case Disconnected:
				showNotificationPopup(MessageFormat.format(dict.get("audio.device.disconnected"), devName));
				break;
		}
	}


	@Subscribe
	public void onCommand(CloseApplicationCommand command) {
		closeWindow();
	}

	@Subscribe
	public void onCommand(final ClosePresenterCommand command) {
		destroyHandler(command.getPresenterClass());
	}

	@Subscribe
	public <T extends Presenter<?>> void onCommand(ShowPresenterCommand<T> command) {
		T presenter = findCachedPresenter(command.getPresenterClass());

		if (isNull(presenter)) {
			presenter = createPresenter(command.getPresenterClass());
		}

		try {
			command.execute(presenter);
		}
		catch (Exception e) {
			logException(e, "Execute command failed");
		}

		display(presenter);
	}

	@Subscribe
	public void onEvent(DocumentEvent event) {
		Document doc = event.getDocument();

		if (event.created()) {
			documentCreated();
		}
		else if (event.closed()) {
			documentClosed(doc);
		}
		else if (event.selected()) {
		}
	}


	@Override
	public void addShutdownHandler(ShutdownHandler handler) {
		requireNonNull(handler, "ShutdownHandler must not be null.");

		if (!shutdownHandlers.contains(handler)) {
			shutdownHandlers.add(handler);
		}
	}

	@Override
	public void removeShutdownHandler(ShutdownHandler handler) {
		requireNonNull(handler, "ShutdownHandler must not be null.");

		shutdownHandlers.remove(handler);
	}

	@Override
	public void showView(View childView, ViewLayer layer) {
		if (layer == ViewLayer.NotificationPopup) {
			popupManager.show(view, (NotificationPopupView) childView);
		}
		else {
			view.showView(childView, layer);

			setViewShown(getViewInterface(childView.getClass()));
		}
	}

	@Override
	public void display(Presenter<?> presenter) {
		requireNonNull(presenter);

		Presenter<?> cachedPresenter = findCachedPresenter(presenter.getClass());

		try {
			if (nonNull(cachedPresenter)) {
				View view = cachedPresenter.getView();

				if (nonNull(view)) {
					BooleanProperty property = getViewVisibleProperty(getViewInterface(view.getClass()));

					if (property.get()) {
						return;
					}

					showView(view, cachedPresenter.getViewLayer());
				}
			}
			else {
				if (presenter.getClass().equals(NotificationPresenter.class) &&
						nonNull(notificationPresenter) &&
						!notificationPresenter.equals(presenter)) {
					hideWaitingNotification();
				}

				presenter.initialize();

				View view = presenter.getView();

				if (nonNull(view)) {
					BooleanProperty property = getViewVisibleProperty(getViewInterface(view.getClass()));

					if (property.get()) {
						return;
					}

					presenter.setOnClose(() -> destroy(presenter));

					showView(view, presenter.getViewLayer());

					addContext(presenter);
				}
			}
		}
		catch (Exception e) {
			handleException(e, "Show view failed", "error", "generic.error");
		}
	}

	@Override
	public void destroy(Presenter<?> presenter) {
		requireNonNull(presenter);

		View childView = presenter.getView();

		try {
			view.removeView(childView, presenter.getViewLayer());

			setViewHidden(getViewInterface(childView.getClass()));

			if (!presenter.cache()) {
				presenter.destroy();

				removeContext(presenter);
			}
		}
		catch (Exception e) {
			handleException(e, "Destroy view failed", "error", "generic.error");
		}
	}

	@Override
	public void closeWindow() {
		destroy();
	}

	@Override
	public void setFullscreen(boolean enable) {
		view.setFullscreen(enable);
	}

	@Override
	public void destroy() {
		LOG.debug("destroy no shutdownHandlers {}", shutdownHandlers.size());
		if (shutdownHandlers.isEmpty()) {
			return;
		}

		Runnable shutdownLoop = () -> {
			for (ShutdownHandler handler : shutdownHandlers) {
				try {
					if (!handler.execute()) {
						LOG.debug("destroy handler failed  " );
						// Abort shutdown process.
						break;
					}
					LOG.debug("destroy handler continues  " );
				}
				catch (Exception e) {
					LOG.debug("destroy handler failed {} ", e.getLocalizedMessage() );
					logException(e, "Execute shutdown handler failed");
				}
			}
		};

		Thread thread = new Thread(shutdownLoop, "ShutdownHandler-Thread");
		thread.start();
		System.exit(-1);
	}

	private void addContext(Presenter<?> presenter) {
		requireNonNull(presenter);

		if (!contexts.contains(presenter)) {
			contexts.add(presenter);
		}
	}

	private void removeContext(Presenter<?> presenter) {
		requireNonNull(presenter);

		contexts.remove(presenter);
	}

	@SuppressWarnings("unchecked")
	private <T extends Presenter<?>> T findCachedPresenter(Class<T> presenterClass) {
		requireNonNull(presenterClass);

		for (Presenter<?> p : contexts) {
			if (presenterClass == p.getClass() && p.cache()) {
				return (T) p;
			}
		}

		return null;
	}

	private boolean keyEvent(KeyEvent event) {
		Predicate<KeyEvent> action = shortcutMap.get(event);

		if (nonNull(action)) {
			return action.test(event);
		}

		return false;
	}

	private BooleanProperty getViewVisibleProperty(Class<? extends View> viewClass) {
		BooleanProperty property = viewMap.get(viewClass);

		if (isNull(property)) {
			property = new BooleanProperty(false);
			property.addListener((observable, oldValue, newValue) -> {
				context.getEventBus().post(new ViewVisibleEvent(viewClass, newValue));
			});

			viewMap.put(viewClass, property);
		}

		return property;
	}

	private void destroyHandler(Class<? extends Presenter<?>> presenterClass) {
		for (Presenter<?> presenter : contexts) {
			if (presenter.getClass() == presenterClass) {
				destroy(presenter);
				break;
			}
		}
	}

	private void onViewShown() {
		PresenterContext presenterContext = (PresenterContext) context;

		try {
			RecordingBackup backup = new RecordingBackup(presenterContext.getRecordingDirectory());

			if (backup.hasCheckpoint()) {
				display(createPresenter(RestoreRecordingPresenter.class));
			}
		}
		catch (IOException e) {
			handleException(e, "Open recording backup failed", "recording.restore.missing.backup");
		}
	}

	private void onViewBounds(Rectangle2D bounds) {
		presentationController.setMainWindowBounds(bounds);
	}

	private boolean closeView(KeyEvent event) {
		if (!contexts.isEmpty()) {
			Presenter<?> presenter = contexts.get(contexts.size() - 1);
			View view = presenter.getView();

			BooleanProperty property = getViewVisibleProperty(getViewInterface(view.getClass()));

			if (property.get()) {
				presenter.close();
				return true;
			}
		}

		return false;
	}

	private void registerShortcut(Shortcut shortcut, Predicate<KeyEvent> action) {
		shortcutMap.put(shortcut.getKeyEvent(), action);
	}

	private void showWaitingNotification(String title) {
		String message = "please.wait";

		if (context.getDictionary().contains(title)) {
			title = context.getDictionary().get(title);
		}
		if (context.getDictionary().contains(message)) {
			message = context.getDictionary().get(message);
		}

		notificationPresenter = createPresenter(NotificationPresenter.class);

		if (nonNull(notificationPresenter)) {
			notificationPresenter.setMessage(message);
			notificationPresenter.setNotificationType(NotificationType.WAITING);
			notificationPresenter.setTitle(title);

			display(notificationPresenter);
		}
	}

	private void hideWaitingNotification() {
		if (nonNull(notificationPresenter)) {
			destroy(notificationPresenter);
			notificationPresenter = null;
		}
	}

	private void setViewHidden(Class<? extends View> viewClass) {
		BooleanProperty property = getViewVisibleProperty(viewClass);
		property.set(false);
	}

	private void setViewShown(Class<? extends View> viewClass) {
		BooleanProperty property = getViewVisibleProperty(viewClass);
		property.set(true);
	}

	private boolean pauseRecording(KeyEvent event) {
		if (!recordingService.suspended()) {
			try {
				recordingService.suspend();
			}
			catch (Exception e) {
				handleException(e, "Pause recording failed", "recording.pause.error");
			}
		}

		return true;
	}

	private void documentCreated() {
		showView(slidesPresenter.getView(), slidesPresenter.getViewLayer());
	}

	private void documentClosed(Document doc) {
		if (documentService.getDocuments().asList().isEmpty()) {
			StartPresenter presenter = createPresenter(StartPresenter.class);

			if (nonNull(presenter)) {
				destroyHandler(presenter.getClass());

				Class<? extends View> viewClass = getViewInterface(presenter.getView().getClass());

				if (isNull(viewMap.get(viewClass))) {
					setViewHidden(viewClass);
				}

				display(presenter);
			}
		}

	}


	private <T extends Presenter<?>> T createPresenter(Class<T> pClass) {
		try {
			return contextFactory.getInstance(pClass);
		}
		catch (Throwable e) {
			handleException(e, "Create presenter failed", "generic.error");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends View> getViewInterface(Class<?> cls) {
		while (nonNull(cls)) {
			final Class<?>[] interfaces = cls.getInterfaces();

			for (final Class<?> i : interfaces) {
				if (i == View.class) {
					return (Class<? extends View>) cls;
				}

				return getViewInterface(i);
			}

			cls = cls.getSuperclass();
		}

		return null;
	}
}