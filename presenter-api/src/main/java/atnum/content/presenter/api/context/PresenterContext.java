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

package atnum.content.presenter.api.context;

import atnum.content.core.app.AppDataLocator;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.bus.EventBus;
import atnum.content.presenter.api.config.PresenterConfigService;
import atnum.content.presenter.api.config.PresenterConfiguration;

import java.io.File;

public class PresenterContext extends ApplicationContext {

	public static final String SLIDES_CONTEXT = "Slides";
	public static final String SLIDES_TO_PDF_CONTEXT = "SlidesToPDF";
	public static final String SLIDES_EXTENSION = "pdf";

	public static final String RECORDING_CONTEXT = "Recording";
	public static final String RECORDING_EXTENSION = "presenter";


	private final BooleanProperty recordingStarted = new BooleanProperty();

	private final BooleanProperty hasRecordedChanges = new BooleanProperty();


	private final BooleanProperty showOutline = new BooleanProperty();


	private final File configFile;

	private final String recordingDir;


	public PresenterContext(AppDataLocator dataLocator, File configFile,
			Configuration config, Dictionary dict, EventBus eventBus,
			EventBus audioBus ) {
		super(dataLocator, config, dict, eventBus, audioBus);

		this.configFile = configFile;

		this.recordingDir = getDataLocator().toAppDataPath("recording");

	}

	public PresenterConfiguration getConfiguration() {
		return (PresenterConfiguration) super.getConfiguration();
	}

	@Override
	public void saveConfiguration() throws Exception {
		var configService = new PresenterConfigService();
		configService.save(configFile, getConfiguration());
	}


	public void setHasRecordedChanges(boolean changes) {
		hasRecordedChanges.set(changes);
	}

	public boolean hasRecordedChanges() {
		return hasRecordedChanges.get();
	}

	public BooleanProperty hasRecordedChangesProperty() {
		return hasRecordedChanges;
	}


	public void setRecordingStarted(boolean started) {
		recordingStarted.set(started);
	}

	public boolean getRecordingStarted() {
		return recordingStarted.get();
	}

	public BooleanProperty recordingStartedProperty() {
		return recordingStarted;
	}

	public void setShowOutline(boolean show) {
		showOutline.set(show);
	}

	public BooleanProperty showOutlineProperty() {
		return showOutline;
	}


	public String getRecordingDirectory() {
		return recordingDir;
	}
}
