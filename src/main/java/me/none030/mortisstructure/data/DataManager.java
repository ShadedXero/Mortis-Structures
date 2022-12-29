package me.none030.mortisstructure.data;

import me.none030.mortisstructure.MortisStructure;
import me.none030.mortisstructure.structure.DespawnManager;
import me.none030.mortisstructure.utils.DespawnStructure;
import me.none030.mortisstructure.utils.StructureBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {

    private final MortisStructure plugin = MortisStructure.getInstance();
    private final Connection connection;
    private final DespawnManager despawnManager;

    public DataManager(DespawnManager despawnManager) {
        this.despawnManager = despawnManager;
        this.connection = despawnManager.getStructureManager().getConnection();
        initializeDatabase();
        loadDatabase();
    }

    public void store(DespawnStructure despawnStructure) {
        long count = 0;
        for (StructureBlock block : despawnStructure.getBlocks()) {
            count = count + 10;
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        String sql = "INSERT INTO DespawnStructures(id, time, world, location, block) VALUES(?, ?, ?, ?, ?);";
                        PreparedStatement stmt = connection.prepareStatement(sql);
                        stmt.setString(1, despawnStructure.getId());
                        stmt.setString(2, despawnStructure.getTime().toString());
                        stmt.setString(3, block.getLocation().getWorld().getName());
                        stmt.setString(4, block.getLocation().getX() + ", " + block.getLocation().getY() + ", " + block.getLocation().getZ());
                        stmt.setString(5, block.getType().toString());
                        stmt.execute();
                    } catch (SQLException exp) {
                        exp.printStackTrace();
                    }
                }
            }.runTaskLater(plugin, count);
        }
    }

    public void delete(DespawnStructure despawnStructure) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM DespawnStructures WHERE time = ?;");
            stmt.setString(1, despawnStructure.getTime().toString());
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS DespawnStructures(id varchar(100), time varchar(100), world varchar(100), location varchar(100), block varchar(100));";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void loadDatabase() {
        List<String> times = new ArrayList<>();
        HashMap<String, List<StructureBlock>> blocksByTime = new HashMap<>();
        HashMap<String, String> idByTime = new HashMap<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM DespawnStructures;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("time");
                String time = resultSet.getString("time");
                if (!times.contains(time)) {
                    times.add(time);
                    blocksByTime.put(time, new ArrayList<>());
                    idByTime.put(time, id);
                }
                World world = Bukkit.getWorld(resultSet.getString("world"));
                String[] location = resultSet.getString("location").split(",");
                Location loc = new Location(world, Double.parseDouble(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]));
                Material type = Material.valueOf(resultSet.getString("block"));
                StructureBlock block = new StructureBlock(type, loc);
                blocksByTime.get(time).add(block);
            }
            for (String key : blocksByTime.keySet()) {
                LocalDateTime time = LocalDateTime.parse(key);
                String id = idByTime.get(key);
                List<StructureBlock> blocks = blocksByTime.get(key);
                DespawnStructure structure = new DespawnStructure(id, time, blocks);
                despawnManager.getDespawnStructures().add(structure);
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }
}
