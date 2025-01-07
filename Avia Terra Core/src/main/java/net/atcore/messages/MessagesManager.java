package net.atcore.messages;

import lombok.extern.slf4j.Slf4j;
import net.atcore.AviaTerraCore;
import net.atcore.data.yml.MessageFile;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.Gradient;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.transform.sax.SAXResult;
import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static net.kyori.adventure.text.event.ClickEvent.*;

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
        player.sendMessage(applyFinalProprieties(message, type, categoryMessages, isPrefix));
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
        Bukkit.getConsoleSender().sendMessage(applyFinalProprieties(message, type, categoryMessages, isPrefix));
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

    public static net.kyori.adventure.text.TextComponent addTextComponent(String s) {
        Builder textBuilder = Component.text();
        int end = 0;
        int start = 0;
        List<Character> charsDisplay = new ArrayList<>();
        boolean isEntrySyntax = false;
        List<Character> chartColor = new ArrayList<>();
        for (int i = 0; s.length() > i; i++) {
            char c = s.charAt(i);
            if ('{' == (c)) {
                start = i;
                isEntrySyntax = true;
                addColor(textBuilder, charsDisplay);

                charsDisplay.clear();
            } else {
                if (!isEntrySyntax) {
                    charsDisplay.add(c);
                }
            }
            if ('&' == c || '§' == c) {
                chartColor.add(s.charAt(i + 1));
            }
            if ('}' == (c)) {
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
                    } else {
                        break;
                    }
                }
                String propertiesText = getProperties(sub);
                String displayText = getDisplayText(sub);
                StringBuilder sb = new StringBuilder();
                for (char ch : chars) {
                    sb.append(ch);
                }

                if (displayText == null) {
                    throw new IllegalArgumentException("displayText is null. Sintaxis errónea");
                }
                if (propertiesText == null) {
                    throw new IllegalArgumentException("propertiesText is null. Sintaxis errónea");
                }
                Builder subTextBuilder = Component.text();
                switch (sb.toString().replace(" ", "")) {
                    case "hover" -> {
                        Builder tc = Component.text();
                        tc.content(displayText);
                        tc.style(getColorTextColor(chartColor));
                        tc.hoverEvent(HoverEvent.showText(Component.text(displayText)));
                        Bukkit.getLogger().warning(tc.build().toString());
                        subTextBuilder.append(tc);
                    }
                    case "click" -> {
                        String[] split = propertiesText.split(":");
                        if (split.length == 1)
                            throw new IllegalArgumentException("Sintaxis errónea tiene que agregar ':' después de la ación");
                        Builder tc = Component.text();
                        tc.content(displayText);
                        tc.style(getColorTextColor(chartColor));
                        tc.clickEvent(clickEvent(Action.valueOf(split[0].replace(" ", "").toUpperCase()), split[1]));
                        subTextBuilder.append(tc);
                    }
                    default ->
                            throw new IllegalArgumentException(String.format("Argumento invalido '%s'. Tiene que ser click o hover", sb));
                }
                textBuilder.append(subTextBuilder);
            }
        }
        addColor(textBuilder, charsDisplay);
        return textBuilder.build();
    }
    //                        OPEN_URL,
