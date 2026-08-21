package com.simplefactions.managers;

import com.simplefactions.SimpleFactions;
import com.simplefactions.models.ClaimResult;
import com.simplefactions.models.Faction;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClaimManager {

    private final SimpleFactions plugin;
    private final Map<String, String> claimIndex = new HashMap<>();

    public ClaimManager(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    public static String chunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
    }

    public void rebuildIndex() {
        claimIndex.clear();
        for (Faction faction : plugin.getFactionManager().getFactions()) {
            for (String claim : faction.getClaims()) {
                claimIndex.put(claim, faction.getId());
            }
        }
    }

    public void removeClaim(String key) {
        claimIndex.remove(key);
    }

    public boolean isClaimed(String key) {
        return claimIndex.containsKey(key);
    }

    public Faction getFactionAt(String key) {
        String id = claimIndex.get(key);
        if (id == null) {
            return null;
        }
        return plugin.getFactionManager().getFactionById(id);
    }

    public Faction getFactionAt(Chunk chunk) {
        return getFactionAt(chunkKey(chunk));
    }

    public ClaimResult claim(Faction faction, Chunk chunk) {
        String key = chunkKey(chunk);
        Faction owner = getFactionAt(key);
        if (owner == null) {
            if (canClaim(faction)) {
                faction.addClaim(key);
                claimIndex.put(key, faction.getId());
                return ClaimResult.SUCCESS;
            }
            return ClaimResult.NOT_ENOUGH_POWER;
        }
        if (owner.getId().equals(faction.getId())) {
            return ClaimResult.ALREADY_OWNED;
        }
        if (!plugin.getConfigManager().isOverclaimEnabled()) {
            return ClaimResult.ENEMY_STRONG;
        }
        if (plugin.getFactionManager().getTotalPower(owner) >= owner.getClaimCount()) {
            return ClaimResult.ENEMY_STRONG;
        }
        if (!canClaim(faction)) {
            return ClaimResult.NOT_ENOUGH_POWER;
        }
        owner.removeClaim(key);
        faction.addClaim(key);
        claimIndex.put(key, faction.getId());
        return ClaimResult.SUCCESS_OVERCLAIM;
    }

    public boolean unclaim(Faction faction, Chunk chunk) {
        String key = chunkKey(chunk);
        Faction owner = getFactionAt(key);
        if (owner == null || !owner.getId().equals(faction.getId())) {
            return false;
        }
        faction.removeClaim(key);
        claimIndex.remove(key);
        return true;
    }

    public int unclaimAll(Faction faction) {
        int removed = 0;
        for (String claim : new HashSet<>(faction.getClaims())) {
            faction.removeClaim(claim);
            claimIndex.remove(claim);
            removed++;
        }
        return removed;
    }

    public boolean canClaim(Faction faction) {
        double totalPower = plugin.getFactionManager().getTotalPower(faction);
        return totalPower > faction.getClaimCount();
    }
}
