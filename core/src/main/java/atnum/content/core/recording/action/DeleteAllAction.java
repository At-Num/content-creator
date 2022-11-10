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
import java.nio.ByteBuffer;

import atnum.content.core.controller.ToolController;

public class DeleteAllAction extends PlaybackAction {

	public DeleteAllAction() {
		
	}

	public DeleteAllAction(byte[] input) throws IOException {
		parseFrom(input);
	}

	@Override
	public void execute(ToolController controller) throws Exception {
		controller.selectDeleteAllTool();
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteBuffer buffer = createBuffer(0);

		return buffer.array();
	}

	@Override
	public void parseFrom(byte[] input) throws IOException {
		createBuffer(input);
	}

	@Override
	public ActionType getType() {
		return ActionType.DELETE_ALL;
	}

}
