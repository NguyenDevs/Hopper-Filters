package com.NguyenDevs.hopperFilter;

import com.NguyenDevs.hopperFilter.Command.HopperCommand;
import com.NguyenDevs.hopperFilter.Listener.HopperListener;
import com.NguyenDevs.hopperFilter.Manager.HopperManager;
import com.NguyenDevs.hopperFilter.Utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class HopperFilter extends JavaPlugin {
    private HopperManager hopperManager;
    private File dataFile;
    private FileConfiguration dataConfig;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        config.addDefault("update-notify", true);
        config.addDefault("menu-slot", 27);
        config.addDefault("sound.enabled", true);
        config.addDefault("sound.name", "BLOCK_GRINDSTONE_USE");
        config.addDefault("sound.volume", 0.5);
        config.addDefault("sound.pitch", 1.5);
        config.addDefault("particle.enabled", true);
        config.addDefault("particle.name", "FIREWORK");
        config.addDefault("delay-effect", 10);
        config.addDefault("disable-world", new String[]{"example", "example_nether", "example_the_end"});
        config.options().copyDefaults(true);
        saveConfig();

        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        messagesConfig.addDefault("prefix", "&8[&7HopperFilter&8]");
        messagesConfig.addDefault("filter-gui-title", "&8HopperFilter");
        messagesConfig.addDefault("no-permission", "&cYou don't have permission to do this!");
        messagesConfig.addDefault("usage", "&7Usage: /hopperfilter <reload>");
        messagesConfig.addDefault("reload-success", "&aConfiguration reloaded successfully!");
        messagesConfig.addDefault("limit-reached", "&cYou have reached your hopper filter limit of %limit%!");
        messagesConfig.addDefault("world-disabled", "&cThe hopper filter is disabled in this world!");
        messagesConfig.options().copyDefaults(true);

        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cCould not save messages.yml: " + e.getMessage()));
        }

        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        UpdateChecker updateChecker = new UpdateChecker(127398, this);
        updateChecker.checkForUpdate();

        hopperManager = new HopperManager(this);
        hopperManager.loadHopperFilters();
        getServer().getPluginManager().registerEvents(new HopperListener(this, hopperManager), this);
        HopperCommand hopperCommand = new HopperCommand(this, hopperManager);
        getCommand("hopperfilter").setExecutor(hopperCommand);
        getCommand("hopperfilter").setTabCompleter(hopperCommand);

        printHopperFilterLogo();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &aHopperFilter plugin enabled successfully!"));
    }

    @Override
    public void onDisable() {
        hopperManager.saveHopperFilters();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cPlugin disabled successfully!"));
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cCould not save data.yml: " + e.getMessage()));
        }
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void reloadAllConfigs() {
        reloadConfig();
        FileConfiguration config = getConfig();
        config.addDefault("update-notify", true);
        config.addDefault("default-hopper-limit", 10);
        config.addDefault("menu-slot", 27);
        config.addDefault("sound.enabled", true);
        config.addDefault("sound.name", "BLOCK_GRINDSTONE_USE");
        config.addDefault("sound.volume", 0.5);
        config.addDefault("sound.pitch", 1.5);
        config.addDefault("particle.enabled", true);
        config.addDefault("particle.name", "FIREWORK");
        config.addDefault("delay-effect", 10);
        config.addDefault("disable-world", new String[]{"example", "example_nether", "example_the_end"});
        config.options().copyDefaults(true);
        saveConfig();

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messagesConfig.addDefault("prefix", "&8[&7HopperFilter&8]");
        messagesConfig.addDefault("filter-gui-title", "&8HopperFilter");
        messagesConfig.addDefault("no-permission", "&cYou don't have permission to do this!");
        messagesConfig.addDefault("usage", "&7Usage: /hopperfilter <reload>");
        messagesConfig.addDefault("reload-success", "&aConfiguration reloaded successfully!");
        messagesConfig.addDefault("limit-reached", "&cYou have reached your hopper filter limit of %limit%!");
        messagesConfig.addDefault("world-disabled", "&cThe hopper filter is disabled in this world!");
        messagesConfig.options().copyDefaults(true);

        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &cCould not save messages.yml: " + e.getMessage()));
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public HopperManager getHopperManager() {
        return hopperManager;
    }

    public void printHopperFilterLogo() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██╗  ██╗ ██████╗ ██████╗ ██████╗ ███████╗██████╗ "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██║  ██║██╔═══██╗██╔══██╗██╔══██╗██╔════╝██╔══██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ███████║██║   ██║██████╔╝██████╔╝█████╗  ██████╔╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██╔══██║██║   ██║██╔═══╝ ██╔═══╝ ██╔══╝  ██╔══██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██║  ██║╚██████╔╝██║     ██║     ███████╗██║  ██║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝     ╚══════╝╚═╝  ╚═╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ███████╗██╗██╗  ████████╗███████╗██████╗ "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██╔════╝██║██║  ╚══██╔══╝██╔════╝██╔══██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   █████╗  ██║██║     ██║   █████╗  ██████╔╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██╔══╝  ██║██║     ██║   ██╔══╝  ██╔══██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ██║     ██║███████╗██║   ███████╗██║  ██║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7   ╚═╝     ╚═╝╚══════╝╚═╝   ╚══════╝╚═╝  ╚═╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8     Hopper Filter"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6     Version " + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b     Development by NguyenDevs"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
    }
}