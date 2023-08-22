package me.none030.mortisstructures.structure;

import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.data.DataManager;
import me.none030.mortisstructures.data.StructureData;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StructureManager {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final DataManager dataManager;
    private final List<Structure> structures;
    private final HashMap<String, Structure> structureById;

    public StructureManager(DataManager dataManager) {
        this.dataManager = dataManager;
        structures = new ArrayList<>();
        structureById = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new StructureListener(this), plugin);
        check();
    }

    private void check() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Structure structure : structures) {
                    String id = structure.getId();
                    if (id == null) {
                        continue;
                    }
                    LocalDateTime time = dataManager.getTime(id);
                    if (time == null) {
                        structure.build(dataManager);
                        dataManager.addTime(id, structure.getNextSpawn());
                        break;
                    }
                    if (LocalDateTime.now().isBefore(time)) {
                        continue;
                    }
                    structure.build(dataManager);
                    dataManager.updateTime(id, structure.getNextSpawn());
                    break;
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (StructureData structureData : dataManager.getStructureData()) {
                    UUID uuid = structureData.getUuid();
                    if (uuid == null) {
                        continue;
                    }
                    String id = structureData.getId();
                    if (id == null) {
                        dataManager.removeStructure(uuid);
                        continue;
                    }
                    Structure structure = structureById.get(id);
                    if (structure == null) {
                        dataManager.removeStructure(uuid);
                        continue;
                    }
                    LocalDateTime time = structureData.getDespawn();
                    if (time == null) {
                        dataManager.removeStructure(uuid);
                        continue;
                    }
                    if (LocalDateTime.now().isBefore(time)) {
                        continue;
                    }
                    structure.despawn(dataManager, structureData);
                    break;
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public HashMap<String, Structure> getStructureById() {
        return structureById;
    }
}
