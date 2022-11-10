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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.*;

import atnum.content.core.view.AboutView;
import atnum.content.core.view.Action;
import atnum.content.swing.components.ContentPane;
import atnum.content.swing.util.SwingUtils;
import atnum.content.swing.view.SwingView;
import atnum.content.swing.view.ViewPostConstruct;

@SwingView(name = "about")
public class SwingAboutView extends ContentPane implements AboutView {

	static final String thanksText = "Based on LectureStudio  ";

	private JLabel thanksLabel;

	private JLabel buildDateLabel;

	private JLabel websiteLabel;

	private JLabel issueLabel;


	private JPanel license;


	private JButton closeButton;


	public SwingAboutView() {
		super();
	}

	@Override
	public void setAppName(String name) {
		SwingUtils.invoke(() -> {
			setTitle(getTitle() + " " + name);
		});
	}

	@Override
	public void setAppVersion(String version) {
		SwingUtils.invoke(() -> {
			thanksLabel.setText(thanksText);
		});
	}

	@Override
	public void setAppBuildDate(String date) {
		SwingUtils.invoke(() -> {
			buildDateLabel.setText("Released under GNU General Public License v3");
		});
	}

	@Override
	public void setWebsite(String website) {
		SwingUtils.invoke(() -> {
			websiteLabel.setName("https://atnum.uk");
			websiteLabel.setText("atnum.uk");
		});
	}

	@Override
	public void setIssueWebsite(String website) {
//		SwingUtils.invoke(() -> {
//			issueLabel.setName(website);
//		});
	}

	@Override
	public void setLicense(String licenseText) {
		var pane = new JTextPane();
		pane.setText(licenseText);
		SwingUtils.invoke(() -> {
			license.add(pane);
		});
	}

	@Override
	public void setOnClose(Action action) {
		SwingUtils.bindAction(closeButton, action);
	}

	@ViewPostConstruct
	private void initialize() {
		MouseListener clickListener = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				JLabel label = (JLabel) event.getSource();
				try {
					Desktop.getDesktop().browse(new URI(label.getName()));
				}
				catch (Exception e) {
					// Ignore.
				}
			}
		};

		websiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		websiteLabel.addMouseListener(clickListener);
//		issueLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//		issueLabel.addMouseListener(clickListener);


	}
}
