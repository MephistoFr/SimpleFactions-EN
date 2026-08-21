package com.simplefactions.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simplefactions.SimpleFactions;
import com.simplefactions.models.Faction;
import com.simplefactions.models.FactionPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class StorageManager {

    private final SimpleFactions plugin;
    private final File dataFolder;
    private final File factionsFile;
    private final File playersFile;
    private final Gson gson;

    public StorageManager(SimpleFactions plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.factionsFile = new File(dataFolder, "factions.json");
        this.playersFile = new File(dataFolder, "players.json");
    }

    public void loadAll() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        loadFactions();
        loadPlayers();
    }

    private void loadFactions() {
        if (!factionsFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(factionsFile, StandardCharsets.UTF_8)) {
            Faction[] factions = gson.fromJson(reader, Faction[].class);
            if (factions != null) {
                for (Faction faction : factions) {
                    plugin.getFactionManager().registerLoadedFaction(faction);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load factions.json: " + e.getMessage());
        }
    }

    private void loadPlayers() {
        if (!playersFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(playersFile, StandardCharsets.UTF_8)) {
            FactionPlayer[] players = gson.fromJson(reader, FactionPlayer[].class);
            if (players != null) {
                for (FactionPlayer fp : players) {
                    plugin.getPowerManager().loadPlayer(fp);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load players.json: " + e.getMessage());
        }
    }

    public void saveAll() {
        saveFactions();
        savePlayers();
    }

    public void saveFactions() {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            try (Writer writer = new FileWriter(factionsFile, StandardCharsets.UTF_8)) {
                gson.toJson(plugin.getFactionManager().getFactions().toArray(new Faction[0]), writer);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save factions.json: " + e.getMessage());
        }
    }

    public void savePlayers() {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            try (Writer writer = new FileWriter(playersFile, StandardCharsets.UTF_8)) {
                gson.toJson(plugin.getPowerManager().getPlayers().values().toArray(new FactionPlayer[0]), writer);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.json: " + e.getMessage());
        }
    }
}
