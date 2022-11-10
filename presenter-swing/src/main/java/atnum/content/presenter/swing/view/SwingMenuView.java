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

import atnum.content.core.app.dictionary.Dictionary;
import atnum.content.core.beans.BooleanProperty;
import atnum.content.core.model.Document;
import atnum.content.core.model.Page;
import atnum.content.core.model.RecentDocument;
import atnum.content.core.view.Action;
import atnum.content.core.view.ConsumerAction;
import atnum.content.core.view.PresentationParameter;
import atnum.content.presenter.api.presenter.MenuPresenter;
import atnum.content.presenter.api.view.MenuView;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import static java.util.Objects.nonNull;

@SwingView(name = "main-menu", presenter = MenuPresenter.class)
public class SwingMenuView extends JMenuBar implements MenuView {

	private final Dictionary dict;


	private ConsumerAction<File> openDocumentAction;

	private JMenu fileMenu;

	private JMenuItem openDocumentMenuItem;

	private JMenuItem closeDocumentMenuItem;

	private JMenuItem saveDocumentsMenuItem;

	private JMenuItem exitMenuItem;

	private JMenuItem undoMenuItem;

	private JMenuItem redoMenuItem;

	private JMenuItem settingsMenuItem;


	private JCheckBoxMenuItem fullscreenMenuItem;

	private JCheckBoxMenuItem advancedSettingsMenuItem;

	private JMenuItem customizeToolbarMenuItem;

	private JMenuItem newWhiteboardMenuItem;

	private JMenuItem newWhiteboardPageMenuItem;

	private JMenuItem deleteWhiteboardPageMenuItem;

	private JCheckBoxMenuItem gridMenuItem;


	private JMenuItem stopRecordingMenuItem;


	private JMenuItem logMenuItem;

	private JMenuItem aboutMenuItem;

	private JMenu timeMenu;




	@Inject
	SwingMenuView(Dictionary dict) {
		super();

		this.dict = dict;
	}

	@Override
	public void setDocument(Document doc) {
		final boolean hasDocument = nonNull(doc);
		final boolean isPdf = nonNull(doc) && doc.isPDF();
		final boolean isWhiteboard = nonNull(doc) && doc.isWhiteboard();

		// TODO: Einträge ausgrauen anzeigen
		SwingUtils.invoke(() -> {
			closeDocumentMenuItem.setEnabled(hasDocument);
			saveDocumentsMenuItem.setEnabled(hasDocument);
			customizeToolbarMenuItem.setEnabled(hasDocument);
			newWhiteboardPageMenuItem.setEnabled(isWhiteboard);
			deleteWhiteboardPageMenuItem.setEnabled(isWhiteboard);
			gridMenuItem.setEnabled(isWhiteboard);
		});
	}

	@Override
	public void setPage(Page page, PresentationParameter parameter) {
		SwingUtils.invoke(() -> {
			boolean hasUndo = false;
			boolean hasRedo = false;
			boolean hasGrid = false;

			if (nonNull(page)) {
				hasUndo = page.hasUndoActions();
				hasRedo = page.hasRedoActions();
			}
			if (nonNull(parameter)) {
				hasGrid = parameter.showGrid();
			}

			undoMenuItem.setEnabled(hasUndo);
			redoMenuItem.setEnabled(hasRedo);
			gridMenuItem.setSelected(hasGrid);
		});
	}

	@Override
	public void setRecentDocuments(List<RecentDocument> recentDocs) {
		// Remove recent document items.
		for (Component item : fileMenu.getMenuComponents()) {
			if (nonNull(item)) {
				String name = item.getName();

				if (nonNull(name) && name.equals("recent-doc")) {
					fileMenu.remove(item);
				}
			}
		}

		int offset = List.of(fileMenu.getMenuComponents()).indexOf(saveDocumentsMenuItem) + 1;

		if (!recentDocs.isEmpty()) {
			JSeparator separator = new JPopupMenu.Separator();
			separator.setName("recent-doc");

			fileMenu.add(separator, offset++);
		}

		for (RecentDocument recentDoc : recentDocs) {
			File file = new File(recentDoc.getDocumentPath());

			JMenuItem docItem = new JMenuItem(file.getName());
			docItem.setName("recent-doc");
			docItem.addActionListener(event -> {
				if (nonNull(openDocumentAction)) {
					openDocumentAction.execute(file);
				}
			});

			fileMenu.add(docItem, offset++);
		}
	}

	@Override
	public void setOnOpenDocument(Action action) {
		SwingUtils.bindAction(openDocumentMenuItem, action);
	}

	@Override
	public void setOnOpenDocument(ConsumerAction<File> action) {
		openDocumentAction = action;
	}

	@Override
	public void setOnCloseDocument(Action action) {
		SwingUtils.bindAction(closeDocumentMenuItem, action);
	}

	@Override
	public void setOnSaveDocuments(Action action) {
		SwingUtils.bindAction(saveDocumentsMenuItem, action);
	}

	@Override
	public void setOnExit(Action action) {
		SwingUtils.bindAction(exitMenuItem, action);
	}

	@Override
	public void setOnUndo(Action action) {
		SwingUtils.bindAction(undoMenuItem, action);
	}

	@Override
	public void setOnRedo(Action action) {
		SwingUtils.bindAction(redoMenuItem, action);
	}

	@Override
	public void setOnSettings(Action action) {
		SwingUtils.bindAction(settingsMenuItem, action);
	}


	@Override
	public void setAdvancedSettings(boolean selected) {
		advancedSettingsMenuItem.setSelected(selected);
	}

	@Override
	public void bindFullscreen(BooleanProperty fullscreen) {
		SwingUtils.bindBidirectional(fullscreenMenuItem, fullscreen);
	}

	@Override
	public void setOnAdvancedSettings(ConsumerAction<Boolean> action) {
		SwingUtils.bindAction(advancedSettingsMenuItem, action);
	}

	@Override
	public void setOnCustomizeToolbar(Action action) {
		SwingUtils.bindAction(customizeToolbarMenuItem, action);
	}

	@Override
	public void setOnNewWhiteboard(Action action) {
		SwingUtils.bindAction(newWhiteboardMenuItem, action);
	}

	@Override
	public void setOnNewWhiteboardPage(Action action) {
		SwingUtils.bindAction(newWhiteboardPageMenuItem, action);
	}

	@Override
	public void setOnDeleteWhiteboardPage(Action action) {
		SwingUtils.bindAction(deleteWhiteboardPageMenuItem, action);
	}

	@Override
	public void setOnShowGrid(ConsumerAction<Boolean> action) {
		SwingUtils.bindAction(gridMenuItem, action);
	}

	@Override
	public void setOnOpenLog(Action action) {
		SwingUtils.bindAction(logMenuItem, action);
	}

	@Override
	public void setOnOpenAbout(Action action) {
		SwingUtils.bindAction(aboutMenuItem, action);
	}

	@ViewPostConstruct
	private void initialize() {

	}

	@Override
	public void setCurrentTime(String time) {
		SwingUtils.invoke(() -> timeMenu.setText(time));
	}



	private void setStateText(AbstractButton button, String start, String stop) {
		button.addItemListener(e -> {
			button.setText(button.isSelected() ? stop : start);
		});
	}


}
