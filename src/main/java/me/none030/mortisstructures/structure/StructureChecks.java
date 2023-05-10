package me.none030.mortisstructures.structure;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.List;

public class StructureChecks {

    private final boolean isInTown;
    private final int townRange;
    private final boolean isInWater;
    private final boolean isInLava;
    private final List<Material> mustHaveBlocks;
    private final List<Material> mustNotHaveBlocks;
    private final List<Biome> mustHaveBiomes;
    private final List<Biome> mustNotHaveBiomes;

    public StructureChecks(boolean isInTown, int townRange, boolean isInWater, boolean isInLava, List<Material> mustHaveBlocks, List<Material> mustNotHaveBlocks, List<Biome> mustHaveBiomes, List<Biome> mustNotHaveBiomes) {
        this.isInTown = isInTown;
        this.townRange = townRange;
        this.isInWater = isInWater;
        this.isInLava = isInLava;
        this.mustHaveBlocks = mustHaveBlocks;
        this.mustNotHaveBlocks = mustNotHaveBlocks;
        this.mustHaveBiomes = mustHaveBiomes;
        this.mustNotHaveBiomes = mustNotHaveBiomes;
    }

    public boolean hasTown() {
        return isInTown;
    }

    public int getTownRange() {
        return townRange;
    }

    public boolean hasWater() {
        return isInWater;
    }

    public boolean hasLava() {
        return isInLava;
    }

    public List<Material> getMustHaveBlocks() {
        return mustHaveBlocks;
    }

    public List<Material> getMustNotHaveBlocks() {
        return mustNotHaveBlocks;
    }

    public List<Biome> getMustHaveBiomes() {
        return mustHaveBiomes;
    }

    public List<Biome> getMustNotHaveBiomes() {
        return mustNotHaveBiomes;
    }
}
