package me.none030.mortisstructures.data;

import me.none030.mortisstructures.structure.StructureLocation;
import org.bukkit.block.data.BlockData;

import java.util.UUID;

public class StructureBlockData {

    private final UUID uuid;
    private final StructureLocation location;
    private final BlockData blockData;

    public StructureBlockData(UUID uuid, StructureLocation location, BlockData blockData) {
        this.uuid = uuid;
        this.location = location;
        this.blockData = blockData;
    }

    public UUID getUuid() {
        return uuid;
    }

    public StructureLocation getLocation() {
        return location;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
