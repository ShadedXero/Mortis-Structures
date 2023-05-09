package me.none030.mortisstructures.config;

import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DataConfig {

    private final MortisStructures plugin = MortisStructures.getInstance();

    public DataConfig(Manager manager) {
        loadCores(manager);
        loadTime(manager);
    }

    private void loadCores(Manager manager) {
        File file = saveStructures();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = new ArrayList<>(config.getStringList("structures"));
        for (String line : locations) {
            String[] raw = line.split(",");
            World world = Bukkit.getWorld(raw[0]);
            if (world == null) {
                locations.remove(line);
                config.set("structures", locations);
                try {
                    config.save(file);
                }catch (IOException exp) {
                    exp.printStackTrace();
                }
                continue;
            }
            Location loc = new Location(world, Double.parseDouble(raw[1]), Double.parseDouble(raw[2]), Double.parseDouble(raw[3]));
            manager.getCores().add(loc);
        }
    }

    private void loadTime(Manager manager) {
        File file = saveData();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String id : config.getKeys(false)) {
            String rawTime = config.getString(id);
            if (rawTime == null) {
                continue;
            }
            LocalDateTime time;
            try {
                time = LocalDateTime.parse(rawTime);
            }catch (DateTimeParseException exp) {
                continue;
            }
            manager.getTimeById().put(id, time);
        }
    }

    public void add(String id, LocalDateTime time) {
        File file = saveData();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(id, time.toString());
        try {
            config.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void remove(String id) {
        File file = saveData();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(id, null);
        try {
            config.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void add(Location location) {
        File file = saveStructures();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = new ArrayList<>(config.getStringList("structures"));
        String loc = location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ();
        locations.add(loc);
        config.set("structures", locations);
        try {
            config.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void remove(Location location) {
        File file = saveStructures();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = new ArrayList<>(config.getStringList("structures"));
        String loc = location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ();
        locations.remove(loc);
        config.set("structures", locations);
        try {
            config.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    private File saveData() {
        File file = new File(plugin.getDataFolder() + "/data/", "data.yml");
        if (!file.exists()) {
            plugin.saveResource("data/" + "data.yml", false);
        }
        return file;
    }

    private File saveStructures() {
        File file = new File(plugin.getDataFolder() + "/data/", "structures.yml");
        if (!file.exists()) {
            plugin.saveResource("data/" + "structures.yml", false);
        }
        return file;
    }
}
