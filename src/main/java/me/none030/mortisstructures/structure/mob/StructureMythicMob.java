package me.none030.mortisstructures.structure.mob;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.none030.mortisstructures.MortisStructures;
import org.bukkit.Location;

public class StructureMythicMob extends Mob {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final String mythicMobId;

    public StructureMythicMob(String mythicMobId, int amount) {
        super(amount);
        this.mythicMobId = mythicMobId;
    }

    @Override
    public void spawn(Location location) {
        if (!plugin.hasMythicMobs()) {
            return;
        }
        for (int i = 0; i < getAmount(); i++) {
            MythicBukkit.inst().getMobManager().spawnMob(mythicMobId, location);
        }
    }

    public String getMythicMobId() {
        return mythicMobId;
    }
}
