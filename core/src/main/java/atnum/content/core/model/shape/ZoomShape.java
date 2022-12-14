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

package atnum.content.core.model.shape;

import java.io.IOException;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.tool.Stroke;

/**
 * A shape representing the "zoom rectangle" while using a zoom tool. Usually it
 * is removed after the zoom operation has ended.
 *
 * @author Alex Andres
 */
public class ZoomShape extends RectangleShape {

	/**
	 * Creates a new {@link ZoomShape} with the specified stroke.
	 * (Calls {@link RectangleShape#RectangleShape(Stroke)} with the stroke.)
	 *
	 * @param stroke The stroke.
	 */
	public ZoomShape(Stroke stroke) {
		super(stroke);
	}

	/**
	 * Creates a new {@link ZoomShape} with the specified input byte array containing the data for a stroke.
	 *
	 * @param input The input byte array.
	 */
	public ZoomShape(byte[] input) throws IOException {
		super(input);
	}

	@Override
	public boolean keepRatio() {
		return true;
	}

	@Override
	public ZoomShape clone() {
		ZoomShape shape = new ZoomShape(getStroke().clone());
		shape.setHandle(getHandle());
		shape.setKeyEvent(getKeyEvent());

		for (PenPoint2D point : getPoints()) {
			shape.addPoint(point.clone());
		}

		return shape;
	}
}
