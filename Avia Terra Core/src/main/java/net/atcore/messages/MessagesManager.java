package net.atcore.messages;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.data.DataSection;
import net.atcore.data.yml.MessageFile;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.kyori.adventure.text.event.ClickEvent.Action;

/**
 * En esta clase esta tod0 relacionado con los colores y el envío de mensajes. Todos los mensajes tienen que pasar por quí
 * al igual que todos los colores.
 */

public final class MessagesManager {

    public static final String LINK_DISCORD = "&a&n<click:open_url:http://discord.xbxt.xyz>http://discord.xbxt.xyz</click>";
    public static final String LINK_WEBSITE = "&a&n<click:open_url:https://xbxt.xyz>xbxt.xyz</click>";
    public static final String LINK_VOTE = "&a&n<click:open_url:https://minecraft-mp.com/server-s339858>minecraft-mp.com/server-s339858</click>";
    //"&8[" + GlobalUtils.applyGradient("<#00CCCC>AviaTerra<#00FFFF>",'l') + "&8]&r " ;
    public static final String PREFIX = "<dark_gray>[<b><#4B2FDE>XT<#ff8C00>XB<dark_gray></b>] ";
    public static final String PREFIX_CHAT_DISCORD = "<dark_gray>[<b><gradient:#0000FF:#0000AA>DISCORD</gradient></b><dark_gray>] ";
    public static final LocaleAvailable DEFAULT_LOCALE_USER = LocaleAvailable.EN;
    public static final LocaleAvailable DEFAULT_LOCALE_PRIVATE = LocaleAvailable.ES;

    /// ////////////////////////
    /// ////////////////////////

    public static void sendFormatMessage(@NotNull CommandSender sender, @NotNull Message message, Object... args) {
        sendMessage(sender, String.format(message.getMessage(sender), args), message.getTypeMessages(), CategoryMessages.PRIVATE, true);
    }

    public static void sendFormatMessage(@NotNull CommandSender sender, @NotNull Message message, CategoryMessages category, Object... args) {
        sendMessage(sender, String.format(message.getMessage(sender), args), message.getTypeMessages(), category, false);
    }

    public static void sendMessage(CommandSender sender, Message message) {
        sendMessage(sender, message.getMessage(sender), message.getTypeMessages(), CategoryMessages.PRIVATE, true);
    }

    private static void sendMessage(CommandSender sender, Message message, @NotNull TypeMessages type) {
        sendMessage(sender, message.getMessage(sender), type, CategoryMessages.PRIVATE, true);
    }

    public static void sendString(CommandSender sender, String message, @NotNull TypeMessages type) {
        sendMessage(sender, message, type, CategoryMessages.PRIVATE, true);
    }

    public static void sendString(CommandSender sender, String message, @NotNull TypeMessages type, CategoryMessages categoryMessages) {
        sendMessage(sender, message, type, categoryMessages, true);
    }

    public static void sendMessage(CommandSender sender, String message, @NotNull TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (sender instanceof Player player) {
            sendMessage(player, message, type, categoryMessages, isPrefix);
        } else {
            logConsole(message, type, categoryMessages, isPrefix);
        }
    }

    public static void sendArgument(CommandSender sender, ArgumentUse argumentUse, @NotNull TypeMessages type) {
        sendMessage(sender, argumentUse.toString(), type, CategoryMessages.PRIVATE, true);
    }

    ///////////////////////////
    ///////////////////////////

    /**
     * @see #sendMessage(Player, String, TypeMessages, CategoryMessages, boolean) sendMessage()
     */

    public static void sendMessage(Player player, String message, @NotNull TypeMessages type, CategoryMessages categoryMessages) {
        sendMessage(player, message, type, categoryMessages, true);
    }

