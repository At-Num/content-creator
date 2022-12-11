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

package atnum.content.presenter.swing.view;

import java.awt.Container;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import atnum.content.core.audio.device.AudioDevice;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.beans.StringProperty;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.presenter.api.view.StartRecordingView;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;

@SwingView(name = "start-recording")
public class SwingStartRecordingView extends JPanel implements StartRecordingView {

	private ConsumerAction<Boolean> viewVisibleAction;

	private Container contentContainer;

	private JComboBox<String> audioCaptureDevicesCombo;

	private JComboBox<String> audioPlaybackDevicesCombo;

	private JToggleButton testCaptureButton;

	private JToggleButton playCaptureButton;

	private JButton closeButton;

	private JButton startButton;


	SwingStartRecordingView() {
		super();
	}

	@Override
	public void setAudioCaptureDevice(StringProperty captureDeviceName) {
		SwingUtils.invoke(() -> {
			SwingUtils.bindBidirectional(audioCaptureDevicesCombo, captureDeviceName);
		});
	}

	@Override
	public void setAudioCaptureDevices(AudioDevice[] captureDevices) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		for (AudioDevice device : captureDevices) {
			model.addElement(device.getName());
		}

		SwingUtils.invoke(() -> {
			audioCaptureDevicesCombo.setLightWeightPopupEnabled(false);
			audioCaptureDevicesCombo.setModel(model);
		});
	}

	@Override
	public void setAudioPlaybackDevice(StringProperty playbackDeviceName) {
//		SwingUtils.invoke(() -> {
//			SwingUtils.bindBidirectional(audioPlaybackDevicesCombo, playbackDeviceName);
//		});
	}

	@Override
	public void setAudioPlaybackDevices(AudioDevice[] playbackDevices) {
//		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
//
//		for (AudioDevice device : playbackDevices) {
//			model.addElement(device.getName());
//		}

//		SwingUtils.invoke(() -> {
//			audioPlaybackDevicesCombo.setLightWeightPopupEnabled(false);
//			audioPlaybackDevicesCombo.setModel(model);
//		});
	}

	@Override
	public void setAudioTestCaptureEnabled(BooleanProperty enable) {
//		boolean enabled = enable.get();
//
//		if (!enabled) {
//			SwingUtils.invoke(() -> {
//				testCaptureButton.setSelected(false);
//				testCaptureButton.setEnabled(false);
//			});
//		}
//
//		enable.addListener((observable, oldValue, newValue) ->
//				testCaptureButton.setEnabled(newValue));
	}

	@Override
	public void setAudioTestPlaybackEnabled(BooleanProperty enable) {
//		boolean enabled = enable.get();
//
//		if (!enabled) {
//			SwingUtils.invoke(() -> {
//				playCaptureButton.setSelected(false);
//				playCaptureButton.setEnabled(false);
//			});
//		}
//
//		enable.addListener((observable, oldValue, newValue) ->
//				playCaptureButton.setEnabled(newValue));
	}

	@Override
	public void setOnAudioTestCapture(BooleanProperty recordProperty) {
//		SwingUtils.bindBidirectional(testCaptureButton, recordProperty);
	}

	@Override
	public void setOnAudioTestCapturePlayback(BooleanProperty playProperty) {
//		SwingUtils.bindBidirectional(playCaptureButton, playProperty);
	}

	@Override
	public void setOnClose(Action action) {
		SwingUtils.bindAction(closeButton, action);
	}

	@Override
	public void setOnStart(Action action) {
		SwingUtils.bindAction(startButton, action);
	}

	@Override
	public void setOnViewVisible(ConsumerAction<Boolean> action) {
		this.viewVisibleAction = action;
	}

	@ViewPostConstruct
	private void initialize() {
		addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(AncestorEvent event) {
				executeAction(viewVisibleAction, true);
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				executeAction(viewVisibleAction, false);
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}
}
