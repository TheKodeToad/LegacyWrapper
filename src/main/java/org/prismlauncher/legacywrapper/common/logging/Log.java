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

package org.prismlauncher.legacywrapper.common.logging;

import java.io.PrintStream;

public final class Log {

	private static final boolean DEBUG_MODE = Boolean.getBoolean("org.prismlauncher.legacywrapper.debug");
	private static final boolean LEVEL_PREFIX_SUPPORTED;
	private static final PrintStream STDOUT = new PrintStream(System.out);
	private static final PrintStream STDERR	= new PrintStream(System.err);

	static {
		String useLevelPrefix = System.getProperty("org.prismlauncher.legacywrapper.useLevelPrefix");

		if (useLevelPrefix == null)
			LEVEL_PREFIX_SUPPORTED = System.getProperty("multimc.instance.title") != null || System.getProperty("org.prismlauncher.instance.name") != null;
		else
			LEVEL_PREFIX_SUPPORTED = useLevelPrefix.equals("true");
	}

	public static void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	public static void info(String message) {
		log(LogLevel.INFO, message);
	}

	public static void warning(String message) {
		log(LogLevel.WARNING, message);
	}

	public static void warning(String message, Throwable exception) {
		log(LogLevel.WARNING, message, exception);
	}

	public static void error(String message) {
		log(LogLevel.ERROR, message);
	}

	public static void error(String message, Throwable exception) {
		log(LogLevel.ERROR, message, exception);
	}

	public static void fatal(String message) {
		log(LogLevel.FATAL, message);
	}

	public static void fatal(String message, Throwable exception) {
		log(LogLevel.FATAL, message, exception);
	}

	public static void log(LogLevel level, String message) {
		log(level, message, null);
	}

	public static void log(LogLevel level, String message, Throwable exception) {
		if (level == LogLevel.DEBUG && !DEBUG_MODE)
			return;

		message = "[LegacyWrapper] " + level + ": " + message;

		PrintStream out = level.error ? STDERR : STDOUT;

		if (LEVEL_PREFIX_SUPPORTED) {
			for (String line : message.split("\n")) {
				line = "!![" + level.name + "]!" + line;
				out.println(line);
			}
		} else
			out.println(message);

		if (exception != null)
			exception.printStackTrace(STDERR);
	}

}
