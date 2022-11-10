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

package atnum.content.core.recording.action;

import java.io.IOException;

import atnum.content.core.controller.ToolController;
import atnum.content.core.input.KeyEvent;
import atnum.content.core.tool.LineTool;
import atnum.content.core.tool.Stroke;
import atnum.content.core.tool.StrokeSettings;
import atnum.content.core.tool.ToolType;

public class LineAction extends BaseStrokeAction {

	public LineAction(int shapeHandle, Stroke stroke, KeyEvent keyEvent) {
		super(shapeHandle, stroke, keyEvent);
	}

	public LineAction(byte[] input) throws IOException {
		super(input);
	}

	@Override
	public void execute(ToolController controller) throws Exception {
		StrokeSettings settings = controller.getPaintSettings(ToolType.LINE);
		settings.setColor(stroke.getColor());
		settings.setWidth(stroke.getWidth());

		controller.setTool(new LineTool(controller, shapeHandle));
		controller.setKeyEvent(getKeyEvent());
	}

	@Override
	public ActionType getType() {
		return ActionType.LINE;
	}

}
