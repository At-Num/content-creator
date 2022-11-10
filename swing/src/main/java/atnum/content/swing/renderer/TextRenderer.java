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
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Map;

import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.model.shape.Shape;
import atnum.content.core.model.shape.TextShape;
import atnum.content.core.text.Font;
import atnum.content.swing.converter.ColorConverter;
import atnum.content.swing.converter.FontConverter;

/**
 * Renderer for TextShapes.
 * 
 * @author Alex Andres
 * @author Tobias
 */
public class TextRenderer extends BaseRenderer {

	@Override
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

		AffineTransform t = context.getTransform();

		double sx = t.getScaleX();
		double sy = t.getScaleY();

		// Perform rendering with identity transformation.
		Rectangle2D bounds = shape.getBounds();
		double x = bounds.getX() * sx + t.getTranslateX();
		double y = bounds.getY() * sy + t.getTranslateY();

		context.setTransform(AffineTransform.getTranslateInstance(x, y));

		// Scale font.
		Font font = textShape.getFont().clone();
		font.setSize(font.getSize() * sy);

		java.awt.Font textFont = FontConverter.INSTANCE.to(font);
		Map<TextAttribute, Object> attrs = (Map<TextAttribute, Object>) textFont.getAttributes();
		attrs.put(TextAttribute.UNDERLINE, toAwtFontUnderline(textShape.isUnderline()));
		attrs.put(TextAttribute.STRIKETHROUGH, textShape.isStrikethrough());
		attrs.put(TextAttribute.FOREGROUND, ColorConverter.INSTANCE.to(textShape.getTextColor()));

		FontRenderContext frc = context.getFontRenderContext();

		// Get lines of the entire paragraph.
		String[] lines = text.split("\\n");

		float layoutX = 0;
		float layoutY = 0;

		for (String line : lines) {
			if (line.isEmpty()) {
				line = " ";
			}

			TextLayout layout = new TextLayout(line, attrs, frc);

			// Move y-coordinate by the ascent of the layout.
			layoutY += layout.getAscent();

			// Draw the TextLayout.
			layout.draw(context, layoutX, layoutY);

			// Move y-coordinate in preparation for next layout.
			layoutY += layout.getDescent() + layout.getLeading();
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
