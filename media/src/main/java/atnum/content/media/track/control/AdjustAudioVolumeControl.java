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

package atnum.content.media.track.control;

import atnum.content.core.audio.filter.AudioVolumeFilter;

/**
 * This media track control increases or decreases the audio sample values based
 * on the provided scalar value.
 *
 * @author Alex Andres
 */
public class AdjustAudioVolumeControl extends AudioFilterControl<AudioVolumeFilter> {

	private double scalar = 1;


	public AdjustAudioVolumeControl() {
		super(new AudioVolumeFilter());
	}

	/**
	 * @return the scalar value.
	 */
	public double getVolumeScalar() {
		return scalar;
	}

	/**
	 * Set the new scalar value. A value of 1 causes no effect on the processed
	 * samples.
	 *
	 * @param scalar The new scalar.
	 */
	public void setVolumeScalar(double scalar) {
		this.scalar = scalar;

		getAudioFilter().setVolumeScalar(scalar);

		fireControlChange();
	}
}
