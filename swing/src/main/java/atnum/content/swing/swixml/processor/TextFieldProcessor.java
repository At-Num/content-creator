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

package atnum.content.swing.swixml.processor;

import static java.util.Objects.isNull;

import java.awt.LayoutManager;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import org.swixml.LogAware;
import org.swixml.Parser;
import org.swixml.processor.TagProcessor;
import org.w3c.dom.Element;

public class TextFieldProcessor implements TagProcessor, LogAware {

	@Override
	public boolean process(Parser parser, Object parent, Element child,
			LayoutManager layoutMgr) throws Exception {
		String classStr = child.getAttribute("class");

		if (isNull(classStr) || classStr.isEmpty()) {
			return false;
		}
		if (!(parent instanceof JTextField)) {
			logger.warning("Filter tag is valid only inside JTextField tag. Ignored!");
			return false;
		}

		Class<?> filterClass = Class.forName(classStr);

		final JTextField textField = (JTextField) parent;
		final DocumentFilter filter = (DocumentFilter) filterClass.getDeclaredConstructor().newInstance();
		final AbstractDocument document = (AbstractDocument) textField.getDocument();

		document.setDocumentFilter(filter);

		return true;
	}
}
