package com.simplefactions.models;

import java.util.UUID;

public class FactionPlayer {

    private UUID uuid;
    private String name;
    private String factionId;
    private double power;
    private long lastPowerRegen;
    private transient boolean factionChat;

    public FactionPlayer() {
    }

    public FactionPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactionId() {
        return factionId;
    }

    public void setFactionId(String factionId) {
        this.factionId = factionId;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public long getLastPowerRegen() {
        return lastPowerRegen;
    }

    public void setLastPowerRegen(long lastPowerRegen) {
        this.lastPowerRegen = lastPowerRegen;
    }

    public boolean isFactionChat() {
        return factionChat;
    }

    public void setFactionChat(boolean factionChat) {
        this.factionChat = factionChat;
    }
}
