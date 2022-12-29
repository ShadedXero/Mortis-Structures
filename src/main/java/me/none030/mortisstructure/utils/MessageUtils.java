package me.none030.mortisstructure.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String colorMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
