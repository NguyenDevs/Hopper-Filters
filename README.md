**Enhance your Minecraft server with HopperFilter, a lightweight and customizable plugin that allows players to create advanced filters for hoppers!**

HopperFilter empowers players to control which items hoppers pick up by setting up a user-friendly GUI-based filter system. 
With per-player hopper limits, customizable permissions, and visual/audio effects, this plugin adds functionality and flair to your server's item management. 
Perfect for survival, factions, or any server where precise item sorting is key!

![image](https://github.com/user-attachments/assets/9b628bd9-f92e-4d21-bf58-3f8039309d89)
## Features
- **Hopper Filters**: Players can right-click (while sneaking) on a hopper to open a customizable GUI, allowing them to define which items the hopper will accept.
- **Per-Player Limits**: Establish a default limit on the number of hopper filters each player can create, with the option for permission-based overrides to customize limits.
- **Per-Player Filter Size**: Adjust the size of the filter GUI for individual players based on permissions, offering flexibility in configuration.
- **World-Specific Control**: Disable hopper filters in specific worlds through the config file for targeted management.
- **Lightweight & Efficient**: Engineered to deliver robust functionality with minimal impact on server performance.

![image](https://github.com/user-attachments/assets/31b13b0c-d8e2-4634-813e-280ae6d081d7)

## Commands
- `/hopperfilter reload` reloads the configuration file.
## Permissions
- `hopperfilter.admin` Allows admin command.
- `hopperfilter.use` Allows players to create and manage hopper filters.
- `hopperfilter.bypass` Permits use of filters in disabled worlds.
- `hopperfilter.limit.<n>` Sets a specific hopper limit (e.g., hopperfilter.limit.20 for 20 hoppers).
- `hopperfilter.limit.*` Grants unlimited hopper filters.
- `hopperfilter.menu.<n>` Sets a specific hopper menu size for player (e.g., hopperfilter.menu.54 for menu filter 54 slot.) - Default value 27 if not set.
