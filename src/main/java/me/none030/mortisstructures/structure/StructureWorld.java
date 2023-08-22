package me.none030.mortisstructures.structure;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class StructureWorld {

    private final String worldName;

    public StructureWorld(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }
}
