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

package atnum.content.core.tool;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.Page;
import atnum.content.core.recording.action.ZoomOutAction;
import atnum.content.core.view.PresentationParameter;
import atnum.content.core.view.PresentationParameterProvider;
import atnum.content.core.view.ViewType;

/**
 * PaintTool for zooming into a Page.
 *
 * @author Alex Andres
 */
public class ZoomOutTool extends SimpleTool {

	public ZoomOutTool(ToolContext context) {
		super(context);
	}

	@Override
	public void begin(PenPoint2D point, Page page) {
		resetView(ViewType.User, page);

		recordAction(new ZoomOutAction());

		resetView(ViewType.Presentation, page);
		resetView(ViewType.Preview, page);
	}

	@Override
	public ToolType getType() {
		return ToolType.ZOOM_OUT;
	}

	private void resetView(ViewType viewType, Page page) {
		PresentationParameterProvider ppp = context.getPresentationParameterProvider(viewType);
		PresentationParameter para = ppp.getParameter(page);
		para.resetPageRect();
	}
}
