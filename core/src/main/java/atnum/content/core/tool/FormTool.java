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
import atnum.content.core.model.action.CreateShapeAction;
import atnum.content.core.model.shape.FormShape;

public abstract class FormTool extends StrokeTool<FormShape> {

	public FormTool(ToolContext context) {
		super(context);
	}

	public FormTool(ToolContext context, Integer shapeHandle) {
		super(context, shapeHandle);
	}

	@Override
	protected void beginInternal(PenPoint2D point, Page page) {
		shape.setStartPoint(point.clone());

		page.addAction(new CreateShapeAction(page, shape));
	}

	@Override
	protected void executeInternal(PenPoint2D point) {
		shape.setKeyEvent(getKeyEvent());
		shape.setEndPoint(point.clone());
	}

	@Override
	protected void endInternal(PenPoint2D point) {
		shape.setEndPoint(point.clone());
	}
}
