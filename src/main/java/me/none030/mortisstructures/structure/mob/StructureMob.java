package me.none030.mortisstructures.structure.mob;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class StructureMob extends Mob {

    private final EntityType entityType;

    public StructureMob(EntityType entityType, int amount) {
        super(amount);
        this.entityType = entityType;
    }

    @Override
    public void spawn(Location location) {
        for (int i = 0; i < getAmount(); i++) {
            location.getWorld().spawnEntity(location, entityType);
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
