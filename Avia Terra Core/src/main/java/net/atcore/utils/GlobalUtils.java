package net.atcore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Charsets;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
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
     * @deprecated Mejor usar {@link Gradient}
     */

    @Deprecated
    @Contract(pure = true)
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
                }else{
                    gradientText.append(ChatColor.of(hexColor)).append("&").append(in).append(text.charAt(i));
                }

                //ChatColor.of(hexColor);
            }
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', gradientText.toString());
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
     * <li> {@code 0} te muestra el tiempo al estilo reloj digital un ejemplo {@code 12:10}
     * donde muestra los minutos y segundo
     * <li> {@code 1} lo muestra todas las unidades de tiempo de esta manera {@code 2d 0h 20m 12s}
     * ideal para fechas largas
     * <li> {@code 2} solo muestra él las unidades de tiempo que no sea ceros ejemplo {@code 2 dias 30 minutos}
     * ideal para marcar el tiempo máximo para algo
     * <ul>
     * @param time tiempo en MS y long
     * @param format un int que comienza del 0
     * @param isGlobalDate indica si se tiene que restar con el tiempo actual
     * @return el formato del seleccionado
     */

    @Contract(pure = true)
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

    /**
     * Añade una protección anti dupe.
     * <ul>
     * Añade una persisten Data que contiene un {@code ?} indicando que el item está a la
     * espera de la asignación de la UUID, hasta entonces el item se puede duplicar pero
     * cuando {@link net.atcore.moderation.ban.CheckAutoBan#checkDupe(Player, Inventory) checkDupe()}
     * lo llaman le asigna una UUID única para que este no se pueda duplicar
     *
     * @param item el item que le quieres aplicar la protección
     */

    public void addProtectionAntiDupe(@NotNull ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(KEY_ANTI_DUPE, PersistentDataType.STRING, "?");
        item.setItemMeta(meta);
        item.setAmount(1);// Se pone uno, por qué si el jugador lo divide va a ser baneado accidentalmente
    }

    /**
     * Añade variable a items, las variables que se manejan son de {@code PersistentDataType}
     * <ul>
     * <Strong>Se tiene que usar en antes o después de asignar el item meta si no se sobreescribirá</Strong>
     * @param itemStack el item que quieres añadir
     * @param nameKey nombre de la variable
     * @param type el tipo de la variable
     * @param data la variable
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setPersistentData(@NotNull ItemStack itemStack, String nameKey, PersistentDataType type, Object data){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = new NamespacedKey(AviaTerraCore.getInstance(), nameKey);
        meta.getPersistentDataContainer().set(key, type, data);
        itemStack.setItemMeta(meta);
    }

    @Contract(pure = true)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getPersistenData(@NotNull ItemStack item, String nameKey, PersistentDataType type){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(AviaTerraCore.getInstance(), nameKey);
        if (dataContainer.has(key)){
            return dataContainer.get(key, type);
        }else{
            return null;
        }
    }

    public static void removePersistenData(@NotNull ItemStack item, String nameKey){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        NamespacedKey key = new NamespacedKey(AviaTerraCore.getInstance(), nameKey);
        meta.getPersistentDataContainer().remove(key);
    }

    public static void addItemPlayer(@NotNull ItemStack item, @NotNull Player player, boolean silent, boolean protection){
        if (!silent) player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1,1);
        if (protection) GlobalUtils.addProtectionAntiDupe(item);
        if (player.getInventory().addItem(item).isEmpty()) return;
        Location location = player.getLocation();
        World world = player.getWorld();
        world.dropItemNaturally(location, item);
    }

    public void synchronizeKickPlayer(@NotNull Player player, @Nullable String reason){
        if (Bukkit.isPrimaryThread()){
            kickPlayer(player, reason);
        }else {
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> kickPlayer(player, reason));
        }
    }

    /**
     * Expulsa el jugador sin importar que esté en modo {@code Login} o en modo {@code Play}
     * y respetando el formato de razón del kick
     */

    public void kickPlayer(@NotNull Player player,@Nullable String reason) {
        reason = org.bukkit.ChatColor.translateAlternateColorCodes('&', MessagesManager.addProprieties(Message.MISC_KICK_UPPER
                + "&4" + (reason == null ? "Has sido expulsado" : reason) + "&c" + Message.MISC_KICK_LOWER, MessagesType.KICK, false, false));
        if (Bukkit.isPrimaryThread()){
            kickFinal(player, reason);
        }else{
            @NotNull String finalReason = reason;
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> kickFinal(player, finalReason));
        }
    }

    private static void kickFinal(@NotNull Player player, @NotNull String finalReason) {
        try {
            if (player.getName().startsWith("UNKNOWN[")){
                PacketContainer kickPack = new PacketContainer(PacketType.Login.Server.DISCONNECT);
                kickPack.getChatComponents().write(0, WrappedChatComponent.fromText(finalReason));
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, kickPack);
            }
        }finally {
            player.kickPlayer(finalReason);
        }
    }

    /**
     * Obtienes la uuid del jugador usando el mismo sistema que usa el servidor
     * para asignarle la uuid a los jugadores no premium
     * @param username el nombre de usuario que le quieres sacar la uuid
     * @return la uuid del jugador
     */

    @Contract(pure = true)
    public UUID getUUIDByName(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
    }

    /**
     * De la variable {@link Color} de bukkit lo convierte en el
     * clasico formato hex, así {@code #AABBCC}
     */

    @SuppressWarnings("DuplicatedCode") // Es curioso que tenga que poner esto
    @Contract(pure = true)
    public String BukkitColorToStringHex(Color color) {
        String r = addZeros(Integer.toString(color.getRed(), 16));
        String g = addZeros(Integer.toString(color.getGreen(), 16));
        String B = addZeros(Integer.toString(color.getBlue(), 16));
        return ("#" + r + g + B).toUpperCase();
    }

    /**
     * De la variable {@link java.awt.Color Color} de java lo convierte en el
     * clasico formato hex, así {@code #AABBCC}
     */

    @SuppressWarnings("DuplicatedCode")
    @Contract(pure = true)
    public String javaColorToStringHex(java.awt.Color color) {
        String r = addZeros(Integer.toString(color.getRed(), 16));
        String g = addZeros(Integer.toString(color.getGreen(), 16));
        String B = addZeros(Integer.toString(color.getBlue(), 16));
        return ("#" + r + g + B).toUpperCase();
    }

    @Contract(pure = true)
    public @NotNull Color colorToColor(java.awt.@NotNull Color color) {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Contract(pure = true, value = "_ -> new")
    public @NotNull java.awt.Color colorToColor(@NotNull Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Contract(pure = true)
    private @NotNull String addZeros(@NotNull String s){
        switch (s.length()){
            case 0 -> {
                return "00";
            }
            case 1 -> {
                return "0" + s;
            }
            default -> {
                return s;
            }
        }
    }

    public static @NotNull ArrayList<String> StringToLoreString(@NotNull String texto, boolean space) {
        return StringToLoreString(texto, 40, space, '7');
    }

    public static @NotNull ArrayList<String> StringToLoreString(@NotNull String texto, int longitud, boolean space) {
        return StringToLoreString(texto, longitud, space, '7');
    }

    public static @NotNull ArrayList<String> StringToLoreString(@NotNull String texto, boolean space, char color) {
        return StringToLoreString(texto, 40, space, color);
    }

    /**
     * Crea un lore a partir de un texto sin salto de línea esto es por qué minecraft el lore se ase con un {@code ArrayList<String>}
     * donde cada String es una liena es decir si tiene dos String en una List solo se muestra dos líneas en el item.
     * @param texto el texto que se va a transformar
     * @param longitud cada cuantas letras tiene que hacer el salto de linéa. Si usa {@code \n} crear un salo de linéa
     *                 sin importar si á llegado a la cantidad de letras
     * @param space si el lore tiene un espacio por arriba y por abajo es solo por qué el lore se ve mejor con el espacio
     * @param color el color por defecto que va a tener el lore
     * @return regresa una List conde cada elemento de la lista es un salto de liena del texto
     */

    @Contract(pure = true)
    public static @NotNull ArrayList<String> StringToLoreString(@NotNull String texto, int longitud, boolean space, char color) {
        texto = ChatColor.translateAlternateColorCodes('&', texto);
        ArrayList<String> lineas = new ArrayList<>();
        if (space) lineas.add(" ");

        String[] partes = texto.split("\n"); // Dividimos el texto en líneas si contiene \n

        for (String parte : partes) {
            int inicio = 0;
            while (inicio < parte.length()) {
                int fin = Math.min(inicio + longitud, parte.length());

                // Si el substring no termina en espacio, buscar el último espacio dentro del rango
                if (fin < parte.length() && parte.charAt(fin) != ' ') {
                    int ultimoEspacio = parte.lastIndexOf(' ', fin);
                    if (ultimoEspacio > inicio) {
                        fin = ultimoEspacio; // Ajusta el fin al último espacio encontrado
                    }
                }

                lineas.add(ChatColor.translateAlternateColorCodes('&', "&" + color + parte.substring(inicio, fin).trim()));
                inicio = fin + 1; // Salta el espacio
            }
        }

        if (space && !lineas.isEmpty()) lineas.add(" ");
        return lineas;
    }

    /**
     * Para buscar un jugador de manera segura sin que bukkit esté jodiendo con el {@code @Nullable}
     * usar solo cuando el jugador no debería dar nulo.
     * <ul>
     * <strong>No usar para comando o donde el nombre del jugador puede ser incorrecto</strong>
     * @param uuid la uuid del jugador
     * @return regresa el jugador con un 100% de probabilidades de que no sea nulo
     */

    @Contract(pure = true)
    public @NotNull Player getPlayer(UUID uuid){
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return player;
        throw new IllegalArgumentException("Un jugador dio nulo cuando no debería: " + uuid);
    }

    /**
     * @see #getPlayer(UUID) Mira esto 
     */

    public @NotNull Player getPlayer(String name){
        Player player = Bukkit.getPlayer(name);
        if (player != null) return player;
        throw new IllegalArgumentException("Un jugador dio nulo cuando no debería: " + name);
    }

    public boolean equalIp(@Nullable InetAddress ipA, @Nullable InetAddress ipB){
        if (ipA == null || ipB == null) return false;
        return Objects.equals(ipA.getHostAddress().split(":")[0], Objects.requireNonNull(ipB.getHostAddress().split(":")[0]));
    }

    /**
     * Modifica los colores hex usando HLS para que sea más fácil de modificar. Los
     * parámetros del HLS son "diferencias" es decir si no lo quieres modificar
     * usa el 0
     * @param hexColor El HEX
     * @param hueDelta se maneja -1 a 1
     * @param lightnessDelta se maneja -1 a 1
     * @param saturationDelta se maneja -1 a 1
     * @return El hex con las modificaciones
     */

    @NotNull
    public static String modifyColorHexWithHLS(@NotNull String hexColor, float hueDelta, float lightnessDelta, float saturationDelta) {
        if(hexColor.startsWith("#")){
            hexColor = hexColor.substring(1);
        }

        int r = Integer.valueOf(hexColor.substring(0, 2), 16);
        int g = Integer.valueOf(hexColor.substring(2, 4), 16);
        int b = Integer.valueOf(hexColor.substring(4, 6), 16);

        float[] hls = rgbToHLS(r, g, b);

        hls[0] = (hls[0] + hueDelta) % 1.0f;
        hls[1] = clamp(hls[1] + lightnessDelta, 0, 1);
        hls[2] = clamp(hls[2] + saturationDelta, 0, 1);

        int[] rgb = hlsToRGB(hls[0], hls[1], hls[2]);

        return String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]).toUpperCase();
    }

    @Contract("_, _, _ -> new")
    private static float @NotNull [] rgbToHLS(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));

        float h, s, l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            float delta = max - min;
            s = l > 0.5 ? delta / (2 - max - min) : delta / (max + min);
            if (max == rf) {
                h = (gf - bf) / delta + (gf < bf ? 6 : 0);
            } else if (max == gf) {
                h = (bf - rf) / delta + 2;
            } else {
                h = (rf - gf) / delta + 4;
            }
            h /= 6;
        }

        return new float[]{h, l, s};
    }

    @Contract("_, _, _ -> new")
    private static int @NotNull [] hlsToRGB(float h, float l, float s) {
        double r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRGB(p, q, h + 1.0f / 3);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1.0f / 3);
        }

        return new int[]{
                (int) Math.round(r * 255),
                (int) Math.round(g * 255),
                (int) Math.round(b * 255)
        };
    }

    private static double hueToRGB(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2) return q;
        if (t < 2.0 / 3) return (p + (q - p) * (2.0 / 3 - t) * 6);
        return p;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

}
