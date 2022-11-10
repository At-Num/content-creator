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

package atnum.content.presenter.api.view;

import java.util.List;

import atnum.content.core.controller.RenderController;
import atnum.content.core.geometry.Matrix;
import atnum.content.core.input.KeyEvent;
import atnum.content.core.model.Document;
import atnum.content.core.model.DocumentOutlineItem;
import atnum.content.core.model.Page;
import atnum.content.core.view.*;
import atnum.content.presenter.api.config.SlideViewConfiguration;
import atnum.content.presenter.api.stylus.StylusHandler;

public interface SlidesView extends View {

	void setSlideViewConfig(SlideViewConfiguration viewState);

	void addPageObjectView(PageObjectView<?> objectView);

	void removePageObjectView(PageObjectView<?> objectView);

	void removeAllPageObjectViews();

	List<PageObjectView<?>> getPageObjectViews();

	void addDocument(Document doc, PresentationParameterProvider ppProvider);

	void removeDocument(Document doc);

	void selectDocument(Document doc, PresentationParameterProvider ppProvider);

	Page getPage();

	void setPage(Page page, PresentationParameter parameter);

	void setPageRenderer(RenderController pageRenderer);

	void setExtendedFullscreen(boolean extended);

	void setStylusHandler(StylusHandler handler);

	void setLaTeXText(String text);

	void setOnKeyEvent(ConsumerAction<KeyEvent> action);

	void setOnSelectDocument(ConsumerAction<Document> action);

	void setOnSelectPage(ConsumerAction<Page> action);

	void setOnViewTransform(ConsumerAction<Matrix> action);

	void setOnNewPage(Action action);

	void setOnDeletePage(Action action);
	void setOnOutlineItem(ConsumerAction<DocumentOutlineItem> action);



}
