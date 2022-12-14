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

package atnum.content.core.model.listener;

import atnum.content.core.model.Document;
import atnum.content.core.model.Page;

public interface DocumentChangeListener {

	/**
	 * Fired when the document has changed.
	 *
	 * @param document The document.
	 */
	void documentChanged(Document document);

	/**
	 * Fired when a page has been added.
	 *
	 * @param page The page.
	 */
	void pageAdded(Page page);

	/**
	 * Fired when a page has been removed.
	 *
	 * @param page The page.
	 */
	void pageRemoved(Page page);
	
}
