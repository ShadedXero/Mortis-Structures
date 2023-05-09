package me.none030.mortisstructures.structure;

import me.none030.mortisstructures.data.StructureData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
        Player player = e.getPlayer();
        Block block  = e.getBlock();
        StructureData data = new StructureData(block.getLocation());
        String id = data.getId();
        if (id == null) {
            return;
        }
        Structure structure = structureManager.getStructureById().get(id);
        if (structure == null) {
            return;
        }
        e.setCancelled(true);
        if (structure.isUnbreakable()) {
            return;
        }else {
            block.breakNaturally(player.getInventory().getItemInMainHand(), true, true);
        }
    }
}
