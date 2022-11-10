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

package atnum.content.core.controller;

import atnum.content.core.ExecutableBase;
import atnum.content.core.ExecutableException;
import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.configuration.Configuration;
import atnum.content.core.app.dictionary.Dictionary;

/**
 * An executable controller base implementation that provides general objects
 * that are required during the controller's life-time.
 *
 * @author Alex Andres
 */
public abstract class Controller extends ExecutableBase {

	/** The application context. */
	private final ApplicationContext context;


	/**
	 * Create a {@link Controller} with the specified application context.
	 *
	 * @param context The application context.
	 */
	public Controller(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Get the application context.
	 *
	 * @return the application context.
	 */
	protected ApplicationContext getContext() {
		return context;
	}

	/**
	 * Get the configuration of the application.
	 *
	 * @return the configuration of the application.
	 */
	protected Configuration getConfig() {
		return context.getConfiguration();
	}

	/**
	 * Get the dictionary of the application.
	 *
	 * @return the dictionary of the application.
	 */
	protected Dictionary getDictionary() {
		return context.getDictionary();
	}

	@Override
	protected void initInternal() throws ExecutableException {

	}

	@Override
	protected void startInternal() throws ExecutableException {

	}

	@Override
	protected void stopInternal() throws ExecutableException {

	}

	@Override
	protected void destroyInternal() throws ExecutableException {

	}

}
