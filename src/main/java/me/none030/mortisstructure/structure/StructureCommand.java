package me.none030.mortisstructure.structure;

import me.none030.mortisstructure.utils.DespawnStructure;
import me.none030.mortisstructure.utils.StructureBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.none030.mortisstructure.structure.StructureMessages.*;

public class StructureCommand implements TabExecutor {

    private final StructureManager manager;

    public StructureCommand(StructureManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("spawn")) {
            if (!sender.hasPermission("mortisstructure.spawn")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            if (args.length != 2) {
                sender.sendMessage(USAGE_SPAWN);
                return false;
            }
            Structure structure = manager.getStructureById().get(args[1]);
            if (structure == null) {
                sender.sendMessage(STRUCTURE_NOT_FOUND);
                return false;
            }
            structure.build();
        }
        if (args[0].equalsIgnoreCase("random")) {
            if (!sender.hasPermission("mortisstructure.random")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            if (args.length != 1) {
                sender.sendMessage(USAGE_RANDOM);
                return false;
            }
            List<Structure> structures = new ArrayList<>(manager.getStructures());
            Collections.shuffle(structures);
            Structure structure = structures.get(0);
            if (structure == null) {
                sender.sendMessage(STRUCTURE_NOT_FOUND);
                return false;
            }
            structure.build();
        }
        if (args[0].equalsIgnoreCase("despawn")) {
            if (!sender.hasPermission("mortisstructure.despawn")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            if (args.length != 5) {
                sender.sendMessage(USAGE_DESPAWN);
                return false;
            }
            World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                sender.sendMessage(WORLD_NOT_FOUND);
                return false;
            }
            double x = Double.parseDouble(args[2]);
            double y = Double.parseDouble(args[3]);
            double z = Double.parseDouble(args[4]);
            Location loc = new Location(world, x, y, z);

            for (DespawnStructure structure : manager.getDespawnManager().getDespawnStructures()) {
                for (StructureBlock block : structure.getBlocks()) {
                    if (loc.distance(block.getLocation()) <= 1) {
                        manager.getDespawnManager().deSpawn(structure);
                        return true;
                    }
                }
            }
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
            return arguments;
        }
        if (args[0].equalsIgnoreCase("spawn")) {
            if (args.length == 2) {
                return new ArrayList<>(manager.getStructureById().keySet());
            }
        }
        return null;
    }
}
