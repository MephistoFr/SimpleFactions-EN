package com.simplefactions.listeners;

import com.simplefactions.SimpleFactions;
import com.simplefactions.models.Faction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionListener implements Listener {

    private final SimpleFactions plugin;

    public ProtectionListener(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isAllowed(event.getPlayer(), event.getBlock().getLocation())) {
            return;
        }
        deny(event.getPlayer(), event.getBlock().getLocation());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isAllowed(event.getPlayer(), event.getBlock().getLocation())) {
            return;
        }
        deny(event.getPlayer(), event.getBlock().getLocation());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (!isInteractive(clicked.getType())) {
            return;
        }
        if (isAllowed(event.getPlayer(), clicked.getLocation())) {
            return;
        }
        deny(event.getPlayer(), clicked.getLocation());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (isAllowed(event.getPlayer(), event.getRightClicked().getLocation())) {
            return;
        }
        deny(event.getPlayer(), event.getRightClicked().getLocation());
        event.setCancelled(true);
    }

    private boolean isAllowed(Player player, Location location) {
        Faction claimOwner = plugin.getClaimManager().getFactionAt(location.getChunk());
        if (claimOwner == null) {
            return true;
        }
        Faction playerFaction = plugin.getFactionManager().getFactionByPlayer(player);
        return playerFaction != null && playerFaction.getId().equals(claimOwner.getId());
    }

    private void deny(Player player, Location location) {
        Faction claimOwner = plugin.getClaimManager().getFactionAt(location.getChunk());
        String name = claimOwner == null ? "?" : claimOwner.getName();
        player.sendMessage(plugin.getConfigManager().msg("protection-denied", "%faction%", name));
    }

    private boolean isInteractive(Material material) {
        String name = material.name();
        return name.equals("CHEST")
                || name.equals("TRAPPED_CHEST")
                || name.equals("BARREL")
                || name.equals("SHULKER_BOX")
                || name.endsWith("_SHULKER_BOX")
                || name.equals("FURNACE")
                || name.equals("BLAST_FURNACE")
                || name.equals("SMOKER")
                || name.equals("HOPPER")
                || name.equals("DISPENSER")
                || name.equals("DROPPER")
                || name.equals("BREWING_STAND")
                || name.equals("ANVIL")
                || name.endsWith("_ANVIL")
                || name.equals("ENCHANTING_TABLE")
                || name.equals("CRAFTING_TABLE")
                || name.equals("GRINDSTONE")
                || name.equals("STONECUTTER")
                || name.equals("CARTOGRAPHY_TABLE")
                || name.equals("SMITHING_TABLE")
                || name.equals("LOOM")
                || name.equals("BEACON")
                || name.equals("NOTE_BLOCK")
                || name.equals("JUKEBOX")
                || name.equals("LEVER")
                || name.endsWith("_BUTTON")
                || name.endsWith("_DOOR")
                || name.endsWith("_TRAPDOOR")
                || name.endsWith("_FENCE_GATE")
                || name.equals("DRAGON_EGG")
                || name.equals("BELL")
                || name.equals("COMPOSTER")
                || name.equals("CAULDRON")
                || name.endsWith("_CAULDRON")
                || name.equals("FLOWER_POT")
                || name.equals("DECORATED_POT")
                || name.equals("LECTERN");
    }
}
