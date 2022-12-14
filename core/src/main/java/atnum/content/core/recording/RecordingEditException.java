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

package atnum.content.core.recording;

/**
 * Thrown to indicate that the {@code Recording} could not be changed.
 * 
 * @author Alex Andres
 */
public class RecordingEditException extends Exception {

	private static final long serialVersionUID = 4027673864137466801L;


	/**
	 * Constructs an <code>RecordingEditException</code> with no detail
	 * message.
	 */
	public RecordingEditException() {
		super();
	}

	/**
	 * Constructs an <code>RecordingEditException</code> with the specified
	 * detail message.
	 *
	 * @param message the detail message.
	 */
	public RecordingEditException(String message) {
		super(message);
	}

	/**
	 * Constructs an <code>RecordingEditException</code> with the specified
	 * cause.
	 *
	 * @param cause the cause (which is saved for later retrieval by the {@link
	 *              #getCause()} method).
	 */
	public RecordingEditException(Throwable cause) {
		super(cause);
	}
}
