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

import java.util.List;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.Page;
import atnum.content.core.model.action.DeleteShapeAction;
import atnum.content.core.model.shape.Shape;
import atnum.content.core.recording.action.DeleteAllAction;

/**
 * Tool that removes all annotations on the page.
 *
 * @author Alex Andres
 * @author Tobias
 */
public class DeleteAllTool extends SimpleTool {

	public DeleteAllTool(ToolContext context) {
		super(context);
	}

	@Override
	public void begin(PenPoint2D point, Page page) {
		List<Shape> shapes = page.getShapes();

		recordAction(new DeleteAllAction());

		page.addAction(new DeleteShapeAction(page, shapes));

		fireToolEvent(new ShapeModifyEvent(shapes));
	}

	@Override
	public ToolType getType() {
		return ToolType.DELETE_ALL;
	}

}
