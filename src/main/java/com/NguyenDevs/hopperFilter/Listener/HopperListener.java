package com.NguyenDevs.hopperFilter.Listener;

import com.NguyenDevs.hopperFilter.HopperFilter;
import com.NguyenDevs.hopperFilter.Manager.HopperManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HopperListener implements Listener {

    private final HopperFilter plugin;
    private final HopperManager hopperManager;
    private final Map<UUID, Location> openInventories = new HashMap<>();
    private final Map<Location, Integer> hopperTransferCount = new HashMap<>();
    private final Set<Location> activeParticles = new HashSet<>();

    public HopperListener(HopperFilter plugin, HopperManager hopperManager) {
        this.plugin = plugin;
        this.hopperManager = hopperManager;
        validateMenuSlots();
    }

    private void validateMenuSlots() {
        int defaultSlots = plugin.getConfig().getInt("menu-slot", 27);
        if (defaultSlots % 9 != 0 || defaultSlots < 9) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cInvalid menu-slot value in config.yml:" + defaultSlots + ". Must be 9, 18, 27, etc. Using default value 27."));
            plugin.getConfig().set("menu-slot", 27);
            try {
                plugin.saveConfig();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
            }
        }
    }

    private int getPlayerMenuSlots(Player player) {
        int defaultSlots = plugin.getConfig().getInt("menu-slot", 27);
        for (int slots = 54; slots >= 9; slots -= 9) {
            if (player.hasPermission("hopperfilter.menu." + slots)) {
                return slots;
            }
        }
        return defaultSlots;
    }

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.HOPPER) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.getType() != Material.AIR) return;

        if (!player.hasPermission("hopperfilter.use")) {
            event.setCancelled(true);
            player.sendMessage(hopperManager.getMessage("prefix", "&8[&7HopperFilter&8]") + " " +
                    hopperManager.getMessage("no-permission", "&cYou do not have permission to use the hopper filter!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }

        Location hopperLocation = block.getLocation();
        List<String> disabledWorlds = plugin.getConfig().getStringList("disable-world");
        if (disabledWorlds.contains(hopperLocation.getWorld().getName()) && !player.hasPermission("hopperfilter.bypass")) {
            event.setCancelled(true);
            player.sendMessage(hopperManager.getMessage("prefix", "&8[&7HopperFilter&8]") + " " +
                    hopperManager.getMessage("world-disabled", "&cThe hopper filter is disabled in this world!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }

        event.setCancelled(true);

        String title = hopperManager.getFilterGUITitle(player, hopperLocation);
        if (title.endsWith(" Unknown")) {
            title = hopperManager.getMessage("filter-gui-title", "&8[&7HopperFilter&8]");
        }
        int slots = getPlayerMenuSlots(player);
        Inventory filterInventory = plugin.getServer().createInventory(null, slots, title);

        if (hopperManager.getHopperFilters().containsKey(hopperLocation)) {
            List<Material> materials = hopperManager.getHopperFilters().get(hopperLocation);
            for (int i = 0; i < Math.min(materials.size(), slots); i++) {
                filterInventory.setItem(i, new ItemStack(materials.get(i), 1));
            }
        }

        player.openInventory(filterInventory);
        openInventories.put(player.getUniqueId(), hopperLocation);

        player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1.0f, 1.8f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();

        if (!openInventories.containsKey(playerId)) return;

        Location hopperLocation = openInventories.get(playerId);
        String expectedTitle = hopperManager.getFilterGUITitle(player, hopperLocation);
        if (expectedTitle.endsWith(" Unknown")) {
            expectedTitle = hopperManager.getMessage("filter-gui-title", "&8[&7HopperFilter&8]");
        }
        if (!event.getView().getTitle().equals(expectedTitle)) return;

        int slots = getPlayerMenuSlots(player);
        if (event.getSlot() >= slots) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                event.getClickedInventory().setItem(event.getSlot(), null);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.2f);
            }
        } else if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Inventory filterInventory = event.getView().getTopInventory();
            boolean materialExists = false;
            for (int i = 0; i < slots; i++) {
                ItemStack slotItem = filterInventory.getItem(i);
                if (slotItem != null && slotItem.getType() == clickedItem.getType()) {
                    materialExists = true;
                    break;
                }
            }

            if (materialExists) {
                player.sendMessage(hopperManager.getMessage("prefix", "&8[&7HopperFilter&8]") + " " +
                        hopperManager.getMessage("item-already-in-filter", "&cThis item is already in the filter!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }

            for (int i = 0; i < slots; i++) {
                ItemStack slotItem = filterInventory.getItem(i);
                if (slotItem == null || slotItem.getType() == Material.AIR) {
                    ItemStack copyItem = new ItemStack(clickedItem.getType(), 1);
                    filterInventory.setItem(i, copyItem);
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1.0f, 1.5f);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!openInventories.containsKey(playerId)) return;

        Location hopperLocation = openInventories.get(playerId);
        String expectedTitle = hopperManager.getFilterGUITitle(player, hopperLocation);
        if (expectedTitle.endsWith(" Unknown")) {
            expectedTitle = hopperManager.getMessage("filter-gui-title", "&8[&7HopperFilter&8]");
        }
        if (!event.getView().getTitle().equals(expectedTitle)) return;

        openInventories.remove(playerId);

        int slots = getPlayerMenuSlots(player);
        List<Material> filterMaterials = new ArrayList<>();
        Inventory filterInventory = event.getInventory();

        for (int i = 0; i < slots; i++) {
            ItemStack item = filterInventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                filterMaterials.add(item.getType());
            }
        }

        if (filterMaterials.isEmpty()) {
            hopperManager.removeHopperFilter(hopperLocation);
        } else {
            if (!hopperManager.getHopperFilters().containsKey(hopperLocation)) {
                if (hopperManager.canPlayerAddHopper(player, hopperLocation)) {
                    hopperManager.addHopperFilter(player, hopperLocation, filterMaterials);
                } else {
                    filterInventory.clear();
                    return;
                }
            } else {
                hopperManager.addHopperFilter(player, hopperLocation, filterMaterials);
            }
        }

        player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!(event.getDestination().getHolder() instanceof Hopper)) return;

        Hopper hopper = (Hopper) event.getDestination().getHolder();
        Location hopperLocation = hopper.getLocation().getBlock().getLocation();

        if (!hopperManager.getHopperFilters().containsKey(hopperLocation)) return;

        ItemStack item = event.getItem();
        List<Material> allowedMaterials = hopperManager.getHopperFilters().get(hopperLocation);

        if (!allowedMaterials.contains(item.getType())) {
            event.setCancelled(true);
            return;
        }

        hopperTransferCount.put(hopperLocation, hopperTransferCount.getOrDefault(hopperLocation, 0) + item.getAmount());
        int transferredCount = hopperTransferCount.get(hopperLocation);

        if (transferredCount >= plugin.getConfig().getDouble("delay-effect")) {
            hopperTransferCount.put(hopperLocation, 0);

            if (plugin.getConfig().getBoolean("sound.enabled", true)) {
                try {
                    Sound sound = Sound.valueOf(plugin.getConfig().getString("sound.name", "BLOCK_GRINDSTONE_USE"));
                    float volume = (float) plugin.getConfig().getDouble("sound.volume", 1.0);
                    float pitch = (float) plugin.getConfig().getDouble("sound.pitch", 1.0);
                    hopperLocation.getWorld().playSound(hopperLocation.clone().add(0.5, 0.5, 0.5), sound, volume, pitch);
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cInvalid sound name in config.yml: " + plugin.getConfig().getString("sound.name")));
                }
            }

            if (plugin.getConfig().getBoolean("particle.enabled", true)) {
                Location center = hopperLocation.clone().add(0.5, 1.0, 0.5);
                startSpiralEffect(center);
            }
        }
    }

    private void startSpiralEffect(Location center) {
        World world = center.getWorld();
        Particle particle;

        try {
            particle = Particle.valueOf(plugin.getConfig().getString("particle.name", "FIREWORKS_SPARK"));
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cInvalid particle name in config.yml: " + plugin.getConfig().getString("particle.name")));
            return;
        }

        final int points = 6;
        final double initialRadius = 0.5;
        final double radiusStep = initialRadius / 20.0;

        new BukkitRunnable() {
            double radius = initialRadius;
            double angle = 0;
            int steps = 0;

            @Override
            public void run() {
                if (steps >= 20) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < points; i++) {
                    double currentAngle = angle + (2 * Math.PI / points) * i;
                    double x = center.getX() + radius * Math.cos(currentAngle);
                    double z = center.getZ() + radius * Math.sin(currentAngle);
                    world.spawnParticle(particle, x, center.getY(), z, 1, 0, 0, 0, 0);
                }

                angle += Math.PI / 16;
                radius -= radiusStep;
                steps++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.HOPPER) return;

        Location hopperLocation = block.getLocation();
        hopperManager.removeHopperFilter(hopperLocation);
    }
}