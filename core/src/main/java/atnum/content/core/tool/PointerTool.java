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

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.Page;
import atnum.content.core.model.shape.PointerShape;
import atnum.content.core.recording.action.PlaybackAction;
import atnum.content.core.recording.action.PointerAction;

public class PointerTool extends StrokeTool<PointerShape> {

	private Page page;


	public PointerTool(ToolContext context) {
		super(context, null);
	}

	@Override
	public ToolType getType() {
		return ToolType.POINTER;
	}

	@Override
	protected void beginInternal(PenPoint2D point, Page page) {
		this.page = page;

		shape.setPoint(point);

		page.addShape(shape);
	}

	@Override
	protected void executeInternal(PenPoint2D point) {
		shape.setPoint(point);
	}

	@Override
	protected void endInternal(PenPoint2D point) {
		page.removeShape(shape);
	}

	@Override
	protected PlaybackAction createPlaybackAction() {
		return new PointerAction(createStroke(), context.getKeyEvent());
	}

	@Override
	protected PointerShape createShape() {
		Stroke stroke = createStroke();
		stroke.scale(3);
		stroke.scale(context.getPageScale());

		return new PointerShape(stroke);
	}
}