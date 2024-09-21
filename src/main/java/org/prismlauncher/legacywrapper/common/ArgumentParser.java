// SPDX-License-Identifier: GPL-3.0-only
/*
 *  LegacyWrapper
 *  Copyright (C) 2024 TheKodeToad <TheKodeToad@proton.me>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Linking this library statically or dynamically with other modules is
 *  making a combined work based on this library. Thus, the terms and
 *  conditions of the GNU General Public License cover the whole
 *  combination.
 *
 *  As a special exception, the copyright holders of this library give
 *  you permission to link this library with independent modules to
 *  produce an executable, regardless of the license terms of these
 *  independent modules, and to copy and distribute the resulting
 *  executable under terms of your choice, provided that you also meet,
 *  for each linked independent module, the terms and conditions of the
 *  license of that module. An independent module is a module which is
 *  not derived from or based on this library. If you modify this
 *  library, you may extend this exception to your version of the
 *  library, but you are not obliged to do so. If you do not wish to do
 *  so, delete this exception statement from your version.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.prismlauncher.legacywrapper.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ArgumentParser {

	private final Set<String> flags = new HashSet<String>();
	private final Set<String> options = new HashSet<String>();

	public void addFlag(String name) {
		this.flags.add(name);
	}

	public void addOption(String name) {
		this.options.add(name);
	}

	public Arguments parse(String[] input) {
		Map<String, String> optionsOut = new HashMap<String, String>();
		Set<String> flagsOut = new HashSet<String>();
		List<String> extraOut = new ArrayList<String>();

		int i = -1;

		while (++i < input.length) {
			String label = input[i];

			// option terminator
			if (label.equals("--")) {
				while (++i < input.length)
					extraOut.add(input[i]);

				break;
			}

			if (!label.startsWith("-")) {
				extraOut.add(label);
				continue;
			}

			String key = label;
			String value = null;

			if (key.startsWith("--"))
				key = key.substring(2);
			else
				key = key.substring(1);

			int separatorIndex = key.indexOf('=');

			if (separatorIndex != -1) {
				value = key.substring(separatorIndex + 1);
				key = key.substring(0, separatorIndex);
			}

			if (this.flags.contains(key)) {
				flagsOut.add(key);
				continue;
			}

			if (!this.options.contains(key)) {
				extraOut.add(label);
				continue;
			}

			if (value == null) {
				if (++i >= input.length)
					throw new IllegalArgumentException("Missing value for '" + label + "'");

				value = input[i];
			}

			optionsOut.put(key, value);
		}

		return new Arguments(optionsOut, flagsOut, extraOut);
	}

}
