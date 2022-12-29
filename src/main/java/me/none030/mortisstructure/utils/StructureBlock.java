package me.none030.mortisstructure.utils;

import org.bukkit.Location;
import org.bukkit.Material;

public class StructureBlock {

    private final Material type;
    private final Location location;

    public StructureBlock(Material type, Location location) {
        this.type = type;
        this.location = location;
    }

    public Material getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }
}
