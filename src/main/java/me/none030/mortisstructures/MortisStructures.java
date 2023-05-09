package me.none030.mortisstructures;

import me.none030.mortisstructures.manager.MainManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MortisStructures extends JavaPlugin {

    private static MortisStructures Instance;
    private boolean towny;
    private boolean mythicMobs;
    private MainManager mainManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        towny = getServer().getPluginManager().getPlugin("Towny") != null;
        mythicMobs = getServer().getPluginManager().getPlugin("MythicMobs") != null;
        mainManager = new MainManager();
    }

    public static MortisStructures getInstance() {
        return Instance;
    }

    public MainManager getMainManager() {
        return mainManager;
    }

    public boolean hasTowny() {
        return towny;
    }

    public boolean hasMythicMobs() {
        return mythicMobs;
    }
}
