package me.none030.mortisstructure.structure;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;

public class StructureMythicMob {

    private final String mythicMob;

    public StructureMythicMob(String mythicMob) {
        this.mythicMob = mythicMob;
    }

    public void spawn(Location location) {
        MythicBukkit.inst().getMobManager().spawnMob(mythicMob, location);
    }
}
