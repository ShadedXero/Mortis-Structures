package me.none030.mortisstructures.manager;

import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.config.ConfigManager;
import me.none030.mortisstructures.structure.StructureManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class MainManager {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private StructureManager structureManager;
    private ConfigManager configManager;

    public MainManager() {
        this.structureManager = new StructureManager();
        this.configManager = new ConfigManager(this);
        plugin.getServer().getPluginCommand("structures").setExecutor(new StructureCommand(this));
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        Bukkit.getScheduler().cancelTasks(plugin);
        setStructureManager(new StructureManager());
        setConfigManager(new ConfigManager(this));
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public void setStructureManager(StructureManager structureManager) {
        this.structureManager = structureManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
