package me.none030.mortisstructures.structure;

import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.data.StructureData;
import me.none030.mortisstructures.manager.Manager;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructureManager extends Manager {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final List<Structure> structures;
    private final HashMap<String, Structure> structureById;

    public StructureManager() {
        structures = new ArrayList<>();
        structureById = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new StructureListener(this), plugin);
        check();
    }

    private void check() {
        StructureManager structureManager = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Structure structure : structures) {
                    String id = structure.getId();
                    LocalDateTime time = getTimeById().get(id);
                    if (time == null) {
                        structure.build(structureManager);
                        add(id, structure.getNextSpawn());
                        continue;
                    }
                    if (LocalDateTime.now().isBefore(time)) {
                        continue;
                    }
                    structure.build(structureManager);
                    add(id, structure.getNextSpawn());
                }
                for (int i = 0; i < getCores().size(); i++) {
                    Location core = getCores().get(i);
                    if (core == null) {
                        continue;
                    }
                    StructureData data = new StructureData(core);
                    String id = data.getId();
                    if (id == null) {
                        remove(core);
                        continue;
                    }
                    Structure structure = structureById.get(id);
                    if (structure == null) {
                        remove(core);
                        continue;
                    }
                    if (LocalDateTime.now().isBefore(data.getDeSpawnTime())) {
                        continue;
                    }
                    structure.deSpawn(data.getUUID(), data.getCenter());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public HashMap<String, Structure> getStructureById() {
        return structureById;
    }
}
