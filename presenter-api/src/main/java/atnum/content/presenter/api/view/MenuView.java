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

import java.io.File;
import java.util.List;

import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.model.Document;
import atnum.content.core.model.Page;
import atnum.content.core.model.RecentDocument;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.core.view.PresentationParameter;
import atnum.content.core.view.View;

public interface MenuView extends View {

	void setDocument(Document doc);

	void setPage(Page page, PresentationParameter parameter);

	/**
	 * File Menu
	 */

	void setRecentDocuments(List<RecentDocument> recentDocs);

	void setOnOpenDocument(Action action);

	void setOnOpenDocument(ConsumerAction<File> action);

	void setOnCloseDocument(Action action);

	void setOnSaveDocuments(Action action);

	void setOnExit(Action action);

	/**
	 * Edit Menu
	 */

	void setOnUndo(Action action);

	void setOnRedo(Action action);

	void setOnSettings(Action action);


	void setAdvancedSettings(boolean selected);

	void bindFullscreen(BooleanProperty fullscreen);

	void setOnAdvancedSettings(ConsumerAction<Boolean> action);

	void setOnCustomizeToolbar(Action action);

	/**
	 * Whiteboard Menu
	 */
	void setOnNewWhiteboard(Action action);

	void setOnNewWhiteboardPage(Action action);

	void setOnDeleteWhiteboardPage(Action action);

	void setOnShowGrid(ConsumerAction<Boolean> action);


	/**
	 * Info Menu
	 */

	void setOnOpenLog(Action action);

	void setOnOpenAbout(Action action);



	void setCurrentTime(String time);


}