//                                OPEN_FILE,
//                                RUN_COMMAND,
//                                SUGGEST_COMMAND,
//                                CHANGE_PAGE,
//                                COPY_TO_CLIPBOARD;

    public static Style getColorTextColor(List<Character> chars) {
        NamedTextColor color = null;
        TextDecoration td = null;
        for (char c : chars) {
            switch (c) {
                case '4' -> color = NamedTextColor.DARK_RED;
                case 'c' -> color = NamedTextColor.RED;
                case '6' -> color = NamedTextColor.GOLD;
                case 'e' -> color = NamedTextColor.YELLOW;
                case '2' -> color = NamedTextColor.DARK_GREEN;
                case 'a' -> color = NamedTextColor.GREEN;
                case 'b' -> color = NamedTextColor.AQUA;
                case '3' -> color = NamedTextColor.DARK_AQUA;
                case '1' -> color = NamedTextColor.DARK_BLUE;
                case '9' -> color = NamedTextColor.BLUE;
                case 'd' -> color = NamedTextColor.LIGHT_PURPLE;
                case '5' -> color = NamedTextColor.DARK_PURPLE;
                case 'f' -> color = NamedTextColor.WHITE;
                case '7' -> color = NamedTextColor.GRAY;
                case '8' -> color = NamedTextColor.DARK_GRAY;
                case '0' -> color = NamedTextColor.BLACK;
                case 'k' -> td = TextDecoration.OBFUSCATED;
                case 'l' -> td = TextDecoration.BOLD;
                case 'n' -> td = TextDecoration.UNDERLINED;
                case 'o' -> td = TextDecoration.ITALIC;
                case 'm' -> td = TextDecoration.STRIKETHROUGH;
                case 'r' -> {
                    Style.Builder s = Style.style();
                    Map<TextDecoration, TextDecoration.State> decorationStateMap = Style.style(color).decorations();
                    for (TextDecoration key : decorationStateMap.keySet()) {
                        s.decoration(key, TextDecoration.State.FALSE);
                    }
                    s.color(null);
                    return s.build();
                }
            }
        }
        if (td != null) {
            return Style.style(color, td);
        }else {
            return Style.style(color);
        }
    }

    public static TextComponent applyFinalProprieties(String message, @Nullable MessagesType type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        String s = addProprieties(message, type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        @SuppressWarnings("deprecation") var textComponent = addTextComponent(ChatColor.translateAlternateColorCodes('&', addEndColor(s)));
        return textComponent;
    }

    private static String addEndColor(String text) {
        StringBuilder sb = new StringBuilder();
        List<String> colorHex = new ArrayList<>();
        for (int i = 0; text.length() > i; i++) {
            char c = text.charAt(i);
            if ('&' == c || '§' == c) {
                char h = text.charAt(i + 1);
                if (h != 'x'){
                    colorHex.add(String.valueOf(h));
                }else {
                    colorHex.add(text.substring(i+1, i+14));
                }
            }
            if ('}' == c){
                sb.append(c);
                for (String s : colorHex) {
                    sb.append("&").append(s);
                }
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static void addColor(Builder textComponent, List<Character> charsDisplay) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : charsDisplay) sb.append(ch);
        String color = sb.toString();
        // Quien creó LegacyComponentSerializer le beso los pies
        textComponent.append(LegacyComponentSerializer.legacy('§').deserialize(color));
        /*if (color.contains("§x")) {// TODO: Arreglar esto que no funciona cuando hay un degrado
            String[] parts = color.split("§x");
            for (String part : parts) {
                if (part.isEmpty()) continue; // Ignorar partes vacías
                if (!(part.length() > 12)) {
                    textComponent.append(Component.text(part));
                    continue;
                }
                String hexColor = part.substring(0, 12);
                if (!(hexColor.replace("§", "").length() == 6)) {
                    continue;
                }
                String text = part.substring(12);

                // Crear un componente con el color y el texto correspondiente
                Component component = Component.text(text);
                textComponent.append(component.color(TextColor.color(Integer.parseInt(hexColor.replace("§", "").substring(1), 16))));
            }
        }else {
            String[] split = color.split("§");
            List<Component> components = new ArrayList<>();
            for (String part : split) {
                if (part.isEmpty()) continue;
                Component c;
                c = Component.text(part.substring(1));
                if (components.isEmpty()) {
                    components.add(c.style(getColorTextColor(List.of(part.charAt(0)))));
                }else {
                    components.add(c.style(joinStyle(getColorTextColor(List.of(part.charAt(0))), components.getLast().style())));
                }

            }
            for (Component c : components) textComponent.append(c);
        }*/
    }
    /*
    public static Style joinStyle(Style styleNew, Style styleOld) {
        Style.Builder finalStyle = Style.style();
        Map<TextDecoration, TextDecoration.State> td = styleNew.decorations();
        if (styleNew.color() == null) {
            finalStyle.color(styleOld.color());
        }
        for (TextDecoration key : td.keySet()) {
            if (td.get(key) != TextDecoration.State.TRUE) {
                finalStyle.decoration(key, styleOld.decorations().get(key));
            }
        }
        return finalStyle.build();
    }*/

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
        final Title.Times times = Title.Times.times(Duration.ofMillis(20L *fadeIn), Duration.ofMillis(20L *stay), Duration.ofMillis(20L *fadeOut));
        // Using the times object this title will use 500ms to fade in, stay on screen for 3000ms and then fade out for 1000ms
        final Title t = Title.title(
                LegacyComponentSerializer.legacy('§').deserialize(addProprieties(title, type, false, false)),
                LegacyComponentSerializer.legacy('§').deserialize(addProprieties(subtitle, type, false, false)),
                times
        );

        // Send the title, you can also use Audience#clearTitle() to remove the title at any time
        player.showTitle(t);
    }

    public static Component deathMessage(Player victim, LivingEntity killer, ItemStack stack, EntityDamageEvent.DamageCause cause) {
        Random r = new Random();
        String message;
        if (killer != null && !(killer instanceof Player)) {
            message = MessageFile.MESSAGES_ENTITY.get(killer.getType()).get(r.nextInt(MessageFile.MESSAGES_ENTITY.get(killer.getType()).size()));
        }else {
            message = Message.valueOf("DEATH_CAUSE_" + cause.name()).toString();
        }
        Builder tc = Component.text();
        Component component = tc.append(applyFinalProprieties(message, MessagesType.INFO, CategoryMessages.PRIVATE, true)).build();

        Builder componentVictim = Component.text();
        componentVictim.append(victim.displayName());
        componentVictim.clickEvent(ClickEvent.clickEvent(Action.SUGGEST_COMMAND, "/w " + victim.getName()));
        componentVictim.hoverEvent(HoverEvent.showEntity(victim.asHoverEvent().value()));
        TextReplacementConfig.Builder config1 = TextReplacementConfig.builder().matchLiteral("%1$s").replacement(componentVictim);
        component = component.replaceText(config1.build());
        if (killer != null) {
            Builder componentKiller = Component.text();
            if (killer instanceof Player player) {
                componentKiller.append(player.displayName());
                componentKiller.clickEvent(ClickEvent.clickEvent(Action.SUGGEST_COMMAND, "/w " + player.getName()));
            }else {
                componentKiller.append(GlobalUtils.ChatColorLegacyToComponent(MessagesType.INFO.getSecondColor() + killer.getName() + MessagesType.INFO.getMainColor()));
            }
            componentKiller.hoverEvent(HoverEvent.showEntity(killer.asHoverEvent().value()));
            TextReplacementConfig.Builder config2 = TextReplacementConfig.builder().matchLiteral("%2$s").replacement(componentKiller);
            component = component.replaceText(config2.build());
        }
        Builder componentItem = Component.text();
        if (stack != null) {
            componentItem.append(stack.displayName());
            componentItem.hoverEvent(HoverEvent.showItem(stack.asHoverEvent().value()));
            TextReplacementConfig.Builder config3 = TextReplacementConfig.builder().matchLiteral("%3$s").replacement(componentItem);
            component = component.replaceText(config3.build());
        }else {
            componentItem.content("Mano");
            TextReplacementConfig.Builder config3 = TextReplacementConfig.builder().matchLiteral("%3$s").replacement(componentItem);
            component = component.replaceText(config3.build());
        }

        return component;
    }
}
