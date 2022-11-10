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

package atnum.content.swing.combobox;

import static java.util.Objects.nonNull;

import java.awt.Component;
import java.net.NetworkInterface;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class NetAdapterRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		NetworkInterface netAdapter = (NetworkInterface) value;

		if (nonNull(netAdapter)) {
			setText(String.format("%s (%s)", netAdapter.getDisplayName(),
					netAdapter.getName()));
		}
		else {
			setText("");
		}

		return this;
	}
}
