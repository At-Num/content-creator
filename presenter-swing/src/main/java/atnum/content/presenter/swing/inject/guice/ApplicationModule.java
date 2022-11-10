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

package atnum.content.presenter.swing.inject.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import atnum.content.core.app.AppDataLocator;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.LocaleProvider;
import atnum.content.core.app.Theme;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.configuration.ConfigurationService;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.audio.AudioSystemProvider;
import atnum.content.core.audio.bus.AudioBus;
import atnum.content.core.bus.ApplicationBus;
import atnum.content.core.bus.EventBus;
import atnum.content.core.controller.PresentationController;
import atnum.content.core.controller.RenderController;
import atnum.content.core.controller.ToolController;
import atnum.content.core.service.DisplayService;
import atnum.content.core.service.DocumentService;
import atnum.content.core.util.AggregateBundle;
import atnum.content.core.util.DirUtils;
import atnum.content.core.util.FileUtils;
import atnum.content.core.view.PresentationViewFactory;
import atnum.content.media.webrtc.WebRtcAudioSystemProvider;
import atnum.content.presenter.api.config.DefaultConfiguration;
import atnum.content.presenter.api.config.PresenterConfigService;
import atnum.content.presenter.api.config.PresenterConfiguration;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.recording.LectureScreenRecorder;
import atnum.content.swing.AwtPresentationViewFactory;
import atnum.content.swing.DefaultRenderContext;
import atnum.content.swing.service.AwtDisplayService;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import static java.util.Objects.isNull;

public class ApplicationModule extends AbstractModule {

	private final static Logger LOG = LogManager.getLogger(ApplicationModule.class);

	private static final AppDataLocator LOCATOR = new AppDataLocator("lecturePresenter");

	private static final File CONFIG_FILE = new File(LOCATOR.toAppDataPath("config.json"));

	private static String vidDir = Path.of(System.getProperty("user.home"), "AtnumStatic").toString();


	@Override
	protected void configure() {
		bind(ApplicationContext.class).to(PresenterContext.class);
		bind(AudioSystemProvider.class).to(WebRtcAudioSystemProvider.class);
		bind(ToolController.class).asEagerSingleton();

		Properties streamProps = new Properties();

		try {
			streamProps.load(getClass().getClassLoader()
					.getResourceAsStream("resources/stream.properties"));

			Names.bindProperties(binder(), streamProps);
		}
		catch (IOException e) {
			LOG.error("Load stream properties failed", e);
		}

		FileUtils.create(vidDir);
	}


	@Provides
	@Singleton
	LectureScreenRecorder createLectureScreenRecorder(AudioSystemProvider audioSystemProvider, ApplicationContext context)
			throws IOException {
		return new LectureScreenRecorder(audioSystemProvider, context, vidDir);
	}


	@Provides
	@Singleton
	RenderController createRenderController(ApplicationContext context) {
		return new RenderController(context, new DefaultRenderContext());
	}

	@Provides
	@Singleton
	PresentationController createPresentationController(ApplicationContext context) {
		// Handle presentation views with AWT to avoid slow FX scene graph updates.
		DisplayService displayService = new AwtDisplayService();
		PresentationViewFactory viewFactory = new AwtPresentationViewFactory();

		return new PresentationController(context, displayService, viewFactory);
	}

	@Provides
	@Singleton
	ResourceBundle createResourceBundle(Configuration config) throws Exception {
		LocaleProvider localeProvider = new LocaleProvider();
		Locale locale = localeProvider.getBestSupported(config.getLocale());

		return new AggregateBundle(locale, "resources.i18n.core", "resources.i18n.dict");
	}

	@Provides
	@Singleton
	AggregateBundle createAggregateBundle(ResourceBundle resourceBundle) {
		return (AggregateBundle) resourceBundle;
	}

	@Provides
	@Singleton
	DocumentService createDocumentService(ApplicationContext context) {
		return context.getDocumentService();
	}



	@Provides
	@Singleton
	PresenterContext createApplicationContext(Configuration config, Dictionary dict) {
		EventBus eventBus = ApplicationBus.get();
		EventBus audioBus = AudioBus.get();

		return new PresenterContext(LOCATOR, CONFIG_FILE, config, dict, eventBus, audioBus);
	}

	@Provides
	@Singleton
	ConfigurationService<PresenterConfiguration> provideConfigurationService() {
		ConfigurationService<PresenterConfiguration> configService = null;

		try {
			configService = new PresenterConfigService();
		}
		catch (Exception e) {
			LOG.error("Create configuration service failed", e);
		}

		return configService;
	}

	@Provides
	@Singleton
	Configuration provideConfiguration(PresenterConfigService configService) {
		PresenterConfiguration configuration = null;

		try {
			DirUtils.createIfNotExists(Paths.get(LOCATOR.getAppDataPath()));

			if (!CONFIG_FILE.exists()) {
				// Create configuration with default values.
				configuration = new DefaultConfiguration();
				configService.save(CONFIG_FILE, configuration);
				configuration.setLocale(Locale.ENGLISH);
			}
			else {
				configuration = configService.load(CONFIG_FILE, PresenterConfiguration.class);
				if ( isNull(configuration )) {
					configuration = new DefaultConfiguration();
					configuration.setLocale(Locale.ENGLISH);
				} else {
					configService.validate(configuration);
				}
			}
			configuration.setApplicationName("AtNum Static Content Creator");
			configuration.setTheme(new Theme( "default", null));
			configuration.setLocale(Locale.ENGLISH);
			configuration.setCheckNewVersion(false);
			configuration.setStartFullscreen(false);
			configuration.setStartMaximized(false);
			configuration.setTabletMode(false);
			configuration.setAdvancedUIMode(false);
			configuration.setExtendedFullscreen(false);
			//configuration.setExtendPageDimension( new Dimension2D(1.3199999999999998,  0.9899999999999999));
			configuration.setConfirmStopRecording(true);
			configuration.setNotifyToRecord(false);
			configuration.setConfirmStopRecording(true);
		}
		catch (Exception e) {
			LOG.error("Create configuration failed", e);
		}

		return configuration;
	}

	@Provides
	@Singleton
	Dictionary provideDictionary(ResourceBundle resourceBundle) {
		return new Dictionary() {

			@Override
			public String get(String key) throws NullPointerException {
				return resourceBundle.getString(key);
			}

			@Override
			public boolean contains(String key) {
				return resourceBundle.containsKey(key);
			}
		};
	}

}
