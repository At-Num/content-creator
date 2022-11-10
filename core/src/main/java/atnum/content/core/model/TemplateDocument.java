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

import java.io.File;
import java.io.IOException;

import atnum.content.core.pdf.PdfDocument;

public class TemplateDocument extends Document {

	private final Document templateDoc;


	public TemplateDocument(File file) throws IOException {
		super(new PdfDocument());

		templateDoc = new Document(file);
	}

	public TemplateDocument(byte[] byteArray) throws IOException {
		super(new PdfDocument());

		templateDoc = new Document(byteArray);
	}

	@Override
	public Page createPage() {
		Page page;

		try {
			page = super.createPage(templateDoc.getCurrentPage());
		}
		catch (IOException e) {
			LOG.error("Create template page failed", e);

			page = super.createPage();
		}

		return page;
	}
}
