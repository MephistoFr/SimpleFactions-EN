package com.simplefactions.models;

public enum FactionRole {
    LEADER("Leader"),
    OFFICER("Mod"),
    MEMBER("Member"),
    RECRUIT("Recruit");

    private final String displayName;

    FactionRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FactionRole fromName(String name) {
        try {
            return FactionRole.valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return MEMBER;
        }
    }
}
