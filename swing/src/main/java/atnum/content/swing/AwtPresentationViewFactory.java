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

package atnum.content.swing;

import atnum.content.core.app.ApplicationContext;
import atnum.content.core.controller.RenderController;
import atnum.content.core.presenter.PresentationPresenter;
import atnum.content.core.presenter.SlidePresentationPresenter;
import atnum.content.core.view.PresentationView;
import atnum.content.core.view.PresentationViewFactory;
import atnum.content.core.view.Screen;
import atnum.content.core.view.SlidePresentationView;
import atnum.content.swing.window.PresentationWindow;

public class AwtPresentationViewFactory implements PresentationViewFactory {

	@Override
	public PresentationPresenter<? extends PresentationView> createPresentationView(ApplicationContext context, Screen screen) {
		RenderController renderController = new RenderController(context, new DefaultRenderContext());
		SlidePresentationView view = new PresentationWindow(context, screen, renderController);

		return new SlidePresentationPresenter(context, view, context.getDocumentService());
	}
}
