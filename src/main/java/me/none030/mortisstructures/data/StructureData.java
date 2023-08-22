package me.none030.mortisstructures.data;

import com.sk89q.worldedit.math.BlockVector3;

import java.time.LocalDateTime;
import java.util.UUID;

public class StructureData {

    private final UUID uuid;
    private final String id;
    private final LocalDateTime despawn;
    private final BlockVector3 center;

    public StructureData(UUID uuid, String id, LocalDateTime despawn, BlockVector3 center) {
        this.uuid = uuid;
        this.id = id;
        this.despawn = despawn;
        this.center = center;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDespawn() {
        return despawn;
    }

    public BlockVector3 getCenter() {
        return center;
    }
}
