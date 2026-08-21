package com.simplefactions.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Faction {

    private String id;
    private String name;
    private String description;
    private UUID leader;
    private long createdAt;

    private Map<String, String> members = new HashMap<>();
    private Map<String, Long> invites = new HashMap<>();
    private Set<String> claims = new HashSet<>();

    private String homeWorld;
    private double homeX;
    private double homeY;
    private double homeZ;
    private float homeYaw;
    private float homePitch;
    private boolean homeSet;

    public Faction() {
    }

    public Faction(String id, String name, UUID leader) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public void addMember(UUID player, FactionRole role) {
        members.put(player.toString(), role.name());
    }

    public void removeMember(UUID player) {
        members.remove(player.toString());
    }

    public FactionRole getRole(UUID player) {
        String role = members.get(player.toString());
        return role == null ? null : FactionRole.fromName(role);
    }

    public boolean isMember(UUID player) {
        return members.containsKey(player.toString());
    }

    public int getMemberCount() {
        return members.size();
    }

    public Set<String> getMemberUUIDs() {
        return members.keySet();
    }

    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (String uuid : members.keySet()) {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null) {
                online.add(player);
            }
        }
        return online;
    }

    public Map<String, Long> getInvites() {
        return invites;
    }

    public void setInvites(Map<String, Long> invites) {
        this.invites = invites;
    }

    public void addInvite(UUID player, long expiry) {
        invites.put(player.toString(), expiry);
    }

    public void removeInvite(UUID player) {
        invites.remove(player.toString());
    }

    public boolean hasValidInvite(UUID player) {
        Long expiry = invites.get(player.toString());
        if (expiry == null) {
            return false;
        }
        if (expiry < System.currentTimeMillis()) {
            invites.remove(player.toString());
            return false;
        }
        return true;
    }

    public Set<String> getClaims() {
        return claims;
    }

    public void setClaims(Set<String> claims) {
        this.claims = claims;
    }

    public boolean hasClaim(String key) {
        return claims.contains(key);
    }

    public void addClaim(String key) {
        claims.add(key);
    }

    public void removeClaim(String key) {
        claims.remove(key);
    }

    public int getClaimCount() {
        return claims.size();
    }

    public Location getHome() {
        if (!homeSet || homeWorld == null) {
            return null;
        }
        World world = Bukkit.getWorld(homeWorld);
        if (world == null) {
            return null;
        }
        return new Location(world, homeX, homeY, homeZ, homeYaw, homePitch);
    }

    public void setHome(Location location) {
        this.homeWorld = location.getWorld().getName();
        this.homeX = location.getX();
        this.homeY = location.getY();
        this.homeZ = location.getZ();
        this.homeYaw = location.getYaw();
        this.homePitch = location.getPitch();
        this.homeSet = true;
    }

    public boolean isHomeSet() {
        return homeSet;
    }

    public void setHomeSet(boolean homeSet) {
        this.homeSet = homeSet;
    }

    public String getHomeWorld() {
        return homeWorld;
    }

    public void setHomeWorld(String homeWorld) {
        this.homeWorld = homeWorld;
    }

    public double getHomeX() {
        return homeX;
    }

    public void setHomeX(double homeX) {
        this.homeX = homeX;
    }

    public double getHomeY() {
        return homeY;
    }

    public void setHomeY(double homeY) {
        this.homeY = homeY;
    }

    public double getHomeZ() {
        return homeZ;
    }

    public void setHomeZ(double homeZ) {
        this.homeZ = homeZ;
    }

    public float getHomeYaw() {
        return homeYaw;
    }

    public void setHomeYaw(float homeYaw) {
        this.homeYaw = homeYaw;
    }

    public float getHomePitch() {
        return homePitch;
    }

    public void setHomePitch(float homePitch) {
        this.homePitch = homePitch;
    }
}
