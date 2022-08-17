# clientcontext
A BungeeCord plugin that adds [LuckPerms](https://luckperms.net/) contexts for the client version and client type.

## Contexts

### client-version
This context specifies the version of Minecraft client that the player is using.
Two values will be assigned: an exact version (e.g. `1.2.1`), and a rough version (e.g. `1.2.x`).

### client-type
This context specifies the type of Minecraft client being used.
Currently, only two types are supported: `java`, and `bedrock` (through [Geyser](https://geysermc.org/) for Spigot)
