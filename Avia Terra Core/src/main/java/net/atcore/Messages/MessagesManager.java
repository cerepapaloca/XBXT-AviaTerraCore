package net.atcore.Messages;

import net.atcore.AviaTerraCore;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * En esta clase esta tod0 relacionado con los colores y envío de mensajes todos los mensajes tiene que pasar por quí
 * al igual que todos los colores. Es para modificar los calores
 */
public final class MessagesManager {

    public static final String colorSuccess = "&a";
    public static final String colorInfo = "&3";
    public static final String colorWarning = "&e";
    public static final String colorError = "&c";

    public static final String colorEspacial = "&b";
    public static final String linkDiscord = "&a&nhttps://discord.gg/azurex";

    private static final String prefix = "&6[" + ChatColor.of("#00FFFF") + "&lA" + ChatColor.of("#55FFFF")
            + "&lv" + ChatColor.of("#AAFFFF") + "&li" + ChatColor.of("#FFFFFF") + "&la" + ChatColor.of("#FFFFFF")
            + "&lT" + ChatColor.of("#FFFFFF") + "&le" + ChatColor.of("#FFAAFF") + "&lr" + ChatColor.of("#FF55FF")
            + "&lr" + ChatColor.of("#FF00FF") + "&la" +  "&6]&r " ;

    public static void sendMessage(CommandSender sender, String message,@Nullable TypeMessages type) {
        sendMessage(sender, message, type, CategoryMessages.PRIVATE, true);
    }

    public static void sendMessage(CommandSender sender, String message,@Nullable TypeMessages type , CategoryMessages categoryMessages) {
        sendMessage(sender, message, type, categoryMessages, true);
    }

    public static void sendMessage(CommandSender sender, String message,@Nullable TypeMessages type,  CategoryMessages categoryMessages, boolean isPrefix) {
        if (sender instanceof Player player) {
            sendMessage(player, message, type, categoryMessages, isPrefix);
        }else {
            sendMessageConsole(message, type, categoryMessages, isPrefix);
        }
    }

    public static void sendMessage(Player player, String message,@Nullable TypeMessages type) {
        sendMessage(player, message, type,  CategoryMessages.PRIVATE,true);
    }

    public static void sendMessage(Player player, String message,@Nullable TypeMessages type, CategoryMessages categoryMessages) {
        sendMessage(player, message, type,  categoryMessages,true);
    }

    public static void sendMessage(Player player, String message, @Nullable TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE){
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        message = addProprieties(message, type, categoryMessages);
        if (isPrefix) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix +  selectColore(type) + message));
        }else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',selectColore(type) + message));
        }
    }

    ///////////////////////////
    ///////////////////////////

    public static void sendMessageConsole(String message, TypeMessages type) {
        sendMessageConsole(message, type, CategoryMessages.PRIVATE);
    }


    public static void sendMessageConsole(String message, TypeMessages type, boolean prefix) {
        sendMessageConsole(message, type, CategoryMessages.PRIVATE, prefix);
    }

    public static void sendMessageConsole(String message, @Nullable TypeMessages type,CategoryMessages categoryMessages) {
        sendMessageConsole(message, type, categoryMessages,true);
    }

    public static void sendMessageConsole(String message, @Nullable TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE){
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        String s;
        if (isPrefix){
            s = prefix;
        }else {
            s = "";
        }
        message = addProprieties(message, type, categoryMessages);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', s + selectColore(type) + message));
    }

    private static String selectColore(@Nullable TypeMessages type) {
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

    private static String addProprieties(String message, TypeMessages type, CategoryMessages categoryMessages) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            message = message + "&c [R]";
        }
        return message.replace("<|",colorEspacial).replace("|>",selectColore(type));
    }

    private static void sendMessageLogDiscord(TypeMessages type, CategoryMessages categoryMessages, String message) {
        String channelId;
        switch (categoryMessages) {
            case BAN -> channelId = "676059877486886933";
            case MODERATION -> channelId = "676059877486886933";
            default -> {
                return;
            }
        }
        switch (type) {
            case SUCCESS -> message = "『🟩』 " + message;
            case INFO -> message = "『🟦』 " + message;
            case WARNING -> message = "『🟨』 " + message;
            case ERROR -> message = "『🟥』 " + message;
        }
        // Obtén el canal por su ID
        TextChannel channel = JDABuilder.createDefault(AviaTerraCore.TOKEN_BOT).build().getTextChannelById(channelId);

        if (channel != null) {
            message = message.replace("<|", "**").replace("|>", "**");
            // Envía el mensaje al canal
            channel.sendMessage("message").queue();
        } else {
            System.out.println("Canal no encontrado.");
        }
    }
}
