package me.none030.mortisstructure.structure;

import me.none030.mortisstructure.MortisStructure;
import me.none030.mortisstructure.data.DataManager;
import me.none030.mortisstructure.utils.DespawnStructure;
import me.none030.mortisstructure.utils.StructureBlock;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DespawnManager {

    private final MortisStructure plugin = MortisStructure.getInstance();

    private final StructureManager manager;
    private final DataManager dataManager;
    private final List<DespawnStructure> despawnStructures;

    public DespawnManager(StructureManager manager) {
        this.manager = manager;
        despawnStructures = new ArrayList<>();
        dataManager = new DataManager(this);
        check();
    }

    public List<DespawnStructure> getDespawnStructures() {
        return despawnStructures;
    }

    public void deSpawn(DespawnStructure structure) {
        for (StructureBlock block : structure.getBlocks()) {
            block.getLocation().getBlock().setType(block.getType());
        }
        dataManager.delete(structure);
        despawnStructures.remove(structure);
    }

    private void check() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (despawnStructures.size() > 0) {
                    for (DespawnStructure despawnStructure : despawnStructures) {
                        if (despawnStructure.getTime().isAfter(LocalDateTime.now())) {
                             continue;
                        }
                        for (StructureBlock block : despawnStructure.getBlocks()) {
                            block.getLocation().getBlock().setType(block.getType());
                        }
                        Structure structure = manager.getStructureById().get(despawnStructure.getId());
                        for (String command : structure.getCommandsOnDespawn()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                        dataManager.delete(despawnStructure);
                        despawnStructures.remove(despawnStructure);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 500L);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public StructureManager getStructureManager() {
        return manager;
    }

}
