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

import java.util.ArrayList;
import java.util.List;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.Page;
import atnum.content.core.model.action.DeleteShapeAction;
import atnum.content.core.model.shape.Shape;
import atnum.content.core.recording.action.RubberActionExt;

/**
 * PaintTool that deletes paintings near invocation positions.
 *
 * @author Alex Andres
 * @author Tobias
 */
public class RubberTool extends Tool {

	private Page page;


	public RubberTool(ToolContext context) {
		super(context);
	}

	@Override
	public void begin(PenPoint2D point, Page page) {
		this.page = page;
	}

	@Override
	public void execute(PenPoint2D point) {
		List<Shape> toDelete = new ArrayList<>();

		for (Shape shape : page.getShapes()) {
			if (shape.contains(point)) {
				recordAction(new RubberActionExt(shape.getHandle()));

				toDelete.add(shape);
			}
		}

		if (!toDelete.isEmpty()) {
			page.addAction(new DeleteShapeAction(page, toDelete));

			fireToolEvent(new ShapeModifyEvent(toDelete));
		}
	}

	@Override
	public void end(PenPoint2D point) {
		// Do nothing on purpose.
	}

	@Override
	public ToolType getType() {
		return ToolType.RUBBER;
	}

}
