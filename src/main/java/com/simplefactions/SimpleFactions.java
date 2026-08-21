package com.simplefactions;

import com.simplefactions.commands.FactionCommand;
import com.simplefactions.commands.FactionTabCompleter;
import com.simplefactions.gui.FactionGui;
import com.simplefactions.listeners.GuiListener;
import com.simplefactions.listeners.PlayerListener;
import com.simplefactions.listeners.ProtectionListener;
import com.simplefactions.managers.ClaimManager;
import com.simplefactions.managers.ConfigManager;
import com.simplefactions.managers.FactionManager;
import com.simplefactions.managers.PowerManager;
import com.simplefactions.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleFactions extends JavaPlugin {

    private static SimpleFactions instance;

    private ConfigManager configManager;
    private FactionManager factionManager;
    private PowerManager powerManager;
    private ClaimManager claimManager;
    private StorageManager storageManager;
    private FactionGui gui;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        factionManager = new FactionManager(this);
        powerManager = new PowerManager(this);
        claimManager = new ClaimManager(this);
        storageManager = new StorageManager(this);
        gui = new FactionGui(this);

        storageManager.loadAll();
        claimManager.rebuildIndex();

        PluginCommand command = getCommand("factions");
        if (command != null) {
            FactionCommand factionCommand = new FactionCommand(this);
            FactionTabCompleter tabCompleter = new FactionTabCompleter(this);
            command.setExecutor(factionCommand);
            command.setTabCompleter(tabCompleter);
        }

        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        startTasks();

        getLogger().info("SimpleFactions v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (storageManager != null) {
            storageManager.saveAll();
        }
        instance = null;
    }

    private void startTasks() {
        long regenInterval = configManager.getPowerRegenDelaySeconds() * 20L;
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                powerManager.regen(powerManager.getPlayer(player));
            }
        }, regenInterval, regenInterval);

        long autosaveTicks = configManager.getAutosaveMinutes() * 60L * 20L;
        getServer().getScheduler().runTaskTimer(this, storageManager::saveAll, autosaveTicks, autosaveTicks);
    }

    public static SimpleFactions getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FactionManager getFactionManager() {
        return factionManager;
    }

    public PowerManager getPowerManager() {
        return powerManager;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public FactionGui getGui() {
        return gui;
    }
}
