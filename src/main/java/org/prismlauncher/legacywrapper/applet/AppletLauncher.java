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

package org.prismlauncher.legacywrapper.applet;

import java.applet.Applet;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.prismlauncher.legacywrapper.common.logging.Log;
import org.prismlauncher.legacywrapper.prelaunch.GameDirFix;

public final class AppletLauncher {

	private static final String[] APPLET_CLASS_NAMES = { "net.minecraft.client.MinecraftApplet",
			"com.mojang.minecraft.MinecraftApplet", "M" };

	public static void main(String[] rawArgs) throws Throwable {
		AppletArguments args = new AppletArguments(rawArgs);

		Class<?> appletClass;

		if (args.appletClass != null)
			appletClass = Class.forName(args.appletClass);
		else
			appletClass = findAppletClass();

		Log.debug("Constructing applet class: " + appletClass.getName());

		MethodHandle constructor = MethodHandles.lookup().findConstructor(appletClass,
				MethodType.methodType(void.class));
		Applet applet = (Applet) constructor.invoke();

		if (args.gameDir != null) {
			try {
				Class<?> mainClass;

				if (args.mainClass != null)
					mainClass = Class.forName(args.mainClass);
				else
					mainClass = findMinecraftField(appletClass).getType();

				Log.debug("Detected main class: " + mainClass.getName());

				GameDirFix.patchGameDir(mainClass, new File(args.gameDir));
			} catch (Throwable exception) {
				Log.warning("Could not patch game directory", exception);
			}
		}

		Log.debug("Starting applet");

		AppletFrame frame = new AppletFrame(args, applet);
		frame.start(args);
	}

	private static Class<?> findAppletClass() {
		for (String className : APPLET_CLASS_NAMES) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException ignored) {
			}
		}

		throw new UnsupportedOperationException("Cannot find any of " + Arrays.toString(APPLET_CLASS_NAMES));
	}

	private static Field findMinecraftField(Class<?> appletClass) {
		for (Field field : appletClass.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			if (!(field.getType().getName().startsWith("net.minecraft.")
					|| field.getType().getName().startsWith("com.mojang.") || !field.getType().getName().contains(".")))
				continue;

			if (!Arrays.asList(field.getType().getInterfaces()).contains(Runnable.class))
				continue;

			Log.debug("Detected main class field: " + field);
			return field;
		}

		throw new UnsupportedOperationException("Cannot find Minecraft field in " + appletClass);
	}

}
