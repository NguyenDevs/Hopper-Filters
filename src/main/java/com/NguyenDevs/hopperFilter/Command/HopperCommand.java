package com.NguyenDevs.hopperFilter.Command;

import com.NguyenDevs.hopperFilter.HopperFilter;
import com.NguyenDevs.hopperFilter.Manager.HopperManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HopperCommand implements CommandExecutor, TabCompleter {
    private final HopperFilter plugin;
    private final HopperManager hopperManager;

    public HopperCommand(HopperFilter plugin, HopperManager hopperManager) {
        this.plugin = plugin;
        this.hopperManager = hopperManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = hopperManager.getMessage("prefix", "&8[&7HopperFilter&8]");

        if (!sender.hasPermission("hopperfilter.admin")) {
            sender.sendMessage(prefix + " " + hopperManager.getMessage("no-permission", "&cYou don't have permission to do this!"));
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            }
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(prefix + " " + hopperManager.getMessage("usage", "&7Usage: /hopperfilter <reload>"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("reload")) {
            plugin.reloadAllConfigs();
            hopperManager.loadHopperFilters();
            sender.sendMessage(prefix + " " + hopperManager.getMessage("reload-success", "&aConfiguration reloaded successfully!"));
        } else {
            sender.sendMessage(prefix + " " + hopperManager.getMessage("usage", "&7Usage: /hopperfilter <reload>"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!sender.hasPermission("hopperfilter.admin")) {
            return completions;
        }
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) {
                completions.add("reload");
            }
        }
        return completions;
    }
}