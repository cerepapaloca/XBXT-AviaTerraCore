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
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
     * Lo mismo que {@link #timeToString(long, int, boolean)}
     */

    public String timeToString(long time, int format){
        return timeToString(time, format, false);
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
     * @param isGlobalDate indica si se tiene que restar con el tiempo actual
     * @return el formato del seleccionado
     */

    public String timeToString(long time, int format, boolean isGlobalDate) {
        if (time == GlobalConstantes.NUMERO_PERMA) return "permanente";
        if (isGlobalDate) time = time - System.currentTimeMillis();
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
     * Añade una protección anti dupe básicamente se le asigna una uuid al item esto
     * hace que el item no se pueda estakear y si lo se pone en 1
     * @param item el item que le quieres aplicar la protección
     */

    public void addProtectionAntiDupe(@NotNull ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(KEY_ANTI_DUPE, PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(meta);
        item.setAmount(1);//se pone uno por qué si el jugador lo divide va a ser baneado accidentalmente
    }

    /**
     * Añade variable a items, las variables que se manejan {@code PersistentDataType}
     * @param itemStack el item que quieres añadir
     * @param nameKey nombre de la variable
     * @param type el tipo de la variable
     * @param data la variable
     */

    public void addPersistentDataItem(@NotNull ItemStack itemStack, String nameKey, PersistentDataType type, Object data){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = new NamespacedKey(AviaTerraCore.getInstance(), nameKey);
        meta.getPersistentDataContainer().set(key, type, data);
        itemStack.setItemMeta(meta);
    }

    public Object getPersistenData(@NotNull ItemStack item, String nameKey, PersistentDataType type){
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(AviaTerraCore.getInstance(), nameKey);
        if (dataContainer.has(key)){
            return dataContainer.get(key, type);
        }else{
            return null;
        }
    }

    public static void addItemPlayer(@NotNull ItemStack item, @NotNull Player player, boolean isSilent){
        if (!isSilent) player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1,1);
        if (player.getInventory().addItem(item).isEmpty()) {
            return;
        }
        Location location = player.getLocation();
        World world = player.getWorld();
        world.dropItemNaturally(location, item);
    }

    /**
     * Expulsa el jugador sin importar que esté en modo {@code Login} o en modo {@code Play}
     * y respetando el formato de razón del kick
     */

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

    /**
     * Obtienes la uuid del jugador usando el mismo sistema que usa el servidor
     * para asignarle la uuid a los jugadores no premium
     * @param username el nombre de usuario que le quieres sacar la uuid
     * @return la uuid del jugador
     */

    public UUID getUUIDByName(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
    }
}
