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

import org.prismlauncher.legacywrapper.common.ArgumentParser;
import org.prismlauncher.legacywrapper.common.Arguments;
import org.prismlauncher.legacywrapper.common.logging.Log;

public final class AppletArguments {

	public final String gameDir;
	public final String username;
	public final String session;
	public final String serverAddress;
	public final int serverPort;
	public final boolean demo;
	public final int width;
	public final int height;
	public final String title;
	public final boolean maximized;
	public final String appletClass;
	public final String mainClass;

	public AppletArguments(String[] input) {
		ArgumentParser parser = new ArgumentParser();
		parser.addOption("gameDir");
		parser.addOption("username");
		parser.addOption("session");
		parser.addOption("accessToken");
		parser.addOption("uuid");
		parser.addOption("server");
		parser.addOption("port");
		parser.addOption("quickPlayMultiplayer");
		parser.addFlag("demo");
		parser.addOption("legacyWrapper.title");
		parser.addOption("version");
		parser.addOption("width");
		parser.addOption("height");
		parser.addFlag("legacyWrapper.maximized");
		parser.addOption("legacyWrapper.applet");
		parser.addOption("legacyWrapper.mainClass");

		Arguments args = parser.parse(input);

		this.gameDir = args.options.get("gameDir");

		String usernameTemp = args.options.get("username");

		if (usernameTemp == null && args.extra.size() >= 1)
			usernameTemp = args.extra.get(0);

		this.username = usernameTemp;

		String sessionTemp = args.options.get("session");

		if (sessionTemp == null && args.extra.size() >= 2)
			sessionTemp = args.extra.get(1);

		if (sessionTemp == null) {
			String accessToken = args.options.get("accessToken");
			String uuid = args.options.get("uuid");

			if (accessToken != null && uuid == null)
				Log.warning(
						"--accessToken and --uuid is required to generate session ID; only --accessToken was provided");

			if (uuid != null && accessToken == null)
				Log.warning("--accessToken and --uuid is required to generate session ID; only --uuid was provided");

			if (uuid != null && accessToken != null)
				sessionTemp = "token:" + accessToken + ':' + uuid;
		}

		if (sessionTemp == null)
			sessionTemp = "-";

		this.session = sessionTemp;

		String serverAddressTemp = args.options.get("server");
		int serverPortTemp = 0;

		if (serverAddressTemp != null) {
			serverPortTemp = 25565;

			String portStr = args.options.get("port");

			if (portStr != null) {
				try {
					serverPortTemp = Integer.parseInt(portStr);
				} catch (NumberFormatException exception) {
					Log.warning("Could not parse port; falling back to " + serverPortTemp);
				}
			}
		} else {
			String quickPlayMultiplayer = args.options.get("quickPlayMultiplayer");

			if (quickPlayMultiplayer != null) {
				serverAddressTemp = quickPlayMultiplayer;
				serverPortTemp = 25565;

				int portSplitIndex = quickPlayMultiplayer.indexOf(':');
				int ipv6EndIndex = quickPlayMultiplayer.indexOf(']');

				// allow ipv6 to function correctly
				if (quickPlayMultiplayer.startsWith("[") && ipv6EndIndex != -1) {
					serverAddressTemp = quickPlayMultiplayer.substring(1, ipv6EndIndex);

					String portStr = quickPlayMultiplayer.substring(ipv6EndIndex + 1);

					if (portStr.startsWith(":")) {
						portStr = portStr.substring(1);

						try {
							serverPortTemp = Integer.parseInt(portStr);
						} catch (NumberFormatException ignored) {
						}
					}
				} else if (portSplitIndex != -1 && portSplitIndex == quickPlayMultiplayer.lastIndexOf(':')) {
					serverAddressTemp = quickPlayMultiplayer.substring(0, portSplitIndex);

					try {
						serverPortTemp = Integer.parseInt(quickPlayMultiplayer.substring(portSplitIndex + 1));
					} catch (NumberFormatException ignored) {
					}
				}
			}
		}

		this.serverAddress = serverAddressTemp;
		this.serverPort = serverPortTemp;

		this.demo = args.flags.contains("demo");

		int widthTemp = 854;
		int heightTemp = 480;

		try {
			String widthStr = args.options.get("width");
			String heightStr = args.options.get("height");

			if (widthStr != null)
				widthTemp = Integer.parseInt(widthStr);

			if (heightStr != null)
				heightTemp = Integer.parseInt(heightStr);
		} catch (NumberFormatException exception) {
			Log.warning("Could not parse window dimensions; falling back to " + widthTemp + '×' + heightTemp,
					exception);
		}

		this.width = widthTemp;
		this.height = heightTemp;
		this.maximized = args.flags.contains("legacyWrapper.maximized");

		String titleTemp = args.options.get("legacyWrapper.title");

		if (titleTemp == null && args.options.containsKey("version"))
			titleTemp = "Minecraft " + args.options.get("version");

		if (titleTemp == null)
			titleTemp = "Minecraft";

		this.title = titleTemp;

		this.appletClass = args.options.get("appletClass");
		this.mainClass = args.options.get("mainClass");

		if (args.extra.size() > 2)
			Log.warning("Completely ignored arguments " + args.extra.subList(2, args.extra.size()));
	}

}
