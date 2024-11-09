package net.atcore.messages;

import net.atcore.AviaTerraCore;
import net.atcore.exception.DiscordChannelNotFound;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
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

    private static final String PREFIX = "&8[" + GlobalUtils.applyGradient("<#00CCCC>AviaTerra<#00FFFF>",'l') + "&8]&r " ;

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

    /**
     * Todos los mensajes del plugin tiene que pasar por este metodo o por el metodo
     * {@link #sendMessageConsole(String, TypeMessages, CategoryMessages, boolean) sendMessageConsole}.
     * Para que todos los mensajes tenga el mismo formato de color y diseño
     * @param player al jugador que le vas a enviar el mensaje
     * @param message el mensaje
     * @param type indica que mensaje va a ser informativo o error o un proceso exitoso lo qu varia es su color
     *             y su colo secundario
     * @param categoryMessages es si el mensaje se tiene que enviar a un canal de discord se usara {@link #sendMessageLogDiscord}
     *                         para enviar el mensaje, ojo asegurar que el canal de discord exista
     * @param isPrefix si se le añade el prefijo al mensaje
     * @see #addProprieties
     */

    public static void sendMessage(Player player, String message, @Nullable TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE){
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        message = addProprieties(message, type, categoryMessages, isPrefix);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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

        message = addProprieties(message, type, categoryMessages, isPrefix);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    ///////////////////////////
    ///////////////////////////

    private static String selectColore(@Nullable TypeMessages type) {
        String color = "&7";
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

    /**
     * Le añade el formato de colores a los mensajes. Para recalcar una parte de un mensaje
     * se usa {@code <|} para abrir y para cerrar {@code |>} esto aplica el color y añade la
     * negrilla en el mensaje del discord y si un mensaje tiene un color específico y quieres
     * volver al colo origina usa {@code |!>} para que vuelva a color origina sin añadir el doble
     * asterisco para la negrilla de discord
     * @param message el mensaje que quieres modificar
     * @param type si en un mensaje informativo o de un error etc. esto soporta nulo si lo pones nulo
     *             mostrar un color gris
     * @param categoryMessages en este indica si se tiene que registrar si es diferente a
     *                         {@code CategoryMessages.PRIVATE} le añade este sufijo al mensaje
     *                         {@code [R]} indíca que ese mensaje se tiene que registrar en
     *                         un canal de discord
     * @param showPrefix se tiene que poner él {@link #PREFIX} en el mensaje en casi en todos los casos hay que
     *                 poner le prefijo al mensaje para que sea más fácil de identificar
     * @return regresa el
     */

    public static String addProprieties(String message,@Nullable TypeMessages type, CategoryMessages categoryMessages, boolean showPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            while (Character.isSpaceChar(message.charAt(message.length()-1))){
                message = message.substring(0, message.length()-1);
            }
            message = message + " &c[R]";
        }
        String s;
        if (showPrefix){
            s = PREFIX;
        }else {
            s = "";
        }
        char color;
        switch (type) {
            case SUCCESS -> color = 'a';
            case INFO -> color = 'b';
            case WARNING -> color = '6';
            case ERROR -> color = '4';
            case null , default -> color = '8';
        }
        String colorMain = selectColore(type);
        return s + colorMain +  message.replace("<|", "&" + color).replace("|>", colorMain).replace("|!>", colorMain);
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

    /**
     * Le manda un título a jugador respetando el formato de {@link #addProprieties(String, TypeMessages, CategoryMessages, boolean) addProprieties}
     */

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, TypeMessages type) {
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', addProprieties(title, type, CategoryMessages.PRIVATE, false)),
                ChatColor.translateAlternateColorCodes('&', addProprieties(subtitle, type, CategoryMessages.PRIVATE, false)), fadeIn, stay, fadeOut);
    }
}
