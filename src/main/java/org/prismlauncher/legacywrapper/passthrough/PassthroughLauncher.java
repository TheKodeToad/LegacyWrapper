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

package org.prismlauncher.legacywrapper.passthrough;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.prismlauncher.legacywrapper.common.logging.Log;
import org.prismlauncher.legacywrapper.prelaunch.GameDirFix;

public class PassthroughLauncher {

	public static void main(String[] rawArgs) throws Throwable {
		PassthroughArguments args = new PassthroughArguments(rawArgs);

		Class<?> mainClass = Class.forName(args.mainClass);

		if (args.gameDir != null) {
			try {
				Log.debug("Patching game directory");
				GameDirFix.patchGameDir(mainClass, new File(args.gameDir));
			} catch (Throwable exception) {
				Log.warning(
						"Could not patch game directory. If this version supports the --gameDir option use it instead of --legacyWrapper.gameDir.",
						exception);
			}
		}

		MethodHandle mainMethod = MethodHandles.lookup().findStatic(mainClass, "main",
				MethodType.methodType(void.class, String[].class));
		mainMethod.invokeExact(args.gameArgs);
	}

}