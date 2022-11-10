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

package atnum.content.media.event;

import atnum.content.core.bus.event.BusEvent;
import atnum.content.core.model.Time;

public class VideoRenderEvent extends BusEvent {

	public enum State {
		RENDER_AUDIO,
		RENDER_VIDEO,
		PASS_1,
		PASS_2,
		FINISHED
	}



	private final State state;

	private Time current;

	private Time total;


	public VideoRenderEvent(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public Time getCurrentTime() {
		return current;
	}

	public void setCurrentTime(Time current) {
		this.current = current;
	}

	public Time getTotalTime() {
		return total;
	}

	public void setTotalTime(Time total) {
		this.total = total;
	}

}
