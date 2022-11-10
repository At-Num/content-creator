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

package atnum.content.presenter.swing.view;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;

import atnum.content.core.beans.IntegerProperty;
import atnum.content.core.view.Action;
import atnum.content.presenter.api.view.SaveDocumentOptionView;
import atnum.content.presenter.api.view.SaveDocumentsView;
import atnum.content.swing.components.ContentPane;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;

@SwingView(name = "save-documents")
public class SwingSaveDocumentsView extends ContentPane implements SaveDocumentsView {

	private final IntegerProperty anySelected;

	private Container individualContainer;

	private JButton closeButton;

	SwingSaveDocumentsView() {
		super();

		anySelected = new IntegerProperty();
	}

	@Override
	public void addDocumentOptionView(SaveDocumentOptionView optionView) {
		if (!SwingUtils.isComponent(optionView)) {
			throw new RuntimeException("View expected to be a Component");
		}

		optionView.setOnSelectDocument(() -> {
			anySelected.set(anySelected.get() + 1);
		});
		optionView.setOnDeselectDocument(() -> {
			anySelected.set(anySelected.get() - 1);
		});

		SwingUtils.invoke(() -> {
			individualContainer.add((Component) optionView);
			individualContainer.revalidate();
		});
	}

	@Override
	public void setOnClose(Action action) {
		SwingUtils.bindAction(closeButton, action);
	}

}
