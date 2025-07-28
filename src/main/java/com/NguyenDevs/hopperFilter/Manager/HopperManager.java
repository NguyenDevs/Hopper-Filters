package com.NguyenDevs.hopperFilter.Manager;

import com.NguyenDevs.hopperFilter.HopperFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class HopperManager {
    private final HopperFilter plugin;
    private final Map<Location, List<Material>> hopperFilters = new HashMap<>();
    private final Map<UUID, Integer> playerHopperCounts = new HashMap<>();
    private final Map<Location, UUID> hopperOwners = new HashMap<>();

    public HopperManager(HopperFilter plugin) {
        this.plugin = plugin;
        loadHopperFilters();
    }

    public void loadHopperFilters() {
        hopperFilters.clear();
        playerHopperCounts.clear();
        hopperOwners.clear();
        FileConfiguration dataConfig = plugin.getDataConfig();
        ConfigurationSection section = dataConfig.getConfigurationSection("hopper-filters");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String[] parts = key.split(",");
            if (parts.length != 5) continue;

            try {
                Location location = new Location(
                        plugin.getServer().getWorld(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                );
                UUID owner = UUID.fromString(parts[4]);

                List<String> materialNames = section.getStringList(key);
                List<Material> materials = new ArrayList<>();
                for (String materialName : materialNames) {
                    Material material = Material.getMaterial(materialName);
                    if (material != null) {
                        materials.add(material);
                    }
                }
                if (!materials.isEmpty()) {
                    hopperFilters.put(location, materials);
                    hopperOwners.put(location, owner);
                    playerHopperCounts.put(owner, playerHopperCounts.getOrDefault(owner, 0) + 1);
                }
            } catch (NumberFormatException | NullPointerException ignored) {
                // Ignore invalid entries
            }
        }
    }

    public void saveHopperFilters() {
        FileConfiguration dataConfig = plugin.getDataConfig();
        dataConfig.set("hopper-filters", null); // Clear existing data
        ConfigurationSection section = dataConfig.createSection("hopper-filters");

        for (Map.Entry<Location, List<Material>> entry : hopperFilters.entrySet()) {
            Location location = entry.getKey();
            UUID owner = hopperOwners.get(location);
            if (owner == null) continue; // Skip if no owner

            String key = location.getWorld().getName() + "," + location.getBlockX() + "," +
                    location.getBlockY() + "," + location.getBlockZ() + "," + owner.toString();
            List<String> materialNames = new ArrayList<>();
            for (Material material : entry.getValue()) {
                materialNames.add(material.name());
            }
            section.set(key, materialNames);
        }

        plugin.saveDataConfig();
    }

    public boolean canPlayerAddHopper(Player player, Location hopperLocation) {
        if (player.hasPermission("hopperfilter.limit.*")) {
            return true;
        }

        int defaultLimit = plugin.getConfig().getInt("default-hopper-limit", 10);
        int playerLimit = defaultLimit;

        for (int i = 100; i >= 0; i--) {
            if (player.hasPermission("hopperfilter.limit." + i)) {
                playerLimit = i;
                break;
            }
        }

        int currentCount = playerHopperCounts.getOrDefault(player.getUniqueId(), 0);
        if (currentCount >= playerLimit) {
            player.sendMessage(getMessage("prefix", "&8[&7HopperFilter&8]") + " " +
                    getMessage("limit-reached", "&cYou have reached your hopper filter limit of " + playerLimit + "!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return false;
        }

        return true;
    }

    public void addHopperFilter(Player player, Location location, List<Material> materials) {
        hopperFilters.put(location, materials);
        UUID playerUUID = player.getUniqueId();
        hopperOwners.put(location, playerUUID);
        playerHopperCounts.put(playerUUID, playerHopperCounts.getOrDefault(playerUUID, 0) + 1);
        saveHopperFilters();
    }

    public void removeHopperFilter(Location location) {
        if (hopperFilters.containsKey(location)) {
            UUID owner = hopperOwners.get(location);
            if (owner != null) {
                playerHopperCounts.put(owner, playerHopperCounts.getOrDefault(owner, 0) - 1);
                if (playerHopperCounts.get(owner) <= 0) {
                    playerHopperCounts.remove(owner);
                }
                hopperOwners.remove(location);
            }
            hopperFilters.remove(location);
            saveHopperFilters();
        }
    }

    public String getFilterGUITitle(Player player, Location location) {
        String baseTitle = getMessage("filter-gui-title", "&8[&7HopperFilter&8]");
        UUID ownerUUID = hopperOwners.get(location);
        String ownerName = "Unknown";
        if (ownerUUID != null) {
            Player owner = plugin.getServer().getPlayer(ownerUUID);
            if (owner != null) {
                ownerName = owner.getName();
            } else {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(ownerUUID);
                if (offlinePlayer.hasPlayedBefore()) {
                    ownerName = offlinePlayer.getName();
                }
            }
        }
        return baseTitle + " - " + ownerName ;
    }

    public String getMessage(String path, String defaultMessage) {
        return translateColorCodes(plugin.getMessagesConfig().getString(path, defaultMessage));
    }

    private String translateColorCodes(String message) {
        return message.replace('&', '§');
    }

    public Map<Location, List<Material>> getHopperFilters() {
        return hopperFilters;
    }
}