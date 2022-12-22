/*
 * Copyright (C) 2022 TU Darmstadt, Department of Computer Science,
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

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.inject.Inject;

import atnum.content.core.Executable;
import atnum.content.core.ExecutableState;
import atnum.content.core.app.configuration.AudioConfiguration;
import atnum.content.core.audio.AudioPlayer;
import atnum.content.core.audio.AudioRecorder;
import atnum.content.core.audio.AudioSystemProvider;
import atnum.content.core.audio.device.AudioDevice;
import atnum.content.core.audio.sink.AudioSink;
import atnum.content.core.audio.sink.ByteArrayAudioSink;
import atnum.content.core.audio.source.ByteArrayAudioSource;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.bus.ApplicationBus;
import atnum.content.core.bus.EventBus;
import atnum.content.core.bus.event.RecordFileNameEvent;
import atnum.content.core.presenter.Presenter;
import atnum.content.core.view.Action;
import atnum.content.core.view.ViewLayer;
import atnum.content.presenter.api.context.PresenterContext;
import atnum.content.presenter.api.service.RecordingService;
import atnum.content.presenter.api.view.StartRecordingView;
import atnum.content.swing.util.SwingUtils;

public class StartRecordingPresenter extends Presenter<StartRecordingView> {

	private final AudioSystemProvider audioSystemProvider;

	private final AudioConfiguration audioConfig;

	/** The action that is executed when the saving process has been aborted. */
	private Action startAction;

	private AudioRecorder testRecorder;

	private AudioPlayer audioPlayer;

	private AudioSink testAudioSink;

	private BooleanProperty testCapture;

	private BooleanProperty testPlayback;

	private BooleanProperty captureEnabled;

	private BooleanProperty playbackEnabled;

	private final EventBus eventBus;

	@Inject
	private RecordingService recordingService;


	@Inject
	StartRecordingPresenter(PresenterContext context, StartRecordingView view,
			AudioSystemProvider audioSystemProvider) {
		super(context, view);
		this.eventBus = context.getEventBus();
		this.audioSystemProvider = audioSystemProvider;
		this.audioConfig = context.getConfiguration().getAudioConfig();
	}

	@Override
	public void initialize() {
		testCapture = new BooleanProperty();
		testPlayback = new BooleanProperty();
		captureEnabled = new BooleanProperty(true);
		playbackEnabled = new BooleanProperty();

		testCapture.addListener((observable, oldValue, newValue) -> {
			recordCaptureTest(newValue);
		});
		testPlayback.addListener((observable, oldValue, newValue) -> {
			try {
				playCaptureTest(newValue);
			}
			catch (Exception e) {
				handleException(e, "Test playback failed",
						"microphone.settings.test.playback.error",
						"microphone.settings.test.playback.error.message");
			}
		});

		validateMicrophone();
		validateSpeaker();

		view.setAudioCaptureDevices(audioSystemProvider.getRecordingDevices());
		view.setAudioCaptureDevice(audioConfig.captureDeviceNameProperty());
		view.setAudioPlaybackDevices(audioSystemProvider.getPlaybackDevices());
		view.setAudioPlaybackDevice(audioConfig.playbackDeviceNameProperty());
		view.setAudioTestCaptureEnabled(captureEnabled);
		view.setAudioTestPlaybackEnabled(playbackEnabled);
		view.setOnAudioTestCapture(testCapture);
		view.setOnAudioTestCapturePlayback(testPlayback);
		view.setOnStart(this::onStart);
		view.setOnClose(this::close);
	}

	@Override
	public void close() {
		dispose();
	}

	@Override
	public ViewLayer getViewLayer() {
		return ViewLayer.Dialog;
	}

	public void setOnStart(Action action) {
		startAction = action;
	}

	private void onStart() {
		recordingService.onFileNameUpdate(view.getRecordingName().trim());
		dispose();

		if (nonNull(startAction)) {
			startAction.execute();
		}
	}

	private void dispose() {
		stopAudioCapture();

		super.close();
	}

	private void validateMicrophone() {
		var devices = audioSystemProvider.getRecordingDevices();

		// Check if the recently used microphone is connected.
		if (missingDevice(devices, audioConfig.getCaptureDeviceName())) {
			var device = audioSystemProvider.getDefaultRecordingDevice();

			if (nonNull(device)) {
				// Select the system's default microphone.
				audioConfig.setCaptureDeviceName(device.getName());
			}
			else if (devices.length > 0) {
				// Select the first available microphone.
				audioConfig.setCaptureDeviceName(devices[0].getName());
			}
		}
	}

	private void validateSpeaker() {
		var devices = audioSystemProvider.getPlaybackDevices();

		// Check if the recently used speaker is connected.
		if (missingDevice(devices, audioConfig.getPlaybackDeviceName())) {
			var device = audioSystemProvider.getDefaultPlaybackDevice();

			if (nonNull(device)) {
				// Select the system's default speaker.
				audioConfig.setPlaybackDeviceName(device.getName());
			}
			else if (devices.length > 0) {
				// Select the first available speaker.
				audioConfig.setPlaybackDeviceName(devices[0].getName());
			}
		}
	}

	private boolean missingDevice(AudioDevice[] devices, String deviceName) {
		if (isNull(deviceName)) {
			return true;
		}

		return Arrays.stream(devices)
				.noneMatch(device -> device.getName().equals(deviceName));
	}

	private void recordCaptureTest(boolean capture) {
		playbackEnabled.set(!capture);

		if (capture) {
			testAudioSink = new ByteArrayAudioSink();
			testAudioSink.setAudioFormat(audioConfig.getRecordingFormat());

			testRecorder = createAudioRecorder();
			testRecorder.setAudioSink(testAudioSink);
			testRecorder.setAudioProcessingSettings(
					audioConfig.getRecordingProcessingSettings());

			startAudioExecutable(testRecorder);
		}
		else {
			stopAudioExecutable(testRecorder);
		}
	}

	private void playCaptureTest(boolean play) {
		captureEnabled.set(!play);

		if (play) {
			if (isNull(audioPlayer)) {
				audioPlayer = createAudioPlayer();
			}

			startAudioExecutable(audioPlayer);
		}
		else {
			stopAudioExecutable(audioPlayer);

			audioPlayer = null;
		}
	}

	private void stopAudioCapture() {
		if (nonNull(testRecorder) && testRecorder.started()) {
			// This will update the view and the model.
			testCapture.set(false);
		}
	}

	private void startAudioExecutable(Executable executable) {
		try {
			executable.start();
		}
		catch (Exception e) {
			logException(e, "Start audio executable failed");
		}
	}

	private void stopAudioExecutable(Executable executable) {
		if (executable.started() || executable.suspended()) {
			try {
				executable.stop();
				executable.destroy();
			}
			catch (Exception e) {
				logException(e, "Stop audio executable failed");
			}
		}
	}

	private AudioRecorder createAudioRecorder() {
		String inputDeviceName = audioConfig.getCaptureDeviceName();
		double volume = audioConfig.getMasterRecordingVolume();
		Double devVolume = audioConfig.getRecordingVolume(inputDeviceName);

		if (nonNull(devVolume)) {
			volume = devVolume;
		}

		AudioRecorder recorder = audioSystemProvider.createAudioRecorder();
		recorder.setAudioDeviceName(inputDeviceName);
		recorder.setAudioVolume(volume);

		return recorder;
	}

	private AudioPlayer createAudioPlayer() {
		ByteArrayAudioSink sink = (ByteArrayAudioSink) testAudioSink;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				sink.toByteArray());

		ByteArrayAudioSource source = new ByteArrayAudioSource(inputStream,
				audioConfig.getRecordingFormat());

		AudioPlayer player = audioSystemProvider.createAudioPlayer();
		player.setAudioDeviceName(audioConfig.getPlaybackDeviceName());
		player.setAudioVolume(1.0);
		player.setAudioSource(source);
		player.addStateListener((oldState, newState) -> {
			if (newState == ExecutableState.Stopped) {
				testPlayback.set(false);
			}
		});

		return player;
	}
}