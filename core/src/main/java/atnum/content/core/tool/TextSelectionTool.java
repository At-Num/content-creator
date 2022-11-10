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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.model.Page;
import atnum.content.core.model.action.CreateShapeAction;
import atnum.content.core.model.shape.TextSelectionShape;
import atnum.content.core.recording.action.TextSelectionExtAction;

public class TextSelectionTool extends Tool {

	private Integer shapeHandle;

	private List<Rectangle2D> textBoxes;

	private Page page;

	private PenPoint2D startPoint;

	private TextSelectionShape shape;


	public TextSelectionTool(ToolContext context) {
		super(context);
	}

	public TextSelectionTool(ToolContext context, List<Rectangle2D> textBoxes,
			Integer shapeHandle) {
		super(context);

		this.textBoxes = textBoxes;
		this.shapeHandle = shapeHandle;
	}

	@Override
	public void begin(PenPoint2D point, Page page) {
		this.startPoint = point;
		this.page = page;

		if (isNull(textBoxes)) {
			textBoxes = page.getTextPositions();
		}

		if (nonNull(shapeHandle)) {
			shape = (TextSelectionShape) page.getShape(shapeHandle);
		}
		if (isNull(shape)) {
			shape = createShape();

			if (nonNull(shapeHandle)) {
				shape.setHandle(shapeHandle);
			}
		}

		addSelection(point);
	}

	@Override
	public void execute(PenPoint2D point) {
		if (isNull(textBoxes)) {
			return;
		}

		addSelection(point);
	}

	@Override
	public void end(PenPoint2D point) {
		if (nonNull(shape) && !shape.hasSelection()) {
			page.removeShape(shape);
		}
		else {
			firePaintEvent(ToolEventType.END);
		}

		shape = null;
		textBoxes = null;
	}

	@Override
	public ToolType getType() {
		return ToolType.TEXT_SELECTION;
	}

	private TextSelectionExtAction createPlaybackAction() {
		TextSelectionSettings settings = context.getPaintSettings(getType());

		return new TextSelectionExtAction(shape.getHandle(),
				settings.getColor().derive(settings.getAlpha()));
	}

	private TextSelectionShape createShape() {
		TextSelectionSettings settings = context.getPaintSettings(getType());

		return new TextSelectionShape(settings.getColor().derive(settings.getAlpha()));
	}

	private void addSelection(PenPoint2D point) {
		// Execute only towards x-direction.
		PenPoint2D xPoint = point.clone();
		xPoint.set(point.getX(), startPoint.getY());

		for (Rectangle2D rect : textBoxes) {
			if (!rect.contains(xPoint)) {
				continue;
			}
			if (checkCollision(xPoint)) {
				// Try next text part.
				continue;
			}

			// Create an action only if there is at least one character selection.
			if (!shape.hasSelection()) {
				page.addAction(new CreateShapeAction(page, shape));

				firePaintEvent(ToolEventType.BEGIN);
			}

			if (!shape.contains(xPoint)) {
				shape.addPoint(point.clone());
				shape.addSelection(rect);

				TextSelectionExtAction action = createPlaybackAction();

				if (action.addSelection(rect)) {
					recordAction(action);
				}

				firePaintEvent(ToolEventType.EXECUTE);
			}

			break;
		}
	}

	private boolean checkCollision(PenPoint2D point) {
		// Get all selection shapes on the page and check for collisions.
		var equalShapes = page.getShapes(TextSelectionShape.class);

		for (var s : equalShapes) {
			TextSelectionShape other = (TextSelectionShape) s;

			if (shape != other && other.contains(point)) {
				// Other shape already marked this part.
				return true;
			}
		}

		return false;
	}

	private void firePaintEvent(ToolEventType type) {
		fireToolEvent(new ShapePaintEvent(type, shape, nonNull(shape) ? shape.getBounds() : null));
	}
}
