package me.none030.mortisstructures.structure;

import me.none030.mortisstructures.data.StructureData;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class StructureListener implements Listener {

    private final StructureManager structureManager;

    public StructureListener(StructureManager structureManager) {
        this.structureManager = structureManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block  = e.getBlock();
        StructureLocation location = new StructureLocation(block.getLocation());
        StructureData data = structureManager.getDataManager().getStructure(location);
        if (data == null) {
            return;
        }
        Structure structure = structureManager.getStructureById().get(data.getId());
        if (structure == null) {
            return;
        }
        if (structure.isUnbreakable()) {
            e.setCancelled(true);
        }
    }
}
