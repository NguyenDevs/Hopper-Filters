package com.NguyenDevs.hopperFilter;

import com.NguyenDevs.hopperFilter.Command.HopperCommand;
import com.NguyenDevs.hopperFilter.Listener.HopperListener;
import com.NguyenDevs.hopperFilter.Manager.HopperManager;
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
        config.addDefault("disable-world", new String[]{"example", "example_nether", "example_the_end"});
        config.options().copyDefaults(true);
        saveConfig();
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        hopperManager = new HopperManager(this);
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
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7HopperFilter&8] &aPlugin disabled!"));
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void reloadAllConfigs() {
        reloadConfig();
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
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