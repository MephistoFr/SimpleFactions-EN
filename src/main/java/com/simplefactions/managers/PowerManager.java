package com.simplefactions.managers;

import com.simplefactions.SimpleFactions;
import com.simplefactions.models.FactionPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PowerManager {

    private final SimpleFactions plugin;
    private final Map<UUID, FactionPlayer> players = new HashMap<>();

    public PowerManager(SimpleFactions plugin) {
        this.plugin = plugin;
    }

    public FactionPlayer getPlayer(UUID uuid) {
        FactionPlayer fp = players.get(uuid);
        if (fp == null) {
            fp = new FactionPlayer(uuid);
            fp.setPower(plugin.getConfigManager().getMaxPower());
            fp.setLastPowerRegen(System.currentTimeMillis());
            players.put(uuid, fp);
        }
        return fp;
    }

    public FactionPlayer getPlayer(Player player) {
        FactionPlayer fp = getPlayer(player.getUniqueId());
        fp.setName(player.getName());
        return fp;
    }

    public FactionPlayer getPlayerOrNull(UUID uuid) {
        return players.get(uuid);
    }

    public double getMaxPower(FactionPlayer fp) {
        return plugin.getConfigManager().getMaxPower();
    }

    public void setPower(FactionPlayer fp, double power) {
        double max = getMaxPower(fp);
        fp.setPower(Math.max(0.0, Math.min(max, power)));
        fp.setLastPowerRegen(System.currentTimeMillis());
    }

    public void addPower(FactionPlayer fp, double delta) {
        setPower(fp, fp.getPower() + delta);
    }

    public void regen(FactionPlayer fp) {
        double max = getMaxPower(fp);
        long now = System.currentTimeMillis();
        long last = fp.getLastPowerRegen();
        if (last <= 0) {
            fp.setLastPowerRegen(now);
            return;
        }
        if (fp.getPower() >= max) {
            fp.setLastPowerRegen(now);
            return;
        }
        double elapsedMinutes = (now - last) / 60000.0;
        double gain = elapsedMinutes * plugin.getConfigManager().getPowerRegenPerMinute();
        if (gain > 0) {
            fp.setPower(Math.min(max, fp.getPower() + gain));
        }
        fp.setLastPowerRegen(now);
    }

    public void handleDeath(Player victim, Player killer) {
        FactionPlayer vp = getPlayer(victim);
        double loss = plugin.getConfigManager().getPowerPerDeath();
        setPower(vp, vp.getPower() + loss);
        victim.sendMessage(plugin.getConfigManager().msg("death-power-loss",
                "%power%", formatPower(loss),
                "%total%", formatPower(vp.getPower()),
                "%max%", formatPower(getMaxPower(vp))));

        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            FactionPlayer kp = getPlayer(killer);
            double gain = plugin.getConfigManager().getPowerPerKill();
            if (gain != 0) {
                setPower(kp, kp.getPower() + gain);
                killer.sendMessage(plugin.getConfigManager().msg("kill-power-gain",
                        "%power%", formatPower(gain),
                        "%total%", formatPower(kp.getPower()),
                        "%max%", formatPower(getMaxPower(kp))));
            }
        }
    }

    public static String formatPower(double power) {
        if (power == Math.floor(power)) {
            return String.valueOf((long) power);
        }
        return String.format(Locale.ROOT, "%.1f", power);
    }

    public Map<UUID, FactionPlayer> getPlayers() {
        return players;
    }

    public void loadPlayer(FactionPlayer fp) {
        players.put(fp.getUuid(), fp);
    }
}
