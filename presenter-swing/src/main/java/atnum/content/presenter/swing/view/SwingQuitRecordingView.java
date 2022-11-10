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

import javax.swing.JButton;
import javax.swing.JPanel;

import atnum.content.core.view.Action;
import atnum.content.presenter.api.view.QuitRecordingView;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;

@SwingView(name = "quit-recording")
public class SwingQuitRecordingView extends JPanel implements QuitRecordingView {

	private JButton abortButton;

	private JButton discardButton;

	private JButton saveButton;


	SwingQuitRecordingView() {
		super();
	}

	@Override
	public void setOnAbort(Action action) {
		SwingUtils.bindAction(abortButton, action);
	}

	@Override
	public void setOnDiscardRecording(Action action) {
		SwingUtils.bindAction(discardButton, action);
	}

	@Override
	public void setOnSaveRecording(Action action) {
		SwingUtils.bindAction(saveButton, action);
	}

}
