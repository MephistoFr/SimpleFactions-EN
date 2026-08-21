package com.simplefactions.commands;

import com.simplefactions.SimpleFactions;
import com.simplefactions.managers.PowerManager;
import com.simplefactions.models.ClaimResult;
import com.simplefactions.models.Faction;
import com.simplefactions.models.FactionPlayer;
import com.simplefactions.models.FactionRole;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FactionCommand implements CommandExecutor {

    private final SimpleFactions plugin;

    public FactionCommand(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().raw("console-only"));
            return true;
        }
        if (args.length == 0) {
            return gui(player);
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "gui":
                return gui(player);
            case "create":
                return create(player, args);
            case "disband":
                return disband(player);
            case "invite":
                return invite(player, args);
            case "join":
                return join(player, args);
            case "leave":
                return leave(player);
            case "kick":
                return kick(player, args);
            case "claim":
                return claim(player);
            case "unclaim":
                return unclaim(player);
            case "unclaimall":
                return unclaimall(player);
            case "sethome":
                return setHome(player);
            case "home":
                return home(player);
            case "info":
                return info(player, args);
            case "c":
            case "chat":
                return chat(player, args);
            case "promote":
                return promote(player, args);
            case "demote":
                return demote(player, args);
            case "reload":
                return reload(player);
            case "help":
            default:
                sendHelp(player);
                return true;
        }
    }

    private boolean hasPerm(Player player, String permission) {
        if (player.hasPermission("factions.command." + permission)) {
            return true;
        }
        player.sendMessage(plugin.getConfigManager().msg("no-permission"));
        return false;
    }

    private void sendHelp(Player player) {
        for (String line : plugin.getConfigManager().rawList("help")) {
            player.sendMessage(line);
        }
    }

    private boolean create(Player player, String[] args) {
        if (!hasPerm(player, "create")) {
            return true;
        }
        if (plugin.getFactionManager().getFactionByPlayer(player) != null) {
            player.sendMessage(plugin.getConfigManager().msg("already-in-faction"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-create"));
            return true;
        }
        String name = args[1];
        int maxLength = Math.max(3, plugin.getConfigManager().getMaxNameLength());
        if (name.length() > maxLength) {
            player.sendMessage(plugin.getConfigManager().msg("name-too-long", "%max%", String.valueOf(maxLength)));
            return true;
        }
        if (!name.matches("[a-zA-Z0-9_]{3," + maxLength + "}")) {
            player.sendMessage(plugin.getConfigManager().msg("name-invalid"));
            return true;
        }
        if (plugin.getFactionManager().factionExists(name)) {
            player.sendMessage(plugin.getConfigManager().msg("faction-already-exists", "%name%", name));
            return true;
        }
        Faction faction = plugin.getFactionManager().createFaction(player, name);
        player.sendMessage(plugin.getConfigManager().msg("faction-created", "%name%", faction.getName()));
        return true;
    }

    private boolean disband(Player player) {
        if (!hasPerm(player, "disband")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        String name = faction.getName();
        for (Player member : faction.getOnlineMembers()) {
            member.sendMessage(plugin.getConfigManager().msg("faction-disbanded", "%name%", name));
        }
        plugin.getFactionManager().disbandFaction(faction);
        return true;
    }

    private boolean invite(Player player, String[] args) {
        if (!hasPerm(player, "invite")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        FactionRole role = faction.getRole(player.getUniqueId());
        if (role != FactionRole.LEADER && role != FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-invite"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found", "%player%", args[1]));
            return true;
        }
        if (plugin.getFactionManager().getFactionByPlayer(target) != null) {
            player.sendMessage(plugin.getConfigManager().msg("target-already-in-faction", "%player%", target.getName()));
            return true;
        }
        if (faction.getMemberCount() >= plugin.getConfigManager().getMaxMembers()) {
            player.sendMessage(plugin.getConfigManager().msg("faction-full",
                    "%max%", String.valueOf(plugin.getConfigManager().getMaxMembers())));
            return true;
        }
        if (faction.getInvites().containsKey(target.getUniqueId().toString())) {
            player.sendMessage(plugin.getConfigManager().msg("already-invited", "%player%", target.getName()));
            return true;
        }
        plugin.getFactionManager().invite(faction, target.getUniqueId());
        target.sendMessage(plugin.getConfigManager().msg("you-were-invited", "%faction%", faction.getName()));
        plugin.getFactionManager().broadcast(faction,
                plugin.getConfigManager().msg("invited-notification", "%player%", target.getName()));
        return true;
    }

    private boolean join(Player player, String[] args) {
        if (!hasPerm(player, "join")) {
            return true;
        }
        if (plugin.getFactionManager().getFactionByPlayer(player) != null) {
            player.sendMessage(plugin.getConfigManager().msg("already-in-faction"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-join"));
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByName(args[1]);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("invalid-faction", "%faction%", args[1]));
            return true;
        }
        if (!faction.hasValidInvite(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().msg("no-invite", "%faction%", faction.getName()));
            return true;
        }
        if (!plugin.getFactionManager().join(faction, player)) {
            player.sendMessage(plugin.getConfigManager().msg("faction-full",
                    "%max%", String.valueOf(plugin.getConfigManager().getMaxMembers())));
            return true;
        }
        player.sendMessage(plugin.getConfigManager().msg("joined-faction", "%faction%", faction.getName()));
        plugin.getFactionManager().broadcast(faction,
                plugin.getConfigManager().msg("player-joined", "%player%", player.getName()));
        return true;
    }

    private boolean leave(Player player) {
        if (!hasPerm(player, "leave")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        if (faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().msg("cannot-leave-as-leader"));
            return true;
        }
        plugin.getFactionManager().leave(faction, player);
        player.sendMessage(plugin.getConfigManager().msg("left-faction"));
        plugin.getFactionManager().broadcast(faction,
                plugin.getConfigManager().msg("member-left", "%player%", player.getName()));
        return true;
    }

    private boolean kick(Player player, String[] args) {
        if (!hasPerm(player, "kick")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        FactionRole role = faction.getRole(player.getUniqueId());
        if (role != FactionRole.LEADER && role != FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-kick"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found", "%player%", args[1]));
            return true;
        }
        if (!faction.isMember(target.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-in-faction"));
            return true;
        }
        if (faction.getLeader().equals(target.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().msg("cannot-kick-leader"));
            return true;
        }
        if (role == FactionRole.OFFICER && faction.getRole(target.getUniqueId()) == FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        plugin.getFactionManager().kick(faction, target);
        target.sendMessage(plugin.getConfigManager().msg("you-were-kicked", "%faction%", faction.getName()));
        plugin.getFactionManager().broadcast(faction,
                plugin.getConfigManager().msg("player-kicked", "%player%", target.getName()));
        return true;
    }

    private boolean claim(Player player) {
        if (!hasPerm(player, "claim")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        Chunk chunk = player.getLocation().getChunk();
        ClaimResult result = plugin.getClaimManager().claim(faction, chunk);
        String key = com.simplefactions.managers.ClaimManager.chunkKey(chunk);
        String[] pos = key.split(";");
        switch (result) {
            case SUCCESS:
                player.sendMessage(plugin.getConfigManager().msg("claim-success",
                        "%x%", pos[1],
                        "%z%", pos[2],
                        "%claims%", String.valueOf(faction.getClaimCount()),
                        "%power%", PowerManager.formatPower(plugin.getFactionManager().getTotalPower(faction))));
                break;
            case SUCCESS_OVERCLAIM:
                player.sendMessage(plugin.getConfigManager().msg("claim-overclaim"));
                break;
            case ALREADY_OWNED:
                player.sendMessage(plugin.getConfigManager().msg("claim-already-owned"));
                break;
            case ENEMY_STRONG:
                player.sendMessage(plugin.getConfigManager().msg("claim-enemy-strong"));
                break;
            case NOT_ENOUGH_POWER:
                player.sendMessage(plugin.getConfigManager().msg("claim-not-enough-power"));
                break;
        }
        return true;
    }

    private boolean unclaim(Player player) {
        if (!hasPerm(player, "unclaim")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        Chunk chunk = player.getLocation().getChunk();
        if (!plugin.getClaimManager().unclaim(faction, chunk)) {
            player.sendMessage(plugin.getConfigManager().msg("unclaim-not-yours"));
            return true;
        }
        player.sendMessage(plugin.getConfigManager().msg("unclaim-success"));
        return true;
    }

    private boolean unclaimall(Player player) {
        if (!hasPerm(player, "unclaimall")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        FactionRole role = faction.getRole(player.getUniqueId());
        if (role != FactionRole.LEADER && role != FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("unclaimall-permission"));
            return true;
        }
        plugin.getClaimManager().unclaimAll(faction);
        player.sendMessage(plugin.getConfigManager().msg("unclaimall-success"));
        return true;
    }

    private boolean setHome(Player player) {
        if (!hasPerm(player, "sethome")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        faction.setHome(player.getLocation());
        player.sendMessage(plugin.getConfigManager().msg("home-set"));
        return true;
    }

    private boolean home(Player player) {
        if (!hasPerm(player, "home")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        Location home = faction.getHome();
        if (home == null) {
            player.sendMessage(plugin.getConfigManager().msg("home-not-set"));
            return true;
        }
        player.sendMessage(plugin.getConfigManager().msg("home-teleported"));
        player.teleportAsync(home);
        return true;
    }

    private boolean info(Player player, String[] args) {
        if (!hasPerm(player, "info")) {
            return true;
        }
        Faction faction;
        if (args.length >= 2) {
            faction = plugin.getFactionManager().getFactionByName(args[1]);
            if (faction == null) {
                player.sendMessage(plugin.getConfigManager().msg("invalid-faction", "%faction%", args[1]));
                return true;
            }
        } else {
            faction = plugin.getFactionManager().getFactionByPlayer(player);
            if (faction == null) {
                player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
                return true;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(plugin.getConfigManager().raw("info-header", "%name%", faction.getName())).append("\n");
        sb.append(plugin.getConfigManager().raw("info-leader", "%leader%",
                plugin.getFactionManager().getLeaderName(faction))).append("\n");
        sb.append(plugin.getConfigManager().raw("info-description", "%description%",
                faction.getDescription() == null ? "Aucune" : faction.getDescription())).append("\n");
        sb.append(plugin.getConfigManager().raw("info-members", "%count%",
                String.valueOf(faction.getMemberCount()))).append("\n");
        sb.append(plugin.getConfigManager().raw("info-power",
                "%power%", PowerManager.formatPower(plugin.getFactionManager().getTotalPower(faction)),
                "%max%", PowerManager.formatPower(plugin.getConfigManager().getMaxPower() * faction.getMemberCount()))).append("\n");
        sb.append(plugin.getConfigManager().raw("info-claims", "%claims%",
                String.valueOf(faction.getClaimCount()))).append("\n");
        sb.append(plugin.getConfigManager().raw("info-member-list"));
        for (Map.Entry<String, String> entry : faction.getMembers().entrySet()) {
            UUID memberId = UUID.fromString(entry.getKey());
            FactionPlayer fp = plugin.getPowerManager().getPlayer(memberId);
            String displayName = fp.getName() != null ? fp.getName() : entry.getKey();
            String status = Bukkit.getPlayer(memberId) != null ? "&a[En ligne]" : "&7[Hors ligne]";
            sb.append("\n").append(plugin.getConfigManager().raw("info-member-line",
                    "%role%", FactionRole.fromName(entry.getValue()).getDisplayName(),
                    "%player%", displayName,
                    "%status%", status));
        }
        player.sendMessage(sb.toString());
        return true;
    }

    private boolean chat(Player player, String[] args) {
        if (!hasPerm(player, "chat")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        if (args.length >= 2) {
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            plugin.getFactionManager().sendFactionChat(faction, player, message.toString().trim());
            return true;
        }
        FactionPlayer fp = plugin.getPowerManager().getPlayer(player);
        fp.setFactionChat(!fp.isFactionChat());
        player.sendMessage(plugin.getConfigManager().msg(fp.isFactionChat() ? "chat-toggled-on" : "chat-toggled-off"));
        return true;
    }

    private boolean promote(Player player, String[] args) {
        if (!hasPerm(player, "promote")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        FactionRole role = faction.getRole(player.getUniqueId());
        if (role != FactionRole.LEADER && role != FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-promote"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found", "%player%", args[1]));
            return true;
        }
        FactionRole targetRole = faction.getRole(target.getUniqueId());
        if (targetRole == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-in-faction"));
            return true;
        }
        FactionRole newRole = null;
        if (role == FactionRole.LEADER) {
            if (targetRole == FactionRole.RECRUIT) {
                newRole = FactionRole.MEMBER;
            } else if (targetRole == FactionRole.MEMBER) {
                newRole = FactionRole.OFFICER;
            }
        } else if (targetRole == FactionRole.RECRUIT) {
            newRole = FactionRole.MEMBER;
        }
        if (newRole == null) {
            player.sendMessage(plugin.getConfigManager().msg("cannot-promote"));
            return true;
        }
        faction.getMembers().put(target.getUniqueId().toString(), newRole.name());
        target.sendMessage(plugin.getConfigManager().msg("promoted", "%role%", newRole.getDisplayName()));
        plugin.getFactionManager().broadcast(faction, plugin.getConfigManager().msg("promoted-notification",
                "%player%", target.getName(),
                "%role%", newRole.getDisplayName()));
        return true;
    }

    private boolean demote(Player player, String[] args) {
        if (!hasPerm(player, "demote")) {
            return true;
        }
        Faction faction = plugin.getFactionManager().getFactionByPlayer(player);
        if (faction == null) {
            player.sendMessage(plugin.getConfigManager().msg("not-in-faction"));
            return true;
        }
        FactionRole role = faction.getRole(player.getUniqueId());
        if (role != FactionRole.LEADER && role != FactionRole.OFFICER) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission-role"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-demote"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found", "%player%", args[1]));
            return true;
        }
        FactionRole targetRole = faction.getRole(target.getUniqueId());
        if (targetRole == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-in-faction"));
            return true;
        }
        FactionRole newRole = null;
        if (role == FactionRole.LEADER) {
            if (targetRole == FactionRole.OFFICER) {
                newRole = FactionRole.MEMBER;
            } else if (targetRole == FactionRole.MEMBER) {
                newRole = FactionRole.RECRUIT;
            }
        } else if (targetRole == FactionRole.MEMBER) {
            newRole = FactionRole.RECRUIT;
        }
        if (newRole == null) {
            player.sendMessage(plugin.getConfigManager().msg("cannot-demote"));
            return true;
        }
        faction.getMembers().put(target.getUniqueId().toString(), newRole.name());
        target.sendMessage(plugin.getConfigManager().msg("demoted", "%role%", newRole.getDisplayName()));
        plugin.getFactionManager().broadcast(faction, plugin.getConfigManager().msg("demoted-notification",
                "%player%", target.getName(),
                "%role%", newRole.getDisplayName()));
        return true;
    }

    private boolean gui(Player player) {
        if (!hasPerm(player, "gui")) {
            return true;
        }
        plugin.getGui().openMain(player);
        return true;
    }

    private boolean reload(Player player) {
        if (!player.hasPermission("factions.admin")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return true;
        }
        plugin.getConfigManager().reload();
        plugin.getClaimManager().rebuildIndex();
        player.sendMessage(plugin.getConfigManager().msg("reload-success"));
        return true;
    }
}
