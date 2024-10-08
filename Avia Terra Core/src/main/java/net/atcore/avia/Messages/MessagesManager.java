package net.atcore.avia.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * En esta clase esta todo relacionado al los colores y envio de mensajes todos los mensjaes tiene que pasar por quí
 * al igual que todos los colores es por queremos modificar los calores sea más fácil
 */
public record MessagesManager() {

    public static final String colorSuccess = "&a";
    public static final String colorInfo = "&3";
    public static final String colorWarning = "&e";
    public static final String colorError = "&c";

    private static final String prefix = "[Avia Terra] ";

    public static void sendMessage(CommandSender sender, String message,@Nullable TypeMessages type) {
        sendMessage(sender, message, type, true);
    }

    public static void sendMessage(CommandSender sender, String message,@Nullable TypeMessages type, boolean isPrefix) {
        if (sender instanceof Player player) {
            sendMessage(player, message, type, isPrefix);
        }else {
            sendMessageConsole(message, type, isPrefix);
        }
    }

    public static void sendMessage(Player player, String message,@Nullable TypeMessages type) {
        sendMessage(player, message, type, true);
    }

    public static void sendMessage(Player player, String message, @Nullable TypeMessages type, boolean isPrefix) {
        if (isPrefix) {
            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', selectColore(type) + message));
        }else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',selectColore(type) + message));
        }
    }

    public static void sendMessageConsole(String message, @Nullable TypeMessages type) {
        sendMessageConsole(message, type, true);
    }

    public static void sendMessageConsole(String message, @Nullable TypeMessages type, boolean isPrefix) {
        if (isPrefix) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + selectColore(type) + message));
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', selectColore(type) + message));
        }
    }

    public static String selectColore(@Nullable TypeMessages type) {
        String color = "&8";
        if (type != null) {
            switch (type) {
                case SUCCESS -> color = colorSuccess;
                case INFO -> color = colorInfo;
                case WARNING -> color = colorWarning;
                case ERROR -> color = colorError;
            }
        }
        return color;
    }
}
