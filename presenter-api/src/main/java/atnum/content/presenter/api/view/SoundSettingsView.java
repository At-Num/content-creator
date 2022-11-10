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

package atnum.content.presenter.api.view;

import atnum.content.core.audio.AudioProcessingSettings.NoiseSuppressionLevel;
import atnum.content.core.audio.device.AudioDevice;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.beans.DoubleProperty;
import atnum.content.core.beans.FloatProperty;
import atnum.content.core.beans.ObjectProperty;
import atnum.content.core.beans.StringProperty;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;

public interface SoundSettingsView extends SettingsBaseView {

	void setViewEnabled(boolean enabled);

	void setAudioCaptureDevice(StringProperty captureDeviceName);

	void setAudioCaptureDevices(AudioDevice[] captureDevices);

	void setAudioPlaybackDevice(StringProperty playbackDeviceName);

	void setAudioPlaybackDevices(AudioDevice[] playbackDevices);

	void setAudioCaptureNoiseSuppressionLevel(ObjectProperty<NoiseSuppressionLevel> level);

	void setAudioCaptureLevel(double value);

	void bindAudioCaptureLevel(FloatProperty levelProperty);

	void bindAudioPlaybackLevel(DoubleProperty levelProperty);

	void bindTestCaptureEnabled(BooleanProperty enable);

	void bindTestPlaybackEnabled(BooleanProperty enable);

	void setOnTestCapture(BooleanProperty recordProperty);

	void setOnTestCapturePlayback(BooleanProperty playProperty);

	void setOnTestSpeakerPlayback(BooleanProperty playProperty);

	void setOnAdjustAudioCaptureLevel(Action action);

	void setOnViewVisible(ConsumerAction<Boolean> action);

}
