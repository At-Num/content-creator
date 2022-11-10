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

import atnum.content.core.input.KeyEvent;
import atnum.content.core.model.shape.ArrowShape;
import atnum.content.core.model.shape.FormShape;
import atnum.content.core.recording.action.ArrowAction;
import atnum.content.core.recording.action.PlaybackAction;

/**
 * PaintTool for creating a line on a Page.
 *
 * @author Alex Andres
 */
public class ArrowTool extends FormTool {

	public ArrowTool(ToolContext context) {
		super(context);
	}

	public ArrowTool(ToolContext context, Integer shapeHandle) {
		super(context, shapeHandle);
	}

	@Override
	public ToolType getType() {
		return ToolType.ARROW;
	}

	@Override
	public boolean supportsKeyEvent(KeyEvent event) {
		if (isNull(event)) {
			return false;
		}

		boolean bold = event.isAltDown();
		boolean twoSided = event.isShiftDown();

		return bold || twoSided;
	}

	@Override
	protected FormShape createShape() {
		return new ArrowShape(createStroke());
	}

	@Override
	protected PlaybackAction createPlaybackAction() {
		return new ArrowAction(shape.getHandle(), createStroke(),
				context.getKeyEvent());
	}
}
