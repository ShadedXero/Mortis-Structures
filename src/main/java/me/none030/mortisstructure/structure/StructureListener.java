package me.none030.mortisstructure.structure;

import me.none030.mortisstructure.utils.DespawnStructure;
import me.none030.mortisstructure.utils.StructureBlock;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class StructureListener implements Listener {

    private final StructureManager manager;

    public StructureListener(StructureManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block  = e.getBlock();
        for (DespawnStructure despawnStructure : manager.getDespawnManager().getDespawnStructures()) {
            Structure structure = manager.getStructureById().get(despawnStructure.getId());
            if (!structure.isUnbreakable()) {
                continue;
            }
            for (StructureBlock structureBlock : despawnStructure.getBlocks()) {
                if (block.equals(structureBlock.getLocation().getBlock())) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
