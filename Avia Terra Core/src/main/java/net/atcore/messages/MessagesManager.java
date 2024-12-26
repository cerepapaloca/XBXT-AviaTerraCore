package net.atcore.messages;

import net.atcore.AviaTerraCore;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * En esta clase esta tod0 relacionado con los colores y envío de mensajes todos los mensajes tiene que pasar por quí
 * al igual que todos los colores.
 */
public final class MessagesManager {

    @Deprecated
    public static final String COLOR_SUCCESS = "&2";
    @Deprecated
    public static final String COLOR_INFO = "&3";
    @Deprecated
    public static final String COLOR_ERROR = "&c";

    public static final String[] PREFIX_AND_SUFFIX_KICK = new String[]{"&c&m &r &c&m       &r  &4&lAviaKick&c  &m        &r &c&m \n\n&r", "\n\n&m &r &c&m                               &r &c&m "};

    @Deprecated
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
        String s = addProprieties(message, type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
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

        String s = addProprieties(message, type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    ///////////////////////////
    ///////////////////////////

    /**
     * Le añade el formato de colores a los mensajes. Para recalcar una parte de un mensaje
     * se usa {@code <|} para abrir y para cerrar {@code |>} esto aplica el color y añade la
     * negrilla en el mensaje del discord y si un mensaje tiene un color específico y quieres
     * volver al colo origina usa {@code |!>} para que vuelva a color origina sin añadir el doble
     * asterisco para la negrilla de discord
     * @param message el mensaje que quieres modificar
     * @param type si en un mensaje informativo o de un error etc. esto soporta nulo si lo pones nulo
     *             mostrar un color gris
     * @param isResister en este indica si se tiene que registrar en caso de que si se le añade este sufijo
     *                         {@code [R]} indíca que ese mensaje se tiene que registrar en
     *                         un canal de discord OJO esto no hace que envié el mensaje
     * @param showPrefix se tiene que poner él {@link #PREFIX} en el mensaje en casi en todos los casos hay que
     *                 poner le prefijo al mensaje para que sea más fácil de identificar
     * @return regresa el
     */

    public static String addProprieties(String message,@Nullable TypeMessages type, boolean isResister, boolean showPrefix) {
        if (isResister) {
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
        if (type == null) type = TypeMessages.NULL;
        String colorMain = type.getMainColor();
        message = message.replace("`", "");
        return s + colorMain +  message.replace("<|", type.getSecondColor()).replace("|>", colorMain).replace("|!>", colorMain);
    }

    private static void sendMessageLogDiscord(TypeMessages type, CategoryMessages categoryMessages, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(),() -> {
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
            String chanelId = categoryMessages.getIdChannel();
            if (chanelId == null)return;
            TextChannel channel = AviaTerraCore.jda.getTextChannelById(chanelId);
            if (channel !=  null) {
                channel.sendMessage(finalMessage.replace("<|", "**").replace("|>", "**").replace("|!>", "")).queue();
            } else {
                throw new IllegalArgumentException("No se encontró canal de discord para los registro " + categoryMessages.name());
            }
        });
    }

    /**
     * Le manda un título a jugador respetando el formato de {@link #addProprieties(String, TypeMessages, boolean, boolean) addProprieties}
     */

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, TypeMessages type) {
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', addProprieties(title, type, false, false)),
                ChatColor.translateAlternateColorCodes('&', addProprieties(subtitle, type, false, false)), fadeIn, stay, fadeOut);
    }
}
