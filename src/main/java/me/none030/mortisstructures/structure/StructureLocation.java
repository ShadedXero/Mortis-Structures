package me.none030.mortisstructures.structure;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class StructureLocation {

    private String worldName;
    private double x;
    private double y;
    private double z;

    public StructureLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public StructureLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StructureLocation other = (StructureLocation) obj;
        return Objects.equals(worldName, other.worldName) &&
                Double.compare(x, other.x) == 0 &&
                Double.compare(y, other.y) == 0 &&
                Double.compare(z, other.z) == 0;
    }
}
