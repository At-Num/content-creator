/*
 * Copyright (C) 2022 TU Darmstadt, Department of Computer Science,
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

package atnum.content.core.model;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.pdf.pdfbox.PDFGraphics2D;

public class ScreenDocument extends Document {

	public ScreenDocument() throws IOException {
		super();

		setDocumentType(DocumentType.SCREEN);
	}

	public ScreenDocument(byte[] byteArray) throws IOException {
		super(byteArray);

		setDocumentType(DocumentType.SCREEN);
	}

	public Page createPage(BufferedImage image) {
		Page page = createPage();
		int pageIndex = page.getPageNumber();

		Rectangle2D rect = page.getPageRect();

		double s = rect.getWidth() / (double) image.getWidth(null);

		if (image.getHeight(null) > image.getWidth(null)) {
			s = rect.getHeight() / (double) image.getHeight(null);
		}

		double sInv = 1 / s;

		int x = (int) ((rect.getWidth() - image.getWidth(null) * s) / 2 * sInv);
		int y = (int) ((rect.getHeight() - image.getHeight(null) * s) / 2 * sInv);
		int w = (int) (rect.getWidth() * sInv);
		int h = (int) (rect.getHeight() * sInv);

		try {
			getPdfDocument().setPageContentTransform(pageIndex, AffineTransform.getScaleInstance(s, s));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		PDFGraphics2D g2d = (PDFGraphics2D) getPdfDocument().createAppendablePageGraphics2D(pageIndex);
		// Draw screen frame onto a black page background.
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, w, h);
		g2d.drawImage(image, x, y, null);
		g2d.close();
		g2d.dispose();

		return page;
	}
}
