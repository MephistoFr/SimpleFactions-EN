package com.simplefactions.listeners;

import com.simplefactions.SimpleFactions;
import com.simplefactions.models.Faction;
import com.simplefactions.models.FactionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final SimpleFactions plugin;

    public PlayerListener(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FactionPlayer fp = plugin.getPowerManager().getPlayer(player);
        if (fp.getLastPowerRegen() <= 0) {
            fp.setLastPowerRegen(System.currentTimeMillis());
        } else {
            plugin.getPowerManager().regen(fp);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getStorageManager().saveAll();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        plugin.getPowerManager().handleDeath(victim, killer);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FactionPlayer fp = plugin.getPowerManager().getPlayer(player);
        if (!fp.isFactionChat()) {
            return;
        }
        event.setCancelled(true);
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            fp.setFactionChat(false);
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return;
        }
        plugin.getFactionManager().sendFactionChat(faction, player, event.getMessage());
    }
}
