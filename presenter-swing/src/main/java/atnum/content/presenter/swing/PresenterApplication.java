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

package atnum.content.presenter.swing;

import atnum.content.core.app.ApplicationFactory;
import atnum.content.swing.app.LectSwingPreloader;
import atnum.content.swing.app.SwingApplication;

public class PresenterApplication extends SwingApplication {

	/**
	 * The entry point of the application. This method calls the static {@link
	 * #launch(String[], Class)} method to fire up the application.
	 *
	 * @param args the main method's arguments.
	 */
	public static void main(String[] args) {
		// Start with preloader.
		PresenterApplication.launch(args, LectSwingPreloader.class);
	}

	@Override
	public ApplicationFactory createApplicationFactory() {
		return new PresenterFactory();
	}
}
