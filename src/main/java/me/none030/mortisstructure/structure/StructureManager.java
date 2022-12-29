package me.none030.mortisstructure.structure;

import me.none030.mortisstructure.MortisStructure;
import me.none030.mortisstructure.config.ConfigManager;
import me.none030.mortisstructure.data.SpawnManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructureManager {

    private final MortisStructure plugin = MortisStructure.getInstance();
    private final SpawnManager spawnManager;
    private ConfigManager configManager;
    private final DespawnManager despawnManager;
    private Connection connection;
    private final List<Structure> structures;
    private final HashMap<String, LocalDateTime> timeById;
    private final HashMap<String, Structure> structureById;

    public StructureManager() {
        structures = new ArrayList<>();
        timeById = new HashMap<>();
        structureById = new HashMap<>();
        configManager = new ConfigManager(this);
        spawnManager = new SpawnManager(this);
        despawnManager = new DespawnManager(this);
        StructureCommand command = new StructureCommand(this);
        plugin.getServer().getPluginCommand("structure").setExecutor(command);
        plugin.getServer().getPluginManager().registerEvents(new StructureListener(this), plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                check();
            }
        }.runTaskTimer(plugin, 0L, 500L);
    }

    private void check() {
        for (Structure structure : structures) {
            String id = structure.getId();
            if (timeById.containsKey(id)) {
                continue;
            }
            spawnManager.saveTime(id, LocalDateTime.now().plusDays(structure.getInterval()));
        }
        for (String id : timeById.keySet()) {
            LocalDateTime time = timeById.get(id);
            if (LocalDateTime.now().isBefore(time)) {
                continue;
            }
            Structure structure = structureById.get(id);
            if (structure == null) {
                continue;
            }
            structure.build();
            spawnManager.saveTime(id, time.plusDays(structure.getInterval()));
        }
    }

    public HashMap<String, LocalDateTime> getTimeById() {
        return timeById;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public DespawnManager getDespawnManager() {
        return despawnManager;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public HashMap<String, Structure> getStructureById() {
        return structureById;
    }
}
