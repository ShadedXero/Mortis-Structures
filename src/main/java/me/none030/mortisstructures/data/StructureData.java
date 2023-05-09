package me.none030.mortisstructures.data;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.time.LocalDateTime;
import java.util.UUID;

public class StructureData extends Data {

    private final String uuidKey = "MortisStructuresUUID";
    private final String idKey = "MortisStructuresId";
    private final String centerKey = "MortisStructuresCenter";
    private final String blockDataKey = "MortisStructuresBlockData";
    private final String deSpawnTimeKey = "MortisStructuresDeSpawnTime";

    public StructureData(Location location) {
        super(location);
    }

    public void create(UUID uuid, String id, BlockVector3 center, BlockData blockData, LocalDateTime deSpawnTime) {
        setUUID(uuid);
        setId(id);
        setCenter(center);
        setBlockData(blockData);
        setDeSpawnTime(deSpawnTime);
    }

    public void setUUID(UUID uuid) {
        if (uuid == null) {
            set(uuidKey, null);
            return;
        }
        set(uuidKey, uuid.toString());
    }

    public UUID getUUID() {
        String value = get(uuidKey);
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }

    public void setId(String id) {
        set(idKey, id);
    }

    public String getId() {
        return get(idKey);
    }

    public void setCenter(BlockVector3 center) {
        if (center == null) {
            set(centerKey, null);
            return;
        }
        set(centerKey, center.getX() + "," + center.getY() + "," + center.getZ());
    }

    public BlockVector3 getCenter() {
        String value = get(centerKey);
        if (value == null) {
            return null;
        }
        String[] values = value.split(",");
        if (values.length == 0) {
            return null;
        }
        return BlockVector3.at(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
    }

    public void setBlockData(BlockData blockData) {
        if (blockData == null) {
            set(blockDataKey, null);
            return;
        }
        set(blockDataKey, blockData.getAsString());
    }

    public BlockData getBlockData() {
        String value = get(blockDataKey);
        if (value == null) {
            return null;
        }
        return Bukkit.createBlockData(value);
    }

    public void setDeSpawnTime(LocalDateTime deSpawnTime) {
        if (deSpawnTime == null) {
            set(deSpawnTimeKey, null);
            return;
        }
        set(deSpawnTimeKey, deSpawnTime.toString());
    }

    public LocalDateTime getDeSpawnTime() {
        String value = get(deSpawnTimeKey);
        if (value == null) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}
