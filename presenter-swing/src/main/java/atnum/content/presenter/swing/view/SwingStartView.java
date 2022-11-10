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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.model.RecentDocument;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.presenter.api.presenter.StartPresenter;
import atnum.content.presenter.api.view.StartView;
import atnum.content.swing.components.DocButton;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;

@SwingView(name = "main-start", presenter = StartPresenter.class)
public class SwingStartView extends JPanel implements StartView {

	private final ActionListener recentButtonListener = event -> {
		DocButton source = (DocButton) event.getSource();
		onOpenRecentDocument(source.getRecentDocument());
	};

	private final Dictionary dict;

	private ConsumerAction<RecentDocument> openRecentDocumentAction;

	private JButton openDocumentButton;

	private JButton openWhiteboardButton;

	private JComponent recentContainer;

	private JComponent docContainer;


	@Inject
	SwingStartView(Dictionary dictionary) {
		super();

		this.dict = dictionary;
	}

	@Override
	public void setRecentDocuments(List<RecentDocument> documents) {
		SwingUtils.invoke(() -> {
			recentContainer.setVisible(!documents.isEmpty());
			docContainer.removeAll();

			Dimension buttonSize = new Dimension(250, 50);

			for (RecentDocument doc : documents) {
				DocButton button = new DocButton(doc);
				button.setMinimumSize(buttonSize);
				button.setPreferredSize(buttonSize);
				button.setMaximumSize(buttonSize);
				button.setToolTipText(dict.get("start.open.document"));
				button.addActionListener(recentButtonListener);

				docContainer.add(button);
			}

			docContainer.revalidate();
			docContainer.repaint();
		});
	}

	@Override
	public void setOnOpenRecentDocument(ConsumerAction<RecentDocument> action) {
		this.openRecentDocumentAction = action;
	}

	@Override
	public void setOnOpenDocument(Action action) {
		SwingUtils.bindAction(openDocumentButton, action);
	}

	@Override
	public void setOnOpenWhiteboard(Action action) {
		SwingUtils.bindAction(openWhiteboardButton, action);
	}

	private void onOpenRecentDocument(RecentDocument document) {
		if (nonNull(openRecentDocumentAction)) {
			openRecentDocumentAction.execute(document);
		}
	}

}
