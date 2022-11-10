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

import java.util.ArrayList;
import java.util.List;

import atnum.content.core.model.Interval;

/**
 * The MediaTrackControl base class mainly provides convenience methods for
 * specific implementations.
 *
 * @author Alex Andres
 */
public abstract class MediaTrackControlBase implements MediaTrackControl {

	private final List<Runnable> onChangeListeners = new ArrayList<>();

	private final List<Runnable> onRemoveListeners = new ArrayList<>();

	private final Interval<Double> interval = new Interval<>();

	private double start;

	private double end;


	@Override
	public void addChangeListener(Runnable listener) {
		onChangeListeners.add(listener);
	}

	@Override
	public void removeChangeListener(Runnable listener) {
		onChangeListeners.remove(listener);
	}

	@Override
	public void setStartTime(double value) {
		// Convert -0.0 to +0.0.
		if (value == 0.0) {
			value = 0.0;
		}

		if (Double.compare(this.start, value) != 0) {
			this.start = value;

			interval.set(start, end);

			fireControlChange();
		}
	}

	@Override
	public void setEndTime(double value) {
		// Convert -0.0 to +0.0.
		if (value == 0.0) {
			value = 0.0;
		}

		if (Double.compare(this.end, value) != 0) {
			this.end = value;

			interval.set(start, end);

			fireControlChange();
		}
	}

	@Override
	public Interval<Double> getInterval() {
		return interval;
	}

	/**
	 * Adds a listener that is notified when this media track control should be
	 * removed from further processing.
	 *
	 * @param listener The listener to add.
	 */
	public void addRemoveListener(Runnable listener) {
		onRemoveListeners.add(listener);
	}

	/**
	 * Indicates that this control should be removed from the media track and
	 * its further processing.
	 */
	public void remove() {
		for (Runnable listener : onRemoveListeners) {
			listener.run();
		}
	}

	/**
	 * Notify listeners that a change within this control has occurred.
	 */
	protected void fireControlChange() {
		for (Runnable listener : onChangeListeners) {
			listener.run();
		}
	}
}
