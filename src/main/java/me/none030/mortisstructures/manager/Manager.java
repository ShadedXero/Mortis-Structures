package me.none030.mortisstructures.manager;

import me.none030.mortisstructures.config.DataConfig;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Manager {

    private final DataConfig config;
    private final List<Location> cores;
    private final HashMap<String, LocalDateTime> timeById;

    public Manager() {
        this.cores = new ArrayList<>();
        this.timeById = new HashMap<>();
        this.config = new DataConfig(this);
    }

    public void add(Location core) {
        config.add(core);
        cores.add(core);
    }

    public void remove(Location core) {
        config.remove(core);
        cores.remove(core);
    }

    public void add(String id, LocalDateTime time) {
        config.add(id, time);
        timeById.put(id, time);
    }

    public void remove(String id) {
        config.remove(id);
        timeById.remove(id);
    }

    public DataConfig getConfig() {
        return config;
    }

    public List<Location> getCores() {
        return cores;
    }

    public HashMap<String, LocalDateTime> getTimeById() {
        return timeById;
    }
}
