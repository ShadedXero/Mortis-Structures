package me.none030.mortisstructures.structure;

import org.bukkit.Material;

import java.util.List;

public class StructureChecks {

    private final boolean isInTown;
    private final boolean isInWater;
    private final boolean isInLava;
    private final List<Material> isInBlocks;
    private final List<Material> isNotInBlocks;

    public StructureChecks(boolean isInTown, boolean isInWater, boolean isInLava, List<Material> isInBlocks, List<Material> isNotInBlocks) {
        this.isInTown = isInTown;
        this.isInWater = isInWater;
        this.isInLava = isInLava;
        this.isInBlocks = isInBlocks;
        this.isNotInBlocks = isNotInBlocks;
    }

    public boolean hasTown() {
        return isInTown;
    }

    public boolean hasWater() {
        return isInWater;
    }

    public boolean hasLava() {
        return isInLava;
    }

    public List<Material> hasBlocks() {
        return isInBlocks;
    }

    public List<Material> hasNotBlocks() {
        return isNotInBlocks;
    }
}
