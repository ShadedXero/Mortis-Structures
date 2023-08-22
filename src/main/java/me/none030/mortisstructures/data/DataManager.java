package me.none030.mortisstructures.data;

import com.sk89q.worldedit.math.BlockVector3;
import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.structure.StructureLocation;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DataManager {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final H2Database database;
    private final Map<String, LocalDateTime> timeById;
    private final Map<UUID, StructureData> structureDataByUUID;
    private final Map<StructureLocation, StructureBlockData> structureBlockByLocation;
    private final List<QueueData> queue;

    public DataManager(H2Database database) {
        this.database = database;
        this.timeById = new HashMap<>();
        this.structureDataByUUID = new HashMap<>();
        this.structureBlockByLocation = new HashMap<>();
        this.queue = new ArrayList<>();
        initializeDatabase();
        loadDatabase();
        runQueue();
    }

    private void runQueue() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (queue.isEmpty()) {
                    return;
                }
                for (int i = 0; i < 5000; i++) {
                    if (queue.isEmpty()) {
                        return;
                    }
                    QueueData data = queue.get(0);
                    queue.remove(0);
                    data.run(database);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void initializeDatabase() {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.execute("CREATE TABLE IF NOT EXISTS MortisStructures(id tinytext primary key, spawn tinytext)");
                database.execute("CREATE TABLE IF NOT EXISTS MortisStructureData(uuid tinytext primary key, id tinytext, despawn tinytext, center mediumtext)");
                database.execute("CREATE TABLE IF NOT EXISTS MortisStructureBlockData(uuid tinytext, location mediumtext, blockdata tinytext)");
            }
        }.runTask(plugin);
    }

    private void loadDatabase() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet structureSet = database.query("SELECT * FROM MortisStructures");
                    while (structureSet.next()) {
                        String id = structureSet.getString("id");
                        if (id == null) {
                            continue;
                        }
                        String rawTime = structureSet.getString("spawn");
                        if (rawTime == null) {
                            removeTime(id);
                            continue;
                        }
                        LocalDateTime time;
                        try {
                            time = LocalDateTime.parse(rawTime);
                            removeTime(id);
                        }catch (DateTimeParseException exp) {
                            continue;
                        }
                        timeById.put(id, time);
                    }
                    ResultSet structureDataSet = database.query("SELECT * FROM MortisStructureData");
                    while (structureDataSet.next()) {
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(structureDataSet.getString("uuid"));
                        }catch (IllegalArgumentException exp) {
                            continue;
                        }
                        String id = structureDataSet.getString("id");
                        if (id == null) {
                            removeStructure(uuid);
                            continue;
                        }
                        String rawTime = structureDataSet.getString("despawn");
                        if (rawTime == null) {
                            removeStructure(uuid);
                            continue;
                        }
                        LocalDateTime time;
                        try {
                            time = LocalDateTime.parse(rawTime);
                        }catch (DateTimeParseException exp) {
                            removeStructure(uuid);
                            continue;
                        }
                        String rawCenter = structureDataSet.getString("center");
                        if (rawCenter == null) {
                            removeStructure(uuid);
                            continue;
                        }
                        BlockVector3 center = getVector(rawCenter);
                        if (center == null) {
                            removeStructure(uuid);
                            continue;
                        }
                        StructureData data = new StructureData(uuid, id, time, center);
                        structureDataByUUID.put(uuid, data);
                    }
                    loadStructureBlocks();
                }catch (SQLException exp) {
                    exp.printStackTrace();
                }
            }
        }.runTask(plugin);
    }

    private void loadStructureBlocks() {
        ResultSet structureBlockDataSet = database.query("SELECT * FROM MortisStructureBlockData");
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5000; i++) {
                        if (!structureBlockDataSet.next()) {
                            cancel();
                            return;
                        }
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(structureBlockDataSet.getString("uuid"));
                        } catch (IllegalArgumentException exp) {
                            continue;
                        }
                        String rawLocation = structureBlockDataSet.getString("location");
                        if (rawLocation == null) {
                            continue;
                        }
                        StructureLocation location = getLocation(rawLocation);
                        if (location == null) {
                            continue;
                        }
                        String blockData = structureBlockDataSet.getString("blockdata");
                        if (blockData == null) {
                            continue;
                        }
                        StructureBlockData data = new StructureBlockData(uuid, location, Bukkit.createBlockData(blockData));
                        structureBlockByLocation.put(location, data);
                    }
                }catch (SQLException exp) {
                    exp.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private StructureLocation getLocation(String rawLocation) {
        String[] raw = rawLocation.split(",");
        if (raw.length != 4) {
            return null;
        }
        double x;
        double y;
        double z;
        try {
            x = Double.parseDouble(raw[1]);
            y = Double.parseDouble(raw[2]);
            z = Double.parseDouble(raw[3]);
        }catch (NumberFormatException exp) {
            return null;
        }
        return new StructureLocation(raw[0], x, y, z);
    }

    private String getRawLocation(StructureLocation location) {
        return location.getWorldName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    private BlockVector3 getVector(String rawVector) {
        String[] raw = rawVector.split(",");
        if (raw.length != 3) {
            return null;
        }
        double x;
        double y;
        double z;
        try {
            x = Double.parseDouble(raw[0]);
            y = Double.parseDouble(raw[1]);
            z = Double.parseDouble(raw[2]);
        }catch (NumberFormatException exp) {
            return null;
        }
        return BlockVector3.at(x, y, z);
    }

    private String getRawVector(BlockVector3 vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

    public void addTime(String id, LocalDateTime time) {
        QueueData data = new QueueData("INSERT INTO MortisStructures(id, spawn) VALUES (?, ?)", QueueType.UPDATE, id, time);
        queue.add(data);
        timeById.put(id, time);
    }

    public void addStructure(StructureData structureData) {
        QueueData data = new QueueData("INSERT INTO MortisStructureData(uuid, id, despawn, center) VALUES (?, ?, ?, ?)", QueueType.UPDATE, structureData.getUuid().toString(), structureData.getId(), structureData.getDespawn().toString(), getRawVector(structureData.getCenter()));
        queue.add(data);
        structureDataByUUID.put(structureData.getUuid(), structureData);
    }

    public void addStructureBlock(StructureBlockData structureBlockData) {
        QueueData data = new QueueData("INSERT INTO MortisStructureBlockData(uuid, location, blockdata) VALUES (?, ?, ?)", QueueType.UPDATE, structureBlockData.getUuid(), getRawLocation(structureBlockData.getLocation()), structureBlockData.getBlockData().getAsString());
        queue.add(data);
        structureBlockByLocation.put(structureBlockData.getLocation(), structureBlockData);
    }

    public void updateTime(String id, LocalDateTime time) {
        QueueData data = new QueueData("UPDATE MortisStructures SET spawn = ? WHERE id = ?", QueueType.UPDATE, time, id);
        queue.add(data);
        timeById.put(id, time);
    }

    public void removeTime(String id) {
        QueueData data = new QueueData("DELETE FROM MortisStructures WHERE id = ?", QueueType.UPDATE, id);
        queue.add(data);
        timeById.remove(id);
    }

    public void removeStructure(UUID uuid) {
        QueueData data = new QueueData("DELETE FROM MortisStructureData WHERE uuid = ?", QueueType.UPDATE, uuid);
        queue.add(data);
        QueueData data2 = new QueueData("DELETE FROM MortisStructureBlockData WHERE uuid = ?", QueueType.UPDATE, uuid);
        queue.add(data2);
        structureDataByUUID.remove(uuid);
        structureBlockByLocation.entrySet().removeIf(entry -> entry.getValue().getUuid().equals(uuid));
    }

    public LocalDateTime getTime(String id) {
        return timeById.get(id);
    }

    public List<StructureData> getStructureData() {
        return new ArrayList<>(structureDataByUUID.values());
    }

    public List<StructureBlockData> getStructureBlockData(UUID uuid) {
        List<StructureBlockData> structureBlocks = new ArrayList<>();
        for (StructureBlockData structureBlock : structureBlockByLocation.values()) {
            if (structureBlock.getUuid().equals(uuid)) {
                structureBlocks.add(structureBlock);
            }
        }
        return structureBlocks;
    }

    public StructureData getStructure(StructureLocation location) {
        StructureBlockData structureBlockData = getStructureBlock(location);
        if (structureBlockData == null) {
            return null;
        }
        return structureDataByUUID.get(structureBlockData.getUuid());
    }

    public StructureBlockData getStructureBlock(StructureLocation location) {
        return structureBlockByLocation.get(location);
    }

    public Map<String, LocalDateTime> getTimeById() {
        return timeById;
    }

    public Map<UUID, StructureData> getStructureDataByUUID() {
        return structureDataByUUID;
    }

    public Map<StructureLocation, StructureBlockData> getStructureBlockByLocation() {
        return structureBlockByLocation;
    }
}
