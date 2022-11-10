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

package atnum.content.swing.renderer;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import atnum.content.core.model.shape.Shape;
import atnum.content.core.model.shape.TextShape;
import atnum.content.core.text.Font;
import atnum.content.swing.converter.FontConverter;

/**
 * Render for rendering a TextShape.
 * 
 * @author Alex Andres
 */
public class PDFTextRenderer extends BaseRenderer {

	public Class<? extends Shape> forClass() {
		return TextShape.class;
	}

	@Override
	protected void renderPrivate(Shape shape, Graphics2D context) {
		TextShape textShape = (TextShape) shape;
		String text = textShape.getText();

		if (text.isEmpty()) {
			return;
		}

		Font font = textShape.getFont();
		
		float fontSize = (float) (font.getSize());
		float x = (float) (textShape.getLocation().getX());
		float y = (float) (textShape.getLocation().getY());

		Map<TextAttribute, Object> attrs = new HashMap<>();
		attrs.put(TextAttribute.FAMILY, font.getFamilyName());
		attrs.put(TextAttribute.SIZE, fontSize);
		attrs.put(TextAttribute.POSTURE, FontConverter.toAwtFontPosture(font.getPosture()));
		attrs.put(TextAttribute.WEIGHT, FontConverter.toAwtFontWeight(font.getWeight()));
		attrs.put(TextAttribute.UNDERLINE, toAwtFontUnderline(textShape.isUnderline()));
		attrs.put(TextAttribute.STRIKETHROUGH, textShape.isStrikethrough());
			
		context.setFont(new java.awt.Font(attrs));
		context.setPaint(new java.awt.Color(textShape.getTextColor().getRGBA(), true));

		double sy = Math.abs(context.getTransform().getScaleY());
		
		java.awt.Font awtFont = context.getFont().deriveFont((float) (fontSize * sy));
		LineMetrics metrics = awtFont.getLineMetrics(text, new FontRenderContext(context.getTransform(), true, true));
		
		String[] lines = text.split("\n");
		
		for (String line : lines) {
			y += (metrics.getAscent() + metrics.getLeading()) / sy;
			
			context.drawString(line, x, y);
			
			y += (metrics.getDescent()) / sy + 0;
		}
	}

	public static Number toAwtFontUnderline(boolean underline) {
		if (underline) {
			return TextAttribute.UNDERLINE_ON;
		}
		else {
			return -1;
		}
	}
}
