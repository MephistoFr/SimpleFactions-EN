package com.simplefactions.managers;

import com.simplefactions.SimpleFactions;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final SimpleFactions plugin;
    private FileConfiguration config;

    public ConfigManager(SimpleFactions plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getPrefix() {
        return config.getString("messages.prefix", "&8[&6Factions&8]&r");
    }

    public String msg(String key, String... placeholders) {
        return colorize(getPrefix() + " " + config.getString("messages." + key, key), placeholders);
    }

    public String raw(String key, String... placeholders) {
        return colorize(config.getString("messages." + key, key), placeholders);
    }

    public List<String> rawList(String key) {
        List<String> lines = config.getStringList("messages." + key);
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(colorize(line));
        }
        return result;
    }

    private String colorize(String message, String... placeholders) {
        if (placeholders != null) {
            for (int i = 0; i + 1 < placeholders.length; i += 2) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public double getMaxPower() {
        return config.getDouble("settings.max-power", 10.0);
    }

    public double getPowerPerDeath() {
        return config.getDouble("settings.power-per-death", -2.0);
    }

    public double getPowerPerKill() {
        return config.getDouble("settings.power-per-kill", 1.0);
    }

    public double getPowerRegenPerMinute() {
        return config.getDouble("settings.power-regen-per-min", 1.0);
    }

    public long getPowerRegenDelaySeconds() {
        return config.getLong("settings.power-regen-delay-seconds", 30);
    }

    public int getMaxMembers() {
        return config.getInt("settings.max-members", 50);
    }

    public long getInviteExpireSeconds() {
        return config.getLong("settings.invite-expire-seconds", 300);
    }

    public int getMaxNameLength() {
        return config.getInt("settings.max-name-length", 16);
    }

    public boolean isOverclaimEnabled() {
        return config.getBoolean("settings.overclaim", true);
    }

    public int getAutosaveMinutes() {
        return config.getInt("settings.autosave-minutes", 5);
    }
}
