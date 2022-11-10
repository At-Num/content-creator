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

package atnum.content.swing.converter;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import atnum.content.core.beans.Converter;

public class KeyEventConverter implements Converter<atnum.content.core.input.KeyEvent, KeyEvent> {

	public static final KeyEventConverter INSTANCE = new KeyEventConverter();


	@Override
	public KeyEvent to(atnum.content.core.input.KeyEvent event) {
		atnum.content.core.input.KeyEvent.EventType eventType = event.getEventType();
		int modifiers = event.getModifiers();
		int keyCode = event.getKeyCode();
		int id;

		boolean altGrDown = (modifiers & atnum.content.core.input.KeyEvent.ALT_GRAPH_MASK) != 0;
		boolean shiftDown = (modifiers & atnum.content.core.input.KeyEvent.SHIFT_MASK) != 0;
		boolean controlDown = (modifiers & atnum.content.core.input.KeyEvent.CTRL_MASK) != 0 || altGrDown;
		boolean altDown = (modifiers & atnum.content.core.input.KeyEvent.ALT_MASK) != 0 || altGrDown;

		modifiers = 0;

		if (altDown) {
			modifiers |= InputEvent.ALT_DOWN_MASK;
		}
		if (altGrDown) {
			modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
		}
		if (controlDown) {
			modifiers |= InputEvent.CTRL_DOWN_MASK;
		}
		if (shiftDown) {
			modifiers |= InputEvent.SHIFT_DOWN_MASK;
		}

		if (eventType == atnum.content.core.input.KeyEvent.EventType.TYPED) {
			id = KeyEvent.KEY_TYPED;
		}
		else if (eventType == atnum.content.core.input.KeyEvent.EventType.PRESSED) {
			id = KeyEvent.KEY_PRESSED;
		}
		else if (eventType == atnum.content.core.input.KeyEvent.EventType.RELEASED) {
			id = KeyEvent.KEY_RELEASED;
		}
		else {
			id = KeyEvent.KEY_TYPED;
		}

		return new KeyEvent(null, id, System.currentTimeMillis(), modifiers, keyCode, (char) keyCode);
	}

	@Override
	public atnum.content.core.input.KeyEvent from(KeyEvent event) {
		int id = event.getID();
		int code = event.getKeyCode();
		int modifiers = 0;
		atnum.content.core.input.KeyEvent.EventType eventType;

		if (event.isAltDown()) {
			modifiers |= atnum.content.core.input.KeyEvent.ALT_MASK;
		}
		if (event.isAltGraphDown()) {
			modifiers |= atnum.content.core.input.KeyEvent.ALT_GRAPH_MASK;
		}
		if (event.isControlDown()) {
			modifiers |= atnum.content.core.input.KeyEvent.CTRL_MASK;
		}
		if (event.isShiftDown()) {
			modifiers |= atnum.content.core.input.KeyEvent.SHIFT_MASK;
		}

		if (id == KeyEvent.KEY_TYPED) {
			eventType = atnum.content.core.input.KeyEvent.EventType.TYPED;
		}
		else if (id == KeyEvent.KEY_PRESSED) {
			eventType = atnum.content.core.input.KeyEvent.EventType.PRESSED;
		}
		else if (id == KeyEvent.KEY_RELEASED) {
			eventType = atnum.content.core.input.KeyEvent.EventType.RELEASED;
		}
		else {
			eventType = atnum.content.core.input.KeyEvent.EventType.TYPED;
		}

		return new atnum.content.core.input.KeyEvent(code, modifiers, eventType);
	}

}
