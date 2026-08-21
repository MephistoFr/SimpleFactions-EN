package com.simplefactions.managers;

import com.simplefactions.SimpleFactions;
import com.simplefactions.models.Faction;
import com.simplefactions.models.FactionPlayer;
import com.simplefactions.models.FactionRole;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FactionManager {

    private final SimpleFactions plugin;
    private final Map<String, Faction> factionsById = new HashMap<>();
    private final Map<String, Faction> factionsByName = new HashMap<>();

    public FactionManager(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    public Faction createFaction(Player creator, String name) {
        Faction faction = new Faction(UUID.randomUUID().toString(), name, creator.getUniqueId());
        faction.addMember(creator.getUniqueId(), FactionRole.LEADER);
        factionsById.put(faction.getId(), faction);
        factionsByName.put(faction.getName().toLowerCase(Locale.ROOT), faction);
        FactionPlayer fp = plugin.getPowerManager().getPlayer(creator);
        fp.setFactionId(faction.getId());
        return faction;
    }

    public boolean disbandFaction(Faction faction) {
        if (faction == null) {
            return false;
        }
        for (String member : faction.getMemberUUIDs()) {
            FactionPlayer fp = plugin.getPowerManager().getPlayerOrNull(UUID.fromString(member));
            if (fp != null) {
                fp.setFactionId(null);
            }
        }
        for (String claim : faction.getClaims()) {
            plugin.getClaimManager().removeClaim(claim);
        }
        factionsById.remove(faction.getId());
        factionsByName.remove(faction.getName().toLowerCase(Locale.ROOT));
        return true;
    }

    public Faction getFactionByPlayer(Player player) {
        return getFactionByPlayer(player.getUniqueId());
    }

    public Faction getFactionByPlayer(UUID uuid) {
        FactionPlayer fp = plugin.getPowerManager().getPlayer(uuid);
        if (fp.getFactionId() == null) {
            return null;
        }
        return factionsById.get(fp.getFactionId());
    }

    public Faction getFactionByName(String name) {
        return factionsByName.get(name.toLowerCase(Locale.ROOT));
    }

    public Faction getFactionById(String id) {
        return factionsById.get(id);
    }

    public boolean factionExists(String name) {
        return getFactionByName(name) != null;
    }

    public Collection<Faction> getFactions() {
        return factionsById.values();
    }

    public List<String> getFactionNames() {
        List<String> names = new ArrayList<>();
        for (Faction faction : factionsById.values()) {
            names.add(faction.getName());
        }
        return names;
    }

    public void registerLoadedFaction(Faction faction) {
        factionsById.put(faction.getId(), faction);
        factionsByName.put(faction.getName().toLowerCase(Locale.ROOT), faction);
    }

    public void invite(Faction faction, UUID target) {
        long expiry = System.currentTimeMillis() + plugin.getConfigManager().getInviteExpireSeconds() * 1000L;
        faction.addInvite(target, expiry);
    }

    public boolean join(Faction faction, Player player) {
        int max = plugin.getConfigManager().getMaxMembers();
        if (faction.getMemberCount() >= max) {
            return false;
        }
        faction.addMember(player.getUniqueId(), FactionRole.RECRUIT);
        faction.removeInvite(player.getUniqueId());
        FactionPlayer fp = plugin.getPowerManager().getPlayer(player);
        fp.setFactionId(faction.getId());
        return true;
    }

    public boolean leave(Faction faction, Player player) {
        if (faction.getLeader().equals(player.getUniqueId())) {
            return false;
        }
        faction.removeMember(player.getUniqueId());
        FactionPlayer fp = plugin.getPowerManager().getPlayer(player);
        fp.setFactionId(null);
        return true;
    }

    public boolean kick(Faction faction, Player target) {
        if (faction.getLeader().equals(target.getUniqueId())) {
            return false;
        }
        faction.removeMember(target.getUniqueId());
        FactionPlayer fp = plugin.getPowerManager().getPlayer(target);
        fp.setFactionId(null);
        return true;
    }

    public double getTotalPower(Faction faction) {
        double total = 0;
        for (String member : faction.getMemberUUIDs()) {
            FactionPlayer fp = plugin.getPowerManager().getPlayer(UUID.fromString(member));
            total += fp.getPower();
        }
        return total;
    }

    public String getLeaderName(Faction faction) {
        FactionPlayer fp = plugin.getPowerManager().getPlayer(faction.getLeader());
        if (fp.getName() != null) {
            return fp.getName();
        }
        return faction.getLeader().toString().substring(0, 8);
    }

    public void broadcast(Faction faction, String message) {
        for (Player member : faction.getOnlineMembers()) {
            member.sendMessage(message);
        }
    }

    public void sendFactionChat(Faction faction, Player sender, String message) {
        String formatted = plugin.getConfigManager().raw("chat-format",
                "%faction%", faction.getName(),
                "%player%", sender.getName(),
                "%message%", message);
        for (Player member : faction.getOnlineMembers()) {
            member.sendMessage(formatted);
        }
        plugin.getLogger().info(formatted.replace('\u00A7', '&'));
    }
}
