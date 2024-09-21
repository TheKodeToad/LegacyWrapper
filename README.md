# LegacyWrapper

A wrapper library to launch legacy Minecraft versions with or without an applet implementing various fixes. This intends to replace Prism Launcher's current wrapper integrated in the launcher.

## TODO
- [x] Applet launch
- [x] Main class launch
- [x] Game directory patch
- [ ] Skin fix & online mode fix
- [ ] Asset index override

## Usage

### AppletLauncher

This launches the game using an applet, attempting to construct `net.minecraft.client.MinecraftApplet` or `com.mojang.minecraft.MinecraftApplet` (can be overriden).

Set main class to `org.prismlauncher.legacywrapper.applet.AppletLauncher`. The following parameters are available:

| Specification                             | Description                                                                                                                       |
| ----------------------------------------  | --------------------------------------------------------------------------------------------------------------------------------- |
| `--gameDir <value>`                       | Patches the game directory using reflection.                                                                                      |
| `--username <value>`, first argument      | Sets the username, otherwise it will be randomised.                                                                               |
| `--session <value>`, second argument      | Sets the session ID (`token:{accessToken}:{uuid}`), otherwise it will be determined from access token and UUID or default to `-`. |
| `--accessToken <value>`                   | Sets the access token, used to determine the session ID.                                                                          |
| `--uuid <value>`                          | Sets the UUID, used to determine the session ID.                                                                                  |
| `--server <value>`                        | Sets the address of a server to join.                                                                                             |
| `--port <value>`                          | Sets the port of the a server to join.                                                                                            |
| `--quickPlayMultiplayer <value>`          | Specifies the server address and port as `address:port`.                                                                          |
| `--demo`                                  | Enables demo mode if available.                                                                                                   |
| `--legacyWrapper.title <value>`           | Sets the applet window title.                                                                                                     |
| `--version <value>`                       | Sets the version ID, currently used to determine the applet window title if not explicitly set.                                   |
| `--width <value>`, `--height <value>`     | Sets the applet window dimensions.                                                                                                |
| `--legacyWrapper.maximized`               | Enables maximization for the applet window.                                                                                       |
| `--legacyWrapper.applet <value>`          | Overrides the automatically detected class used to construct the applet.                                                          |
| `--legacyWrapper.mainClass <value>`       | Overrides the automatically detected main class used to patch the game directory, which is typically `net.minecraft.client.Main`. |

All parameters are optional, and values to options can be specified with `=` as well a space.

### PassthroughLauncher

This launches the game directly using a main class, only applying fixes.

Set main class to `org.prismlauncher.legacywrapper.passthrough.PassthroughLauncher`. The following parameters are available:

| Specification                 | Description                                  |
| ----------------------------- | --------------------------------------------   |
| First argument **(required)** | Specifies the main class to invoke.                                                                                 |
| `--legacyWrapper.gameDir`     | Patches the game directory using reflection. The prefix is used for newer versions which already support --gameDir. |
| Remaining arguments           | Specifies the arguments to pass to the invoked main class. This includes prefixed options which are not recognised. |

Values to options can be specified with `=` as well as a space.
