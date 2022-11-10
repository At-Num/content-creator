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

package atnum.content.core.presenter;

import javax.inject.Inject;

import atnum.content.core.app.ApplicationContext;
import atnum.content.core.geometry.Position;
import atnum.content.core.view.NotificationPopupView;
import atnum.content.core.view.NotificationType;
import atnum.content.core.view.ViewLayer;

public class NotificationPopupPresenter extends Presenter<NotificationPopupView> {

	@Inject
	NotificationPopupPresenter(ApplicationContext context, NotificationPopupView view) {
		super(context, view);
	}

	public void setNotificationType(NotificationType type) {
		view.setType(type);
	}

	public void setTitle(String title) {
		view.setTitle(title);
	}

	public void setMessage(String message) {
		view.setMessage(message);
	}

	public void setPosition(Position position) {
		view.setPosition(position);
	}

	@Override
	public ViewLayer getViewLayer() {
		return ViewLayer.NotificationPopup;
	}
}
