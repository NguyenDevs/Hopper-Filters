![image](https://github.com/user-attachments/assets/9b628bd9-f92e-4d21-bf58-3f8039309d89)

## HopperFilter Plugin

**Enhance your Minecraft server with HopperFilter** — a lightweight, powerful and highly customizable plugin that lets players create advanced hopper filters through an intuitive GUI system!

HopperFilter gives players full control over which items hoppers can pick up or transfer.  
With per-player limits, permission-based customization, world-specific toggles, and nice visual + sound effects — it brings both functionality and style to item management.

Perfect for **Survival**, **Factions**, **Skyblock**, or any server that values precise and beautiful item sorting!

![image](https://github.com/user-attachments/assets/31b13b0c-d8e2-4634-813e-280ae6d081d7)

## Features
- **Hopper Filters**  
  Right-click a hopper while sneaking → open a beautiful, customizable GUI to choose exactly which items the hopper will accept.

- **Per-Player Limits**  
  Set a default maximum number of filters per player, with the ability to override via permissions for VIPs/donors/admins.

- **Custom Filter GUI Size**  
  Adjust the filter menu size (27, 54 slots…) individually per player through permissions — more space for serious sorters!

- **World Blacklist**  
  Easily disable hopper filters in certain worlds via config (great for minigames, spawn, etc.).

- **Lightweight & Performant**  
  Designed from the ground up to be efficient — minimal lag even on large servers with thousands of hoppers.

## Commands
- `/hopperfilter reload`  
  Reloads the configuration file (admin only)

## Permissions
- `hopperfilter.admin`  
  Access to admin commands (reload)

- `hopperfilter.use`  
  Allows players to create and manage their own hopper filters

- `hopperfilter.bypass`  
  Allows using filters even in worlds where they are disabled

- `hopperfilter.limit.<number>`  
  Sets custom hopper filter limit for the player  
  Example: `hopperfilter.limit.20` → max 20 filters

- `hopperfilter.limit.*`  
  Unlimited hopper filters

- `hopperfilter.menu.<size>`  
  Sets custom GUI size for the player's filter menu  
  Example: `hopperfilter.menu.54` → 54-slot menu  
  (Default: 27 slots if no permission is set)
