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

import java.util.function.Predicate;

import atnum.content.core.input.KeyEvent;
import atnum.content.core.geometry.Rectangle2D;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.core.view.View;
import atnum.content.core.view.ViewLayer;

public interface MainView extends View {

	Rectangle2D getViewBounds();

	void closeView();

	void hideView();

	void removeView(View view, ViewLayer layer);

	void showView(View view, ViewLayer layer);

	void setFullscreen(boolean fullscreen);

	void setMenuVisible(boolean visible);

	void setOnKeyEvent(Predicate<KeyEvent> action);

	void setOnBounds(ConsumerAction<Rectangle2D> action);

	void setOnShown(Action action);

	void setOnClose(Action action);

}
