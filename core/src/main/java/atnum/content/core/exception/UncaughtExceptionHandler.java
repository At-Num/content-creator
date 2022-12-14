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

package atnum.content.core.exception;

import org.apache.logging.log4j.Logger;

/**
 * {@link UncaughtExceptionHandler} implementation for catching exceptions when a {@link Thread}
 * abruptly terminates due to an uncaught exception.
 *
 * @author Alex Andres
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	/** The logger that will log the exceptions. */
	private final Logger LOGGER;


	/**
	 * Create an {@link UncaughtExceptionHandler} with the specified logger that will log the exceptions.
	 *
	 * @param logger The logger that will log the exceptions.
	 */
	public UncaughtExceptionHandler(Logger logger) {
		this.LOGGER = logger;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LOGGER.error("Crashed thread: " + thread.getName(), throwable);
	}

}
