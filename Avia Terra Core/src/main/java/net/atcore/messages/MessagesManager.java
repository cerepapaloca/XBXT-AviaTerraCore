package net.atcore.messages;

import lombok.extern.slf4j.Slf4j;
import net.atcore.AviaTerraCore;
import net.atcore.utils.Gradient;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * En esta clase esta tod0 relacionado con los colores y el envío de mensajes. Todos los mensajes tienen que pasar por quí
 * al igual que todos los colores.
 */
@Slf4j
public final class MessagesManager {

    public static final String LINK_DISCORD = "&a&nhttps://discord.gg/azurex";
    //"&8[" + GlobalUtils.applyGradient("<#00CCCC>AviaTerra<#00FFFF>",'l') + "&8]&r " ;
    public static final String PREFIX = "&8[" + new Gradient("AviaTerra")
            .addGradient(new Color(0, 200, 200), 1)
            .addGradient(new Color(0, 255, 255), 1)
            .getText() + "&8] ";

    /// ////////////////////////
    /// ////////////////////////

    public static void sendMessage(CommandSender sender, Message message, @Nullable MessagesType type) {
        sendMessage(sender, message.getMessage(), type, CategoryMessages.PRIVATE, true);
    }

    public static void sendMessage(CommandSender sender, String message, @Nullable MessagesType type) {
        sendMessage(sender, message, type, CategoryMessages.PRIVATE, true);
    }

    public static void sendMessage(CommandSender sender, String message, @Nullable MessagesType type, CategoryMessages categoryMessages) {
        sendMessage(sender, message, type, categoryMessages, true);
    }

    public static void sendMessage(CommandSender sender, String message, @Nullable MessagesType type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (sender instanceof Player player) {
            sendMessage(player, message, type, categoryMessages, isPrefix);
        } else {
            sendMessageConsole(message, type, categoryMessages, isPrefix);
        }
    }

    ///////////////////////////
    ///////////////////////////

    /**
     * @see #sendMessage(Player, String, MessagesType, CategoryMessages, boolean) sendMessage()
     */

    public static void sendMessage(Player player, String message, @Nullable MessagesType type) {
        sendMessage(player, message, type, CategoryMessages.PRIVATE, true);
    }

    /**
     * @see #sendMessage(Player, String, MessagesType, CategoryMessages, boolean) sendMessage()
     */

    public static void sendMessage(Player player, String message, @Nullable MessagesType type, CategoryMessages categoryMessages) {
        sendMessage(player, message, type, categoryMessages, true);
    }

    /**
     * Todos los mensajes del plugin tiene que pasar por este metodo o por el metodo
     * {@link #sendMessageConsole(String, MessagesType, CategoryMessages, boolean) sendMessageConsole}.
     * Para que todos los mensajes tenga el mismo formato de color y diseño
     *
     * @param player           al jugador que le vas a enviar el mensaje
     * @param message          el mensaje
     * @param type             indica que mensaje va a ser informativo o error o un proceso exitoso lo qu varia es su color
     *                         y su colo secundario
     * @param categoryMessages es si el mensaje se tiene que enviar a un canal de discord se usara {@link #sendMessageLogDiscord}
     *                         para enviar el mensaje, ojo asegurar que el canal de discord exista
     * @param isPrefix         si se le añade el prefijo al mensaje
     * @see #addProprieties
     */

