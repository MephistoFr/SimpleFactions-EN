package com.simplefactions.commands;

import com.simplefactions.SimpleFactions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FactionTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "create", "disband", "invite", "join", "leave", "kick",
            "claim", "unclaim", "unclaimall", "sethome", "home",
            "info", "chat", "c", "promote", "demote", "reload", "gui", "help"
    );

    private final SimpleFactions plugin;

    public FactionTabCompleter(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(prefix)) {
                    result.add(sub);
                }
            }
            return result;
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            if (sub.equals("join") || sub.equals("info")) {
                for (String name : plugin.getFactionManager().getFactionNames()) {
                    if (name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                        result.add(name);
                    }
                }
            } else if (sub.equals("invite") || sub.equals("kick") || sub.equals("promote") || sub.equals("demote")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                        result.add(player.getName());
                    }
                }
            }
            return result;
        }
        return new ArrayList<>();
    }
}