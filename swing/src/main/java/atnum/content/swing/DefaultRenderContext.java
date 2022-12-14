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

import atnum.content.core.render.RenderContext;
import atnum.content.core.render.RenderService;
import atnum.content.core.view.ViewType;
import atnum.content.swing.renderer.ArrowRenderer;
import atnum.content.swing.renderer.EllipseRenderer;
import atnum.content.swing.renderer.GridRenderer;
import atnum.content.swing.renderer.LineRenderer;
import atnum.content.swing.renderer.PointerRenderer;
import atnum.content.swing.renderer.RectangleRenderer;
import atnum.content.swing.renderer.SelectRenderer;
import atnum.content.swing.renderer.StrokeRenderer;
import atnum.content.swing.renderer.TeXRenderer;
import atnum.content.swing.renderer.TextRenderer;
import atnum.content.swing.renderer.TextSelectionRenderer;
import atnum.content.swing.renderer.ZoomRenderer;

public class DefaultRenderContext extends RenderContext {

	public DefaultRenderContext() {
		setRenderer(ViewType.Preview, createPreviewRenderContext());
		setRenderer(ViewType.User, createUserRenderContext());
		setRenderer(ViewType.Presentation, createPresentationRenderContext());
	}
	
	private RenderService createPreviewRenderContext() {
		RenderService service = new RenderService();
		service.registerRenderer(new StrokeRenderer());
		service.registerRenderer(new TeXRenderer());
		service.registerRenderer(new TextRenderer());
		service.registerRenderer(new TextSelectionRenderer());
		service.registerRenderer(new ZoomRenderer());
		service.registerRenderer(new ArrowRenderer());
		service.registerRenderer(new LineRenderer());
		service.registerRenderer(new RectangleRenderer());
		service.registerRenderer(new EllipseRenderer());
		service.registerRenderer(new SelectRenderer());

		return service;
	}

	private RenderService createUserRenderContext() {
		RenderService service = new RenderService();
		service.registerRenderer(new StrokeRenderer());
		service.registerRenderer(new PointerRenderer());
		service.registerRenderer(new TeXRenderer());
		service.registerRenderer(new TextRenderer());
		service.registerRenderer(new TextSelectionRenderer());
		service.registerRenderer(new ZoomRenderer());
		service.registerRenderer(new ArrowRenderer());
		service.registerRenderer(new LineRenderer());
		service.registerRenderer(new RectangleRenderer());
		service.registerRenderer(new EllipseRenderer());
		service.registerRenderer(new SelectRenderer());
		service.registerRenderer(new GridRenderer());

		return service;
	}

	private RenderService createPresentationRenderContext() {
		RenderService service = new RenderService();
		service.registerRenderer(new StrokeRenderer());
		service.registerRenderer(new PointerRenderer());
		service.registerRenderer(new TeXRenderer());
		service.registerRenderer(new TextRenderer());
		service.registerRenderer(new TextSelectionRenderer());
		service.registerRenderer(new ZoomRenderer());
		service.registerRenderer(new ArrowRenderer());
		service.registerRenderer(new LineRenderer());
		service.registerRenderer(new RectangleRenderer());
		service.registerRenderer(new EllipseRenderer());
		service.registerRenderer(new SelectRenderer());
		service.registerRenderer(new GridRenderer());

		return service;
	}
	
}
