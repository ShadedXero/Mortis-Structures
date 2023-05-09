package me.none030.mortisstructures.structure.mob;

import org.bukkit.Location;

public abstract class Mob {

    private final int amount;

    public Mob(int amount) {
        this.amount = amount;
    }

    public abstract void spawn(Location location);

    public int getAmount() {
        return amount;
    }
}
