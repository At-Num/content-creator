/*
 * Copyright (C) 2021 TU Darmstadt, Department of Computer Science,
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

package atnum.content.media.webrtc;

import dev.onvoid.webrtc.media.audio.AudioProcessingStats;

import atnum.content.core.beans.Converter;

/**
 * WebRTC {@code AudioProcessingStats} converter.
 *
 * @author Alex Andres
 */
public class AudioProcessingStatsConverter implements
		Converter<AudioProcessingStats, atnum.content.core.audio.AudioProcessingStats> {

	public static final AudioProcessingStatsConverter INSTANCE = new AudioProcessingStatsConverter();


	@Override
	public atnum.content.core.audio.AudioProcessingStats to(AudioProcessingStats stats) {
		var audioStats = new atnum.content.core.audio.AudioProcessingStats();
		audioStats.delayMs = stats.delayMs;
		audioStats.voiceDetected = stats.voiceDetected;

		return audioStats;
	}

	@Override
	public AudioProcessingStats from(atnum.content.core.audio.AudioProcessingStats stats) {
		return null;
	}
}
