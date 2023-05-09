package me.none030.mortisstructures.manager;

import me.none030.mortisstructures.data.StructureData;
import me.none030.mortisstructures.structure.Structure;
import me.none030.mortisstructures.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureCommand implements TabExecutor {

    private final MainManager mainManager;

    public StructureCommand(MainManager mainManager) {
        this.mainManager = mainManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("spawn")) {
            if (!sender.hasPermission("mortisstructures.spawn")) {
                sender.sendMessage(new MessageUtils("&cYou don't have permission to use this").color());
                return false;
            }
            if (args.length != 2 && args.length != 6) {
                sender.sendMessage(new MessageUtils("&cUsage: /structures spawn <structure-id>").color());
                sender.sendMessage(new MessageUtils("&cUsage: /structures spawn <structure-id> <world_name> <x> <y> <z>").color());
                return false;
            }
            if (args.length == 2) {
                Structure structure = mainManager.getStructureManager().getStructureById().get(args[1]);
                if (structure == null) {
                    sender.sendMessage(new MessageUtils("&cPlease enter a valid structure id").color());
                    return false;
                }
                structure.build(mainManager.getStructureManager());
                sender.sendMessage(new MessageUtils("&aStructure built").color());
            }
            if (args.length == 6) {
                Structure structure = mainManager.getStructureManager().getStructureById().get(args[1]);
                if (structure == null) {
                    sender.sendMessage(new MessageUtils("&cPlease enter a valid structure id").color());
                    return false;
                }
                World world = Bukkit.getWorld(args[2]);
                if (world == null) {
                    return false;
                }
                double x;
                double y;
                double z;
                try {
                    x = Double.parseDouble(args[3]);
                    y = Double.parseDouble(args[4]);
                    z = Double.parseDouble(args[5]);
                }catch (NumberFormatException exp) {
                    return false;
                }
                Location location = new Location(world, x, y, z);
                structure.build(mainManager.getStructureManager(), location);
                sender.sendMessage(new MessageUtils("&aStructure built").color());
            }
        }
        if (args[0].equalsIgnoreCase("random")) {
            if (!sender.hasPermission("mortisstructures.random")) {
                sender.sendMessage(new MessageUtils("&cYou don't have permission to use this").color());
                return false;
            }
            if (args.length != 1) {
                sender.sendMessage(new MessageUtils("&cUsage: /structures random").color());
                return false;
            }
            List<Structure> structures = new ArrayList<>(mainManager.getStructureManager().getStructures());
            int index = new Random().nextInt(0, structures.size());
            Structure structure = structures.get(index);
            if (structure == null) {
                sender.sendMessage(new MessageUtils("&cCould not find any structure").color());
                return false;
            }
            structure.build(mainManager.getStructureManager());
            sender.sendMessage(new MessageUtils("&aStructure built").color());
        }
        if (args[0].equalsIgnoreCase("despawn")) {
            if (!sender.hasPermission("mortisstructures.despawn")) {
                sender.sendMessage(new MessageUtils("&cYou don't have permission to use this").color());
                return false;
            }
            if (args.length != 5) {
                sender.sendMessage(new MessageUtils("&cUsage: /structures despawn <world_name> <x> <y> <z>").color());
                return false;
            }
            World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                sender.sendMessage(new MessageUtils("&cPlease enter a valid world").color());
                return false;
            }
            double x = Double.parseDouble(args[2]);
            double y = Double.parseDouble(args[3]);
            double z = Double.parseDouble(args[4]);
            Location loc = new Location(world, x, y, z);
            StructureData data = new StructureData(loc);
            if (data.getId() == null) {
                return false;
            }
            Structure structure = mainManager.getStructureManager().getStructureById().get(data.getId());
            if (structure == null) {
                return false;
            }
            structure.deSpawn(data.getUUID(), data.getCenter());
            sender.sendMessage(new MessageUtils("&aStructure despawned").color());
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mortisstructures.reload")) {
                sender.sendMessage(new MessageUtils("&cYou don't have permission to use this").color());
                return false;
            }
            mainManager.reload();
            sender.sendMessage(new MessageUtils("&aMortis Structures Reloaded").color());
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("spawn");
            arguments.add("despawn");
            arguments.add("random");
            arguments.add("reload");
            return arguments;
        }
        if (args[0].equalsIgnoreCase("spawn")) {
            if (args.length == 2) {
                return new ArrayList<>(mainManager.getStructureManager().getStructureById().keySet());
            }
        }
        return null;
    }
}
