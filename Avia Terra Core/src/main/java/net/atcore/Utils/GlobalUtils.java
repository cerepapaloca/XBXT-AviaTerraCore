package net.atcore.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Charsets;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.MessagesManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.atcore.Messages.MessagesManager.COLOR_ERROR;

@UtilityClass//Le añade static a todos los métodos y a las variables
public final class GlobalUtils {

    public final NamespacedKey KEY_ANTI_DUPE = new NamespacedKey(AviaTerraCore.getInstance(), "uuid");

    public @NotNull String applyGradient(String input){
        return applyGradient(input , 'r');
    }

    /**
     * Crea un gradiente de color en un texto. Para crear un
     * degradado tienes que poner {@code <#FFEEDD>Text<#FFEEDD>} obviamente tiene
     * que ser colores en Hex y si necesitas añadir varios degradados en un texto
     * solo lo separas con {@code ::} un ejemplo {@code <#Hex>Text1<#Hex>::<#Hex>Text2<#Hex>}
     * @param input El texto al que quieres darle el degradado
     * @param in el formato del texto que usa minecraft por ejemplo la {@code l} da negrilla o
     *           la {@code o} pone el texto en cursiva
     * @return te da el texto con los degradados y formato
     */

    public @NotNull String applyGradient(@NotNull String input, char in) {
        if (input.contains("</#"))input = input.replace("/","");
        input = input.replace("##","#");
        StringBuilder gradientText = new StringBuilder();

        for (String s : input.split("::")){
            // Extraer colores de degradado y texto
            String startTag = s.substring(s.indexOf("<#") + 2, s.indexOf(">")).replace("#", "");
            String endTag = s.substring(s.lastIndexOf("<#") + 2, s.lastIndexOf(">")).replace("#", "");
            String text = s.substring(s.indexOf(">") + 1, s.lastIndexOf("<"));

            //convierte el String en números Int respetando la base hexadecimal
            int startColor = Integer.parseInt(startTag, 16);
            int endColor = Integer.parseInt(endTag, 16);

            int length = text.length();

            for (int i = 0; i < length; i++) {
                //No Tengo ni idea de como funciona esto pero funciona
                float ratio = (float) i / (length - 1);
                int red = (int) ((1 - ratio) * ((startColor >> 16) & 0xFF) + ratio * ((endColor >> 16) & 0xFF));
                int green = (int) ((1 - ratio) * ((startColor >> 8) & 0xFF) + ratio * ((endColor >> 8) & 0xFF));
                int blue = (int) ((1 - ratio) * (startColor & 0xFF) + ratio * (endColor & 0xFF));
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                if (in =='r'){
                    gradientText.append(ChatColor.of(hexColor)).append(text.charAt(i));
                }
                gradientText.append(ChatColor.of(hexColor)).append("&").append(in).append(text.charAt(i));
                ChatColor.of("#" + hexColor);
            }
        }
        return  gradientText.toString();
    }

    /**
     * lo mismo que {@link #TimeToString(long, int)} pero se maneja segundos
     * @param second el tiempo en segundos y en un int
     */

    public String TimeToString(int second, int format){
        return TimeToString(second*1000L, format);
    }

    /**
     *
     * Convierte los ms en una String más bonita hay 3 formatos comenzando del 0
     * <ul>
     * <li> 0 te muestra el tiempo al estilo reloj digital un ejemplo {@code 12:10}
     * donde muestra los minutos y segundo
     * <li> 1 lo muestra todas las unidades de tiempo de esta manera {@code 2d 0h 20m 12s}
     * ideal para fechas largas
     * <li> 2 solo muestra él las unidades de tiempo que no sea ceros ejemplo {@code 2 dias 30 minutos}
     * ideal para marcar el tiempo máximo para algo
     * <ul>
     * @param time tiempo en MS y long
     * @param format un int que comienza del 0
     * @return el formato del seleccionado
     */

    public String TimeToString(long time, int format) {
        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        switch (format) {
            case 0 -> {
                String timeString = "";

                if (minutes < 10) {
                    timeString = "0" + minutes;
                }

                if (seconds < 10) {
                    return timeString + ":0" + seconds;
                } else {
                    return minutes + ":" + seconds;
                }
            }
            case 1 -> {
                return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
            }
            case 2 -> {
                String date = "";
                if (days != 0) {
                    date = date + days + " dias";
                } else if (hours != 0) {
                    date = date + hours + " horas";
                } else if (minutes != 0) {
                    date = date + minutes + " minutos";
                } else if (seconds != 0) {
                    date = date + seconds + " segundos";
                }
                return date;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public String[] EnumsToStrings(Enum[] raw){
        String[] strings = new String[raw.length];
        int i = 0 ;
        for (Enum e : raw){
            strings[i] = e.name().toLowerCase();
            i++;
        }
        return strings;
    }

    /**
     * Convierte un string de este estilo {@code 20d} en ms
     * @param time el string que quieres convertir a long
     * @return el tiempo en ms
     */

    public long StringToMilliseconds(@NotNull String time) {
        time = time.toLowerCase();
        char unit = time.charAt(time.length() - 1);
        long value = Long.parseLong(time.substring(0, time.length() - 1));

        return switch (unit) {
            case 's' -> // Segundos
                    value * 1000;
            case 'm' -> // Minutos
                    value * 1000 * 60;
            case 'h' -> // Horas
                    value * 1000 * 60 * 60;
            case 'd' -> // Días
                    value * 1000 * 60 * 60 * 24;
            default -> throw new IllegalArgumentException("Unidad de tiempo no válida: " + unit);
        };
    }

    /**
     * Añade una protección anti dupe básicamente se le asigna una uuid al item esto
     * hace que el item no se pueda estakear y si lo se pone en 1
     * @param item el item que le quieres aplicar la protección
     * @return el mismo item pero con la protección
     */

    public ItemStack addProtectionAntiDupe(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.getPersistentDataContainer().set(KEY_ANTI_DUPE, PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(meta);
        item.setAmount(1);//se pone uno por qué si el jugador lo divide va a ser baneado accidentalmente
        return item;
    }

    public void kickPlayer(Player player, String reason) {
        reason =ChatColor.translateAlternateColorCodes('&', MessagesManager.PREFIX_AND_SUFFIX_KICK[0]
                + "&4" + reason + "&c" + MessagesManager.PREFIX_AND_SUFFIX_KICK[1]);
        try {
            if (player.getName().startsWith("UNKNOWN[")){
                PacketContainer kickPack = new PacketContainer(PacketType.Login.Server.DISCONNECT);
                kickPack.getChatComponents().write(0, WrappedChatComponent.fromText(reason));
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, kickPack);
            }
        }finally {
            player.kickPlayer(reason);
        }
    }

    public UUID getUUIDByName(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
    }
}
