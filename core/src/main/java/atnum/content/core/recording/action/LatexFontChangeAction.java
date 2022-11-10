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
import atnum.content.core.graphics.Color;
import atnum.content.core.text.TeXFont;

public class LatexFontChangeAction extends PlaybackAction {

	private int handle;

	private Color textColor;

	private TeXFont textFont;


	public LatexFontChangeAction(int handle, Color color, TeXFont font) {
		this.handle = handle;
		this.textColor = color;
		this.textFont = font;
	}

	public LatexFontChangeAction(byte[] input) throws IOException {
		parseFrom(input);
	}

	@Override
	public void execute(ToolController controller) throws Exception {
		controller.setTeXFont(handle, textColor, textFont);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		int payloadBytes = 4 + 12;

		ByteBuffer buffer = createBuffer(payloadBytes);

		// Shape handle.
		buffer.putInt(handle);

		// Text attributes: 12 bytes.
		buffer.putInt(textFont.getType().getValue());
		buffer.putFloat(textFont.getSize());
		buffer.putInt(textColor.getRGBA());

		return buffer.array();
	}

	@Override
	public void parseFrom(byte[] input) throws IOException {
		ByteBuffer buffer = createBuffer(input);

		handle = buffer.getInt();

		// Text attributes
		int textType = buffer.getInt();
		float textSize = buffer.getFloat();

		textFont = new TeXFont(TeXFont.Type.fromValue(textType), textSize);
		textColor = new Color(buffer.getInt());
	}

	@Override
	public ActionType getType() {
		return ActionType.LATEX_FONT_CHANGE;
	}

}
