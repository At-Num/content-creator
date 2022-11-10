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

import static java.util.Objects.nonNull;

import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import atnum.content.core.view.Action;
import atnum.content.presenter.api.view.SaveDocumentOptionView;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;

@SwingView(name = "save-documents-option")
public class SwingSaveDocumentOptionView extends JPanel implements SaveDocumentOptionView {

	private JCheckBox documentCheckBox;

	private JButton saveDocumentButton;

	private Action selectAction;

	private Action deselectAction;


	SwingSaveDocumentOptionView() {
		super();
	}

	@Override
	public void select() {
		documentCheckBox.setSelected(true);
	}

	@Override
	public void deselect() {
		documentCheckBox.setSelected(false);
	}

	@Override
	public String getDocumentTitle() {
		return documentCheckBox.getText();
	}

	@Override
	public void setDocumentTitle(String docTitle) {
		documentCheckBox.setText(docTitle);
	}

	@Override
	public void setOnSaveDocument(Action action) {
		SwingUtils.bindAction(saveDocumentButton, action);
	}

	@Override
	public void setOnSelectDocument(Action action) {
		selectAction = Action.concatenate(selectAction, action);
	}

	@Override
	public void setOnDeselectDocument(Action action) {
		deselectAction = Action.concatenate(deselectAction, action);
	}

	@ViewPostConstruct
	private void initialize() {
		documentCheckBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (nonNull(selectAction)) {
					selectAction.execute();
				}
			}
			else {
				if (nonNull(deselectAction)) {
					deselectAction.execute();
				}
			}
		});
	}

}
