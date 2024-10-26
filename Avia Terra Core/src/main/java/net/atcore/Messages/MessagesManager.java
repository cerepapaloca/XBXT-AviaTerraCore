package net.atcore.Messages;

import net.atcore.AviaTerraCore;
import net.atcore.Exception.DiscordChannelNotFound;
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

    public static final String COLOR_SUCCESS = "&2";
    public static final String COLOR_INFO = "&3";
    public static final String COLOR_WARING = "&e";
    public static final String COLOR_ERROR = "&c";

    public static final String[] PREFIX_AND_SUFFIX_KICK = new String[]{"&c&m &r &c&m       &r  &4&lAviaKick&c  &m        &r &c&m \n\n&r", "\n\n&m &r &c&m                               &r &c&m "};

    public static final String COLOR_ESPECIAL = "&b";
    public static final String LINK_DISCORD = "&a&nhttps://discord.gg/azurex";

    private static final String prefix = "&6[" + ChatColor.of("#00FFFF") + "&lA" + ChatColor.of("#55FFFF")
            + "&lv" + ChatColor.of("#AAFFFF") + "&li" + ChatColor.of("#FFFFFF") + "&la" + ChatColor.of("#FFFFFF")
            + "&lT" + ChatColor.of("#FFFFFF") + "&le" + ChatColor.of("#FFAAFF") + "&lr" + ChatColor.of("#FF55FF")
            + "&lr" + ChatColor.of("#FF00FF") + "&la" +  "&6]&r " ;

    ///////////////////////////
    ///////////////////////////

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

    ///////////////////////////
    ///////////////////////////

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
        String s;
        if (isPrefix){
            s = prefix;
        }else {
            s = "";
        }
        message = addProprieties(message, type, categoryMessages);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s + selectColore(type) + message));
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

    ///////////////////////////
    ///////////////////////////

    private static String selectColore(@Nullable TypeMessages type) {
        String color = "&8";
        if (type != null) {
            switch (type) {
                case SUCCESS -> color = COLOR_SUCCESS;
                case INFO -> color = COLOR_INFO;
                case WARNING -> color = COLOR_WARING;
                case ERROR -> color = COLOR_ERROR;
            }
        }
        return color;
    }

    private static String addProprieties(String message, TypeMessages type, CategoryMessages categoryMessages) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            while (Character.isSpaceChar(message.charAt(message.length()-1))){
                message = message.substring(0, message.length()-1);
            }
            message = message + " &c[R]";
        }
        char color = '8';
        switch (type) {
            case SUCCESS -> color = 'a';
            case INFO -> color = 'b';
            case WARNING -> color = '6';
            case ERROR -> color = '4';
        }
        return message.replace("<|","&" + color).replace("|>",selectColore(type)).replace("|!>", selectColore(type));
    }

    private static void sendMessageLogDiscord(TypeMessages type, CategoryMessages categoryMessages, String message) {
        String channelId;
        switch (categoryMessages) {
            case BAN -> channelId = "1294324328401207389";
            case MODERATION -> channelId = "1294324285602795550";
            case LOGIN -> channelId = "1299444352409669746";
            default -> {
                return;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            String finalMessage;
            switch (type) {
                case SUCCESS -> finalMessage = "『🟩』 " + message;
                case INFO -> finalMessage = "『🟦』 " + message;
                case WARNING -> finalMessage = "『🟨』 " + message;
                case ERROR -> finalMessage = "『🟥』 " + message;
                default -> {
                    return;
                }
            }
            //no parece que tenga sentido, pero sí lo tiene, es por qué asi puede quitar los códigos de color del texto
            finalMessage = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', finalMessage));
            //obtén el canal por su ID
            TextChannel channel = AviaTerraCore.BOT_DISCORD.getTextChannelById(channelId);
            if (channel !=  null) {
                channel.sendMessage(finalMessage.replace("<|", "**").replace("|>", "**").replace("|!>", "")).queue();
            } else {
                throw new DiscordChannelNotFound("No se encontró canal de discord para los registro " + type.name());
            }
        });
    }
}