    public static void sendMessage(Player player, String message, @Nullable MessagesType type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        String s = addProprieties(message, type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        TextComponent textComponent = addTextComponent(ChatColor.translateAlternateColorCodes('&', s));
        player.spigot().sendMessage(textComponent);
    }

    /// ////////////////////////
    /// ////////////////////////

    public static void sendMessageConsole(String message, MessagesType type) {
        sendMessageConsole(message, type, CategoryMessages.PRIVATE);
    }

    public static void sendMessageConsole(String message, MessagesType type, boolean prefix) {
        sendMessageConsole(message, type, CategoryMessages.PRIVATE, prefix);
    }

    public static void sendMessageConsole(String message, @Nullable MessagesType type, CategoryMessages categoryMessages) {
        sendMessageConsole(message, type, categoryMessages, true);
    }

    public static void sendMessageConsole(String message, @Nullable MessagesType type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            sendMessageLogDiscord(type, categoryMessages, message);
        }

        String s = addProprieties(message, type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        TextComponent textComponent = addTextComponent(ChatColor.translateAlternateColorCodes('&', s));
        Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
    }

    ///////////////////////////
    ///////////////////////////

    public static void sendErrorException(String message, Exception exception) {
        AviaTerraCore.getInstance().getLogger().severe(setFormatException(message, exception));
    }

    public static void sendWaringException(String message, Exception exception) {
        AviaTerraCore.getInstance().getLogger().warning(setFormatException(message, exception));
    }

    private static String setFormatException(String message, Exception exception) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            builder.append(element.toString()).append("\n\t");
        }
        return String.format("%s [%s=%s] \n\t%s", message, exception.getClass().getSimpleName(), exception.getMessage(), builder);
    }

    ///////////////////////////
    ///////////////////////////

    /**
     * Le añade el formato de colores a los mensajes. Para recalcar una parte de un mensaje
     * se usa {@code <|} para abrir y para cerrar {@code |>} esto aplica el color y añade la
     * negrilla en el mensaje del discord y si un mensaje tiene un color específico y quieres
     * volver al colo origina usa {@code |!>} para que vuelva a color origina sin añadir el doble
     * asterisco para la negrilla de discord
     *
     * @param message    el mensaje que quieres modificar
     * @param type       si en un mensaje informativo o de un error etc. esto soporta nulo si lo pones nulo
     *                   mostrar un color gris
     * @param isResister en este indica si se tiene que registrar en caso de que si se le añade este sufijo
     *                   {@code [R]} indíca que ese mensaje se tiene que registrar en
     *                   un canal de discord OJO esto no hace que envié el mensaje
     * @param showPrefix se tiene que poner él {@link #PREFIX} en el mensaje en casi en todos los casos hay que
     *                   poner le prefijo al mensaje para que sea más fácil de identificar
     * @return regresa el texto con todos las aplicadas
     */

    public static String addProprieties(String message, @Nullable MessagesType type, boolean isResister, boolean showPrefix) {
        if (isResister) {
            while (Character.isSpaceChar(message.charAt(message.length() - 1))) {
                message = message.substring(0, message.length() - 1);
            }
            message = message + " &c[R]";
        }
        String s;
        if (showPrefix) {
            s = PREFIX;
        } else {
            s = "";
        }
        if (type == null) type = MessagesType.NULL;
        String colorMain = type.getMainColor();
        return s + colorMain + message
                .replace("<|", type.getSecondColor())
                .replace("|>", colorMain)
                .replace("|!>", colorMain)
                .replace("`", "");
    }

    /*Experimental*/
    public static TextComponent addTextComponent(String s) {
        TextComponent finalText = new TextComponent();
        int end = 0;
        int start = 0;
        List<Character> charsDisplay = new ArrayList<>();
        boolean isEntrySyntax = false;
        char chartColor = 'r';
        char firstChar = 'r';
        for (int i = 0; s.length() > i; i++) {
            TextComponent textComponent = new TextComponent();
            char c = s.charAt(i);
            if ('{' == (c)) {
                start = i;
                isEntrySyntax = true;
                addColor(finalText, charsDisplay, firstChar);

                charsDisplay.clear();
            }else {
                if (!isEntrySyntax) {
                    charsDisplay.add(c);
                }
            }
            if ('&' == c || '§' == c){
                chartColor = s.charAt(i + 1);
            }
            if ('}' == (c)) {
                firstChar = chartColor;
                end = i;
                isEntrySyntax = false;
            }
            if (start != 0 && end != 0) {
                String sub = s.substring(start, end);
                start = 0;
                end = 0;
                List<Character> chars = new ArrayList<>();
                for (int j = 1; j < sub.length(); j++) {
                    if (sub.charAt(j) != '(') {
                        chars.add(sub.charAt(j));
                    }else {
                        break;
                    }
                }
                String propertiesText = getProperties(sub);
                String displayText = getDisplayText(sub);
                Text builderProperties = new Text(new ComponentBuilder(propertiesText).create());
                StringBuilder sb = new StringBuilder();
                for (char ch : chars) {
                    sb.append(ch);
                }

                if (displayText == null) {
                    throw new IllegalArgumentException("displayText is null");
                }
                if (propertiesText == null) {
                    throw new IllegalArgumentException("propertiesText is null");
                }
                switch (sb.toString().replace(" ", "")) {
                    case "hover"-> {
                        TextComponent tc = new TextComponent(displayText);
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builderProperties));
                        textComponent.setColor(ChatColor.getByChar(chartColor));
                        textComponent.addExtra(tc);
                        finalText.addExtra(textComponent);
                    }
                    case "click"->{
                        String[] split = propertiesText.split(":");
                        TextComponent tc = new TextComponent(displayText);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(split[0].replace(" ", "").toUpperCase()), split[1]));
                        textComponent.setColor(ChatColor.getByChar(chartColor));
                        textComponent.addExtra(tc);
                        finalText.addExtra(textComponent);
                    }
                    default -> throw new IllegalArgumentException();
                }
            }
        }
        addColor(finalText, charsDisplay, firstChar);
        return finalText;
    }

    private static void addColor(TextComponent textComponent, List<Character> charsDisplay, char chartColor) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : charsDisplay) sb.append(ch);
        String color = sb.toString();
        if (color.contains("§x")) {
            String[] parts = color.split("§x");
            for (String part : parts) {
                if (part.isEmpty()) continue; // Ignorar partes vacías
                if (!(part.length() > 12)) {
                    textComponent.addExtra(part);
                    continue;
                }
                String hexColor = part.substring(0, 12);
                if (!(hexColor.replace("§", "").length() == 6)){
                    continue;
                }
                String text = part.substring(12);

                // Crear un componente con el color y el texto correspondiente
                TextComponent coloredComponent = new TextComponent(text);
                coloredComponent.setColor(ChatColor.of("#" + hexColor.replace("§", "")));
                textComponent.addExtra(coloredComponent);
            }
        }else {
            String[] split = color.split("&");
            boolean isFirst = true;
            for (String part : split) {
                TextComponent tc;
                if (part.isEmpty()) continue;
                tc = new TextComponent(part);
                if (isFirst) {
                    isFirst = false;
                    tc.setColor(ChatColor.getByChar(chartColor));
                }else {
                    tc.setColor(ChatColor.getByChar(part.charAt(0)));
                }
                textComponent.addExtra(tc);
            }
        }
    }

    private static @Nullable String getProperties(@NotNull String sub) {
        int start = 0;// hola {test(dsad)dsadasda}
        int end = 0;
        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);
            if ('(' == (c)) start = i;
            if (')' == (c)) end = i;
            if (start != 0 && end != 0) {
                return sub.substring(start + 1, end);
            }
        }
        return null;
    }

    private static @Nullable String getDisplayText(String sub) {
        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);
            if (')' == c) {
                return sub.substring(i + 1);
            }
        }
        return null;
    }

    private static void sendMessageLogDiscord(MessagesType type, CategoryMessages categoryMessages, String message) {
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
     * Le manda un título a jugador respetando el formato de {@link #addProprieties(String, MessagesType, boolean, boolean) addProprieties}
     */

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, MessagesType type) {
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', addProprieties(title, type, false, false)),
                ChatColor.translateAlternateColorCodes('&', addProprieties(subtitle, type, false, false)), fadeIn, stay, fadeOut);
    }
}
