# ClientContext
A BungeeCord plugin that adds [LuckPerms](https://luckperms.net/) contexts for client info, allowing permissions to be added or removed depending on what kind of device the client is on.

## Installation
There are two ways to install ClientContext: standalone with Spigot, or in a BungeeCord network.

### Standalone
When you have [Geyser](https://geysermc.org/) installed as a plugin on a single Spigot or Paper server, you can add ClientContext alongside it under the `plugins` folder.

For a standalone installation, you must have have [floodgate](https://ci.opencollab.dev/job/GeyserMC/job/Floodgate/job/master/) installed as well.

### Networked
When using [Geyser](https://geysermc.org/) on a BungeeCord proxy (as well as [floodgate](https://ci.opencollab.dev/job/GeyserMC/job/Floodgate/job/master/)), contexts will be resolved on the proxy and sent to each of the Spigot servers through plugin messages. This requires installing ClientContext on both the proxy and each of the Spigot servers.

## Contexts
### client:type
This context specifies the type of Minecraft client being used.  
Currently, only two types are supported: `java`, and `bedrock`

### client:version
This context specifies the version of Minecraft client that the player is using.  
Two values will be assigned: an exact version (e.g. `1.2.1`), and a rough version (e.g. `1.2.x`).

### client:archetype
A device archetype for a client.  
Possible values include: `computer`, `console`, `mobile`.

### client:device
> Bedrock clients only.

The device type of a Bedrock client:  
See [here](https://github.com/GeyserMC/Geyser/blob/master/common/src/main/java/org/geysermc/floodgate/util/DeviceOs.java) for a list of values, e.g. `ios`, `nx`, `google`, etc.

### client:controls
> Bedrock clients only.

The control type for a client:
See [here](https://github.com/GeyserMC/Geyser/blob/master/common/src/main/java/org/geysermc/floodgate/util/InputMode.java) for a list of values, e.g
`keyboard_mouse`, `touch`, `controller`, etc.
