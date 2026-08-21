package com.simplefactions.listeners;

import com.simplefactions.SimpleFactions;
import com.simplefactions.gui.FactionGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {

    private final SimpleFactions plugin;

    public GuiListener(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String title = event.getView().getTitle();
        if (!title.startsWith(FactionGui.PREFIX)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        String name = clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()
                ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName())
                : "";
        FactionGui gui = plugin.getGui();

        if (name.equals("Retour")) {
            gui.openMain(player);
            return;
        }
        if (name.equals("Page suivante")) {
            gui.openCommands(player, FactionGui.commandPageFromTitle(title) + 1);
            return;
        }
        if (name.equals("Page précédente")) {
            gui.openCommands(player, FactionGui.commandPageFromTitle(title) - 1);
            return;
        }

        if (title.equals(FactionGui.MAIN)) {
            switch (name) {
                case "Commandes":
                    gui.openCommands(player, 1);
                    break;
                case "Rôles":
                    gui.openRoles(player);
                    break;
                case "Power":
                    gui.openPower(player);
                    break;
                case "Territoire":
                    gui.openClaims(player);
                    break;
                case "Overclaim":
                    gui.openOverclaim(player);
                    break;
                case "Chat de faction":
                    gui.openChat(player);
                    break;
                case "QG & Home":
                    gui.openHome(player);
                    break;
                case "Informations":
                    gui.openInfo(player);
                    break;
                case "Administration":
                    gui.openAdmin(player);
                    break;
                default:
                    break;
            }
        }
    }
}
