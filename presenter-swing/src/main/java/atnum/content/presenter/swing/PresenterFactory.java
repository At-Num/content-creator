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

import com.google.inject.Module;

import atnum.content.core.app.ApplicationContext;
import atnum.content.core.app.ApplicationFactory;
import atnum.content.core.inject.GuiceInjector;
import atnum.content.core.inject.Injector;
import atnum.content.presenter.api.presenter.MainPresenter;
import atnum.content.presenter.swing.inject.guice.ApplicationModule;
import atnum.content.presenter.swing.inject.guice.ViewModule;
import atnum.content.swing.guice.XmlViewModule;

public class PresenterFactory implements ApplicationFactory {

	private final Injector injector;


	public PresenterFactory() {
		Module appModule = new ApplicationModule();
		Module viewModule = new ViewModule();
		Module xmlViewModule = XmlViewModule.create(viewModule);

		injector = new GuiceInjector(appModule, viewModule, xmlViewModule);
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return injector.getInstance(ApplicationContext.class);
	}

	@Override
	public atnum.content.core.presenter.MainPresenter<?> getStartPresenter() {
		return injector.getInstance(MainPresenter.class);
	}
}
