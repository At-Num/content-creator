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

package atnum.content.core.converter;

import static java.util.Objects.nonNull;

import java.net.NetworkInterface;

import atnum.content.core.beans.Converter;
import atnum.content.core.util.NetUtils;

/**
 * String to NetworkInterface and vice-versa converter.
 *
 * @author Alex Andres
 */
public class NetAdapterConverter implements Converter<String, NetworkInterface> {

	@Override
	public NetworkInterface to(String value) {
		if (nonNull(value)) {
			for (NetworkInterface adapter : NetUtils.getNetworkInterfaces()) {
				if (adapter.getName().equals(value)) {
					return adapter;
				}
			}
		}

		return null;
	}

	@Override
	public String from(NetworkInterface value) {
		if (nonNull(value)) {
			return value.getName();
		}

		return null;
	}
}
