package com.simplefactions.gui;

import com.simplefactions.SimpleFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactionGui {

    public static final String PREFIX = "§8[§6Factions§8] §7";
    public static final String MAIN = PREFIX + "Main menu";
    public static final String COMMANDS = PREFIX + "Commands §8(";
    public static final String ROLES = PREFIX + "Roles";
    public static final String POWER = PREFIX + "Power";
    public static final String CLAIMS = PREFIX + "Territory";
    public static final String OVERCLAIM = PREFIX + "Overclaim";
    public static final String CHAT = PREFIX + "Chat";
    public static final String HOME = PREFIX + "HQ";
    public static final String INFO = PREFIX + "Information";
    public static final String ADMIN = PREFIX + "Administration";

    private static final ItemStack BACK = item(Material.ARROW, "&cBack", "&7Return to the main menu");

    public FactionGui(SimpleFactions plugin) {
    }

    public static int commandPageFromTitle(String title) {
        if (!title.startsWith(COMMANDS)) {
            return 1;
        }
        String rest = title.substring(COMMANDS.length());
        int slash = rest.indexOf('/');
        if (slash <= 0) {
            return 1;
        }
        try {
            return Integer.parseInt(rest.substring(0, slash).trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static ItemStack item(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lines = new ArrayList<>();
        for (String s : lore) {
            lines.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lines);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack glow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    private static void fillBorder(Inventory inv) {
        ItemStack border = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        int rows = inv.getSize() / 9;
        for (int i = 0; i < inv.getSize(); i++) {
            int row = i / 9;
            int col = i % 9;
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                inv.setItem(i, border);
            }
        }
    }

    private static final class CommandInfo {
        final Material material;
        final String name;
        final List<String> lore;

        CommandInfo(Material material, String name, String... lore) {
            this.material = material;
            this.name = name;
            this.lore = new ArrayList<>(Arrays.asList(lore));
        }
    }

    private static final List<CommandInfo> COMMANDS_LIST = Arrays.asList(
            new CommandInfo(Material.IRON_SWORD, "&6/f create <name>",
                    "&7Create a new faction.",
                    "&7You become the Leader.",
                    "&7Name: 3 to 16 letters,",
                    "&7numbers or underscores."),
            new CommandInfo(Material.BARRIER, "&6/f disband",
                    "&7Disband your faction.",
                    "&7Leader only.",
                    "&7Removes the faction and",
                    "&7all its claims."),
            new CommandInfo(Material.NAME_TAG, "&6/f invite <player>",
                    "&7Invite a player.",
                    "&7Leader and Mods.",
                    "&7The invitation expires",
                    "&7after 5 minutes."),
            new CommandInfo(Material.ENDER_PEARL, "&6/f join <faction>",
                    "&7Join a faction.",
                    "&7Requires a pending",
                    "&7invitation.",
                    "&7You receive the Recruit rank."),
            new CommandInfo(Material.OAK_DOOR, "&6/f leave",
                    "&7Leave your faction.",
                    "&7Cannot be used by the Leader",
                    "&7(use /f disband)."),
            new CommandInfo(Material.IRON_AXE, "&6/f kick <player>",
                    "&7Kick a player.",
                    "&7Leader and Mods.",
                    "&7The Leader cannot",
                    "&7be kicked."),
            new CommandInfo(Material.GOLD_INGOT, "&6/f promote <player>",
                    "&7Promote a member.",
                    "&7Leader/Mod.",
                    "&7Order: Recruit, Member,",
                    "&7Mod, Leader."),
            new CommandInfo(Material.IRON_INGOT, "&6/f demote <player>",
                    "&7Demote a member.",
                    "&7Leader/Mod.",
                    "&7Reverse of promote."),
            new CommandInfo(Material.GRASS_BLOCK, "&6/f claim",
                    "&7Claim the current chunk",
                    "&7(16x16).",
                    "&7Condition: total power",
                    "&7> number of claims."),
            new CommandInfo(Material.DIAMOND_SHOVEL, "&6/f unclaim",
                    "&7Remove the current chunk",
                    "&7from your territory.",
                    "&7Only works on",
                    "&7your own claims."),
            new CommandInfo(Material.NETHERITE_SHOVEL, "&6/f unclaimall",
                    "&7Remove all your claims.",
                    "&7Leader and Mods only."),
            new CommandInfo(Material.RED_BED, "&6/f sethome",
                    "&7Set the faction HQ",
                    "&7at your position.",
                    "&7Can be changed at any time."),
            new CommandInfo(Material.ENDER_PEARL, "&6/f home",
                    "&7Teleport to your",
                    "&7faction's HQ."),
            new CommandInfo(Material.BOOK, "&6/f info [faction]",
                    "&7Faction statistics:",
                    "&7leader, members, roles,",
                    "&7total power, claims.",
                    "&7Without argument: your faction."),
            new CommandInfo(Material.PAPER, "&6/f c <message>",
                    "&7Send a message to",
                    "&7the faction chat.",
                    "&7Visible only to",
                    "&7faction members."),
            new CommandInfo(Material.WRITABLE_BOOK, "&6/f c (no message)",
                    "&7Toggle between public",
                    "&7and faction chat.",
                    "&7Your messages are then",
                    "&7automatically redirected."),
            new CommandInfo(Material.CHEST, "&6/f gui",
                    "&7Open this interactive guide.",
                    "&7Complete explanation",
                    "&7of the plugin."),
            new CommandInfo(Material.OAK_SIGN, "&6/f help",
                    "&7Display the text-based",
                    "&7command help."),
            new CommandInfo(Material.ANVIL, "&6/f reload",
                    "&7Reload config.yml.",
                    "&7Permission: factions.admin.",
                    "&7Reserved for admins.")
    );

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, MAIN);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.COMMAND_BLOCK, "&6Commands",
                "&7All the plugin commands",
                "&7explained in detail.")));
        inv.setItem(11, item(Material.GOLDEN_HELMET, "&6Roles",
                "&7Leader, Mod, Member, Recruit.",
                "&7What can each rank do?"));
        inv.setItem(12, item(Material.TOTEM_OF_UNDYING, "&6Power",
                "&7Individual and collective power.",
                "&7Deaths, kills, regeneration."));
        inv.setItem(13, item(Material.GRASS_BLOCK, "&6Territory",
                "&7Chunk claims.",
                "&7Protection and conditions."));
        inv.setItem(14, item(Material.DIAMOND_SWORD, "&6Overclaim",
                "&7Conquer the territory",
                "&7of a weakened faction."));
        inv.setItem(15, item(Material.PAPER, "&6Faction chat",
                "&7Communicate between members.",
                "&7Public chat / faction chat."));
        inv.setItem(16, item(Material.COMPASS, "&6HQ & Home",
                "&7Set the faction's HQ",
                "&7and teleport to it."));
        inv.setItem(21, item(Material.BOOK, "&6Information",
                "&7View the statistics",
                "&7of a faction (/f info)."));
        inv.setItem(22, item(Material.ANVIL, "&6Administration",
                "&7Reload the configuration.",
                "&7Permission: factions.admin."));
        player.openInventory(inv);
    }

    public void openCommands(Player player, int page) {
        int total = (COMMANDS_LIST.size() + 7) / 8;
        page = Math.max(1, Math.min(page, total));
        Inventory inv = Bukkit.createInventory(null, 45, COMMANDS + page + "/" + total + "§8)");
        fillBorder(inv);
        int start = (page - 1) * 8;
        int slot = 10;
        for (int i = start; i < Math.min(start + 8, COMMANDS_LIST.size()); i++) {
            CommandInfo info = COMMANDS_LIST.get(i);
            inv.setItem(slot++, glow(item(info.material, info.name, info.lore.toArray(new String[0]))));
        }
        inv.setItem(30, BACK);
        if (page > 1) {
            inv.setItem(32, item(Material.ARROW, "&ePrevious page"));
        }
        inv.setItem(33, glow(item(Material.PAPER, "&ePage &6" + page + "&e/&6" + total)));
        if (page < total) {
            inv.setItem(34, item(Material.ARROW, "&eNext page"));
        }
        player.openInventory(inv);
    }

    public void openRoles(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ROLES);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.GOLDEN_HELMET, "&6Leader",
                "&7Highest rank.",
                "&7- Creates and disbands the faction.",
                "&7- Invites, kicks, promotes.",
                "&7- Cannot leave the faction.")));
        inv.setItem(11, glow(item(Material.CHAINMAIL_HELMET, "&6Mod (Officer)",
                "&7Second rank.",
                "&7- Invites players.",
                "&7- Kicks Members/Recruits.",
                "&7- Promotes Recruit to Member.",
                "&7- Cannot kick another Mod.")));
        inv.setItem(12, item(Material.IRON_HELMET, "&6Member",
                "&7Intermediate rank.",
                "&7- Claim and unclaim chunks.",
                "&7- Set the faction HQ.",
                "&7- Use the faction chat."));
        inv.setItem(13, item(Material.LEATHER_HELMET, "&6Recruit",
                "&7Initial rank.",
                "&7- Obtained upon joining.",
                "&7- Claim, HQ and chat available.",
                "&7- Promoted by a Mod/Leader."));
        inv.setItem(15, item(Material.BLAZE_POWDER, "&6Promotion / Demotion",
                "&7/f promote <player>",
                "&7/f demote <player>",
                "&7Order: Recruit < Member <",
                "&7Mod < Leader",
                "&7The Leader promotes up to Mod."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openPower(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, POWER);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.TOTEM_OF_UNDYING, "&6Maximum power",
                "&7Each player has at most",
                "&e10 &7power points",
                "&7(configurable in config.yml).",
                "&7The faction's total power is",
                "&7the sum of its members' power.")));
        inv.setItem(11, item(Material.SKELETON_SKULL, "&6Death",
                "&7-&e2 power &7on each death.",
                "&7Power never goes",
                "&7below 0."));
        inv.setItem(12, item(Material.DIAMOND_SWORD, "&6Kill",
                "&7+&e1 power &7for the killer",
                "&7when eliminating a player."));
        inv.setItem(13, item(Material.CLOCK, "&6Regeneration",
                "&7+&e1 power per minute&7.",
                "&7Regeneration works even",
                "&7offline (caught up",
                "&7on reconnect)."));
        inv.setItem(14, item(Material.REDSTONE, "&6Power usage",
                "&7Power is used to claim:",
                "&7total power &c>&7 number of claims.",
                "&7Ex: 30 power = 30 claims max."));
        inv.setItem(15, item(Material.NAME_TAG, "&6Joining a faction",
                "&7+&e1 power &7when joining",
                "&7a faction."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openClaims(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, CLAIMS);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.GRASS_BLOCK, "&6Chunk claim",
                "&7/f claim",
                "&7Claim the chunk (16x16)",
                "&7you are currently in.")));
        inv.setItem(11, item(Material.DIAMOND_SHOVEL, "&6Unclaim",
                "&7/f unclaim",
                "&7Remove the current chunk",
                "&7from your territory."));
        inv.setItem(12, item(Material.NETHERITE_SHOVEL, "&6Unclaim all",
                "&7/f unclaimall",
                "&7Remove all your claims.",
                "&7Leader and Mods only."));
        inv.setItem(13, item(Material.SHIELD, "&6Protection",
                "&7In your claims, outside",
                "&7players cannot:",
                "&7- Break/place blocks",
                "&7- Open chests/doors",
                "&7- Use interactive blocks",
                "&7Your members are allowed."));
        inv.setItem(14, item(Material.LEVER, "&6Claim condition",
                "&7total power &c>&7 number of claims.",
                "&7Each claimed chunk costs",
                "&71 power point."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openOverclaim(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, OVERCLAIM);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.DIAMOND_SWORD, "&6Overclaim",
                "&7Conquer the territory of a",
                "&7weakened enemy faction.")));
        inv.setItem(11, item(Material.LEVER, "&6Overclaim condition",
                "&7The enemy faction is",
                "&7overclaimable if its total power",
                "&7is &cstrictly less than",
                "&cits number of claims."));
        inv.setItem(12, item(Material.BOOK, "&6Example",
                "&7The enemy faction has",
                "&77 claims and 6 power.",
                "&7Its territory is",
                "&7vulnerable. Claim it!",
                "&7(Your faction must also",
                "&7have power > claims)"));
        inv.setItem(13, item(Material.REDSTONE_TORCH, "&6Enable",
                "&7Overclaim is configured in",
                "&7config.yml: settings.overclaim"));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openChat(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, CHAT);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.PAPER, "&6Send a message",
                "&7/f c <message>",
                "&7Sends a message visible",
                "&7only to members",
                "&7of your faction.")));
        inv.setItem(11, item(Material.WRITABLE_BOOK, "&6Chat mode",
                "&7/f c without a message",
                "&7toggles between public chat",
                "&7and faction chat. Your messages",
                "&7are then automatically",
                "&7redirected."));
        inv.setItem(12, item(Material.OAK_SIGN, "&6Display format",
                "&7[FactionName]",
                "&7Player: message",
                "&7Ex: [Vikings]",
                "&7   Martin: Hello!"));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openHome(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, HOME);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.RED_BED, "&6Set the HQ",
                "&7/f sethome",
                "&7Sets the faction HQ",
                "&7at your current position.",
                "&7Leader and Mods only.")));
        inv.setItem(11, item(Material.ENDER_PEARL, "&6Teleportation",
                "&7/f home",
                "&7Teleports you to your",
                "&7faction's HQ.",
                "&7Available to all members."));
        inv.setItem(12, item(Material.BARRIER, "&6Remove the HQ",
                "&7/f delhome",
                "&7Removes the faction HQ.",
                "&7Leader and Mods only."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openInfo(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, INFO);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.BOOK, "&6Your faction",
                "&7/f info",
                "&7Displays your faction's",
                "&7statistics: leader, members,",
                "&7roles, total power, claims.")));
        inv.setItem(11, item(Material.KNOWLEDGE_BOOK, "&6Other faction",
                "&7/f info <faction>",
                "&7Displays another faction's",
                "&7statistics (exact name)."));
        inv.setItem(12, item(Material.PAPER, "&6Shortcut",
                "&7/f info also works with",
                "&7tab completion."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }

    public void openAdmin(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ADMIN);
        fillBorder(inv);
        inv.setItem(10, glow(item(Material.ANVIL, "&6Reload the configuration",
                "&7/f reload",
                "&7Reloads config.yml.",
                "&7Permission: &efactions.admin&7.")));
        inv.setItem(11, item(Material.COMMAND_BLOCK, "&6Data saving",
                "&7Data is stored in",
                "&7/plugins/SimpleFactions/data/",
                "&7- factions.json",
                "&7- players.json",
                "&7Auto-save every 5 minutes",
                "&7and on server shutdown."));
        inv.setItem(12, item(Material.NAME_TAG, "&6Main settings",
                "&7Everything is configurable",
                "&7in config.yml: power, delays,",
                "&7messages, limits...",
                "&7Then /f reload."));
        inv.setItem(31, BACK);
        player.openInventory(inv);
    }
}