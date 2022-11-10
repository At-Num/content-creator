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

package atnum.content.swing.components;

import java.util.ResourceBundle;

import javax.inject.Inject;

import atnum.content.core.text.TeXFont;

public class TeXFontPickerButton extends ToggleComboButton<TeXFont> {

	private final TeXFontChooser chooser;


	@Inject
	public TeXFontPickerButton(ResourceBundle resourceBundle) {
		super();

		chooser = new TeXFontChooser(resourceBundle);
		chooser.setOnOk(e -> {
			setSelectedItem(chooser.getSelectedFont());
			hidePopup();
		});
		chooser.setOnCancel(e -> hidePopup());

		setContent(chooser);
	}
}