    /**
     * Todos los mensajes del plugin tiene que pasar por este metodo o por el metodo
     * {@link #logConsole(String, TypeMessages, CategoryMessages, boolean) sendMessageConsole}.
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

    public static void sendMessage(Player player, String message, @NotNull TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        player.sendMessage(applyFinalProprieties(message, type, categoryMessages, isPrefix));
    }

    /////////////////////////////
    ////////////////////////////

    public static void logConsole(Message message, TypeMessages type) {
        logConsole(message.getMessageLocatePrivate(), type, CategoryMessages.PRIVATE);
    }

    public static void logConsole(String message, TypeMessages type) {
        logConsole(message, type, CategoryMessages.PRIVATE);
    }

    public static void logConsole(String message, TypeMessages type, boolean prefix) {
        logConsole(message, type, CategoryMessages.PRIVATE, prefix);
    }

    public static void logConsole(String message, @NotNull TypeMessages type, CategoryMessages categoryMessages) {
        logConsole(message, type, categoryMessages, true);
    }

    public static void logConsole(String message, @NotNull TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        Component component = applyFinalProprieties(message, type, categoryMessages, isPrefix);
        switch (type){
            case ERROR -> AviaTerraCore.getInstance().getComponentLogger().error(component);
            case WARNING -> AviaTerraCore.getInstance().getComponentLogger().warn(component);
            default -> AviaTerraCore.getInstance().getComponentLogger().info(component);
        }
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

    public static String addProprieties(String message, @Nullable TypeMessages type, boolean isResister, boolean showPrefix) {
        if (isResister) {
            while (Character.isSpaceChar(message.charAt(message.length() - 1))) {
                message = message.substring(0, message.length() - 1);
            }
            message = message + " <red>[R]";
        }
        String s;
        if (showPrefix) {
            s = PREFIX;
        } else {
            s = "";
        }
        if (type == null) type = TypeMessages.NULL;
        String colorMain = type.getMainColor();
        return s + colorMain + message
                .replace("<|", type.getSecondColor())
                .replace("|>", "<reset>" + colorMain)
                .replace("|!>", "<reset>" + colorMain)
                .replace("`", "");
    }

    public static Component applyFinalProprieties(String message, @NotNull TypeMessages type, CategoryMessages categoryMessages, boolean isPrefix) {
        if (categoryMessages != CategoryMessages.PRIVATE) {
            sendMessageLogDiscord(type, categoryMessages, message);
        }
        String s = addProprieties(GlobalUtils.convertToMiniMessageFormat(message), type, categoryMessages != CategoryMessages.PRIVATE, isPrefix);
        return AviaTerraCore.getMiniMessage().deserialize(s);
    }

    private static void sendMessageLogDiscord(TypeMessages type, CategoryMessages categoryMessages, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(),() -> {
            String finalMessage;
            switch (type) {
                case SUCCESS -> finalMessage = "🟩 " + message;
                case INFO -> finalMessage = "🟦 " + message;
                case WARNING -> finalMessage = "🟨 " + message;
                case ERROR -> finalMessage = "🟥 " + message;
                default -> {
                    return;
                }
            }
            //no parece que tenga sentido, pero sí lo tiene, es por qué asi puede quitar los códigos de color del texto
            Component component = MiniMessage.miniMessage().deserialize(finalMessage);
            // Convertir Component a texto plano
            String s = PlainTextComponentSerializer.plainText().serialize(component);
            //obtén el canal por su ID
            String chanelId = categoryMessages.getIdChannel();
            if (chanelId == null)return;
            TextChannel channel = AviaTerraCore.jda.getTextChannelById(chanelId);
            if (channel !=  null) {
                Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(TimeZone.getTimeZone("GMT-5").toZoneId());

                channel.sendMessage("`[" + formatter.format(instant) + "]` " +
                        s.replace("<|", "**").replace("|>", "**").replace("|!>", "")).queue();
            } else {
                throw new IllegalArgumentException("No se encontró canal de discord para los registro " + categoryMessages.name());
            }
        });
    }

    /**
     * Le manda un título a jugador respetando el formato de {@link #addProprieties(String, TypeMessages, boolean, boolean) addProprieties}
     */

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, TypeMessages type) {
        final Title.Times times = Title.Times.times(Duration.ofMillis(20L *fadeIn), Duration.ofMillis(20L *stay), Duration.ofMillis(20L *fadeOut));
        // Using the times object this title will use 500ms to fade in, stay on screen for 3000ms and then fade out for 1000ms
        final Title t = Title.title(
                AviaTerraCore.getMiniMessage().deserialize(GlobalUtils.convertToMiniMessageFormat(addProprieties(title, type, false, false))),
                AviaTerraCore.getMiniMessage().deserialize(GlobalUtils.convertToMiniMessageFormat(addProprieties(subtitle, type, false, false))),
                times
        );

        // Send the title, you can also use Audience#clearTitle() to remove the title at any time
        player.showTitle(t);
    }

    public static void deathMessage(@NotNull Player victim, @Nullable LivingEntity killer, @Nullable ItemStack stack, @NotNull EntityDamageEvent.DamageCause cause) {
        Random r = new Random();
        List<CommandSender> senders = new ArrayList<>(Bukkit.getOnlinePlayers());
        senders.add(Bukkit.getConsoleSender());
        for (CommandSender p : senders) {
            String message;//TODO: Incluir el dragon;
            if ((killer != null && killer.getType() != EntityType.PLAYER && (
                    cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ||
                    cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) ||
                    cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) ||
                    cause.equals(EntityDamageEvent.DamageCause.PROJECTILE))
            )) {
                Locale locale;
                if (p instanceof Player player) {
                    locale = player.locale();
                }else {
                    locale = DEFAULT_LOCALE_PRIVATE.getLocale();
                }

                // Obtener el MessageFile del idioma del jugador
                MessageFile messageFile = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(LocaleAvailable.getLocate(locale).name().toLowerCase(), false);
                // Obtiene la lista de mensajes, en caso de que no existe tomara el idioma defecto
                List<String> messages = Objects.requireNonNullElseGet(messageFile.messagesEntity.get(killer.getType()), () -> {
                    MessageFile mf = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(MessagesManager.DEFAULT_LOCALE_PRIVATE.name().toLowerCase(), false);
                    return mf.messagesEntity.get(killer.getType());
                });
                // Obtiene un mensaje aleatorio
                message = messages.get(r.nextInt(messages.size()));
            }else {
                if (killer != null && killer.getType() == EntityType.PLAYER && cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                    message = Message.DEATH_CAUSE_END_CRYSTAL.getMessage(p);
                }else {
                    message = Message.valueOf("DEATH_CAUSE_" + cause.name()).getMessage(p);
                }
            }
            Builder tc = Component.text();
            Component component = tc.append(AviaTerraCore.getMiniMessage().deserialize("<dark_gray>[<dark_red>☠</dark_red>]</dark_gray> ")).append(applyFinalProprieties(message, TypeMessages.INFO, CategoryMessages.PRIVATE, false)).build();

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
                    Component customName = killer.customName();
                    if (customName != null) {
                        componentKiller.append(customName).append(AviaTerraCore.getMiniMessage().deserialize(" (" + "<lang:entity.minecraft." + killer.getType().name().toLowerCase() + ">" + ")"));
                    }else {
                        componentKiller.append(AviaTerraCore.getMiniMessage().deserialize("<lang:entity.minecraft." + killer.getType().name().toLowerCase() + ">"));
                    }
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
            p.sendMessage(component);
        }
    }
}
