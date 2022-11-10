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

package atnum.content.core.tool;

import atnum.content.core.geometry.Dimension2D;
import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.model.Page;
import atnum.content.core.model.shape.ZoomShape;
import atnum.content.core.recording.action.PlaybackAction;
import atnum.content.core.recording.action.ZoomAction;
import atnum.content.core.view.PresentationParameter;
import atnum.content.core.view.PresentationParameterProvider;
import atnum.content.core.view.ViewType;

/**
 * Tool for zooming into a Page.
 *
 * @author Alex Andres
 */
public class ZoomTool extends StrokeTool<ZoomShape> {

	private Page page;


	public ZoomTool(ToolContext context) {
		super(context, null);
	}

	@Override
	public ToolType getType() {
		return ToolType.ZOOM;
	}

	@Override
	protected void beginInternal(PenPoint2D point, Page page) {
		this.page = page;

		Rectangle2D pageBounds = page.getPageRect();

		shape.setRatio(new Dimension2D(pageBounds.getWidth(), pageBounds.getHeight()));
		shape.setStartPoint(point.clone());

		page.addShape(shape);
	}

	@Override
	protected void executeInternal(PenPoint2D point) {
		shape.setEndPoint(point.clone());
	}

	@Override
	protected void endInternal(PenPoint2D point) {
		shape.setEndPoint(point.clone());

		page.removeShape(shape);

		zoom(shape.getBounds());
	}

	@Override
	protected PlaybackAction createPlaybackAction() {
		return new ZoomAction(createStroke(), context.getKeyEvent());
	}

	@Override
	protected ZoomShape createShape() {
		return new ZoomShape(createStroke());
	}

	@Override
	protected Stroke createStroke() {
		Stroke stroke = new Stroke();
		stroke.setWidth(0.002);
		stroke.scale(context.getPageScale());

		return stroke;
	}

	private void zoom(Rectangle2D rect) {
		// Zoom only on user- and presentation view.
		zoomView(ViewType.User, rect);
		zoomView(ViewType.Presentation, rect);
	}

	private void zoomView(ViewType viewType, Rectangle2D rect) {
		PresentationParameterProvider ppp = context.getPresentationParameterProvider(viewType);
		PresentationParameter para = ppp.getParameter(page);
		para.zoom(rect);
	}
}
