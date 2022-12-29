package me.none030.mortisstructure.utils;

import org.bukkit.entity.EntityType;

public class StructureMob {

    private EntityType entity;
    private String mythicMob;
    private final int amount;

    public StructureMob(String mythicMob, int amount) {
        this.mythicMob = mythicMob;
        this.amount = amount;
    }

    public StructureMob(EntityType entity, int amount) {
        this.entity = entity;
        this.amount = amount;
    }

    public EntityType getEntity() {
        return entity;
    }

    public String getMythicMob() {
        return mythicMob;
    }

    public int getAmount() {
        return amount;
    }
}
