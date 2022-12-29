package me.none030.mortisstructure.config;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import me.none030.mortisstructure.MortisStructure;
import me.none030.mortisstructure.structure.Structure;
import me.none030.mortisstructure.structure.StructureManager;
import me.none030.mortisstructure.utils.Checks;
import me.none030.mortisstructure.utils.StructureMob;
import me.none030.mortisstructure.utils.StructureType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private final MortisStructure plugin = MortisStructure.getInstance();

    private final StructureManager manager;

    public ConfigManager(StructureManager manager) {
        this.manager = manager;
        loadConfig();
    }

    private void loadConfig() {
        File file = saveConfig();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("database");
        if (section == null) {
            return;
        }
        String host = section.getString("host");
        int port = section.getInt("port");
        String database = section.getString("database");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        String user = section.getString("user");
        String password = section.getString("password");
        connect(url, user, password);
        ConfigurationSection schematics = config.getConfigurationSection("schematics");
        if (schematics == null) {
            return;
        }
        for (String key : schematics.getKeys(false)) {
            ConfigurationSection structure = schematics.getConfigurationSection(key);
            if (structure == null) {
                continue;
            }
            World world = Bukkit.getWorld(Objects.requireNonNull(structure.getString("world")));
            int interval = structure.getInt("interval");
            int despawn = structure.getInt("despawn");
            int tries = structure.getInt("tries");
            int spawns = structure.getInt("spawn");
            List<String> commandsOnSpawn = structure.getStringList("command-on-spawn");
            List<String> commandsOnDeSpawn = structure.getStringList("command-on-despawn");
            boolean unbreakable = structure.getBoolean("unbreakable");
            List<StructureMob> mobs = new ArrayList<>();
            if (structure.contains("mobs")) {
                for (String line : structure.getStringList("mobs")) {
                    String[] raw = line.split(":");
                    EntityType type = EntityType.valueOf(raw[0]);
                    StructureMob mob = new StructureMob(type, Integer.parseInt(raw[1]));
                    mobs.add(mob);
                }
            }
            if (structure.contains("mythic-mobs")) {
                for (String line : structure.getStringList("mythic-mobs")) {
                    String[] raw = line.split(":");
                    StructureMob mob = new StructureMob(raw[0], Integer.parseInt(raw[1]));
                    mobs.add(mob);
                }
            }
            if (mobs.size() == 0) {
                mobs = null;
            }
            StructureType type = StructureType.valueOf(structure.getString("type"));
            String[] location1 = Objects.requireNonNull(structure.getString("location1")).split(",");
            String[] location2 = Objects.requireNonNull(structure.getString("location2")).split(",");
            Location loc1 = new Location(world, Double.parseDouble(location1[0]), Double.parseDouble(location1[1]), Double.parseDouble(location1[2]));
            Location loc2 = new Location(world, Double.parseDouble(location2[0]), Double.parseDouble(location2[1]), Double.parseDouble(location2[2]));
            File schematic = new File(plugin.getDataFolder() + "/schematics/" + structure.getString("schematic"));
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            if (format == null) {
                continue;
            }
            try {
                ClipboardReader reader = format.getReader(Files.newInputStream(schematic.toPath()));
                clipboard = reader.read();
            }catch (IOException exp) {
                exp.printStackTrace();
                continue;
            }
            ConfigurationSection checks = structure.getConfigurationSection("checks");
            if (checks == null) {
                continue;
            }
            boolean town = checks.getBoolean("town");
            boolean water = checks.getBoolean("water");
            boolean lava = checks.getBoolean("lava");
            List<Material> hasBlocks = null;
            if (checks.contains("has-blocks")) {
                hasBlocks = new ArrayList<>();
                for (String line : checks.getStringList("has-blocks")) {
                    Material material = Material.valueOf(line);
                    hasBlocks.add(material);
                }
            }
            List<Material> hasNotBlocks = null;
            if (checks.contains("has-not-blocks")) {
                hasNotBlocks = new ArrayList<>();
                for (String line : checks.getStringList("has-not-blocks")) {
                    Material material = Material.valueOf(line);
                    hasNotBlocks.add(material);
                }
            }
            Checks check = new Checks(town, water, lava, hasBlocks, hasNotBlocks);
            Structure struc = new Structure(manager, key, clipboard, type, world, loc1, loc2, unbreakable, spawns, interval, despawn, tries, check, mobs, commandsOnSpawn, commandsOnDeSpawn);
            manager.getStructures().add(struc);
            manager.getStructureById().put(key, struc);
        }
    }

    private void connect(String url, String user, String password) {

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            plugin.getLogger().info("Connected to database");

            manager.setConnection(connection);
        } catch (SQLException exp) {
            plugin.getLogger().severe("Database could not be found");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private File saveConfig() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }
        File schematic = new File(plugin.getDataFolder() + "/schematics/hut.schem");
        if (!schematic.exists()) {
            schematic.mkdir();
            plugin.saveResource("schematics/hut.schem", true);
        }
        return file;
    }
}
