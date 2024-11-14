package net.atcore.command;

import lombok.experimental.UtilityClass;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.ModeTab;
import net.dv8tion.jda.api.interactions.callbacks.IPremiumRequiredReplyCallback;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static net.atcore.messages.MessagesManager.COLOR_ERROR;

@UtilityClass
public final class CommandUtils {

    /**
     * Variación del {@link #enumsToStrings(Enum[], boolean)}
     */

    @SuppressWarnings("rawtypes")
    public String[] enumsToStrings(Enum[] raw){
        return enumsToStrings(raw, true);
    }

    /**
     * Crea una lista para el tab usando un enum
     * @param raw la clase de enum
     * @param b ¿Se modifica las mayúsculas?
     * @return lista de enum en string
     */

    @SuppressWarnings("rawtypes")
    public String[] enumsToStrings(Enum[] raw, boolean b){
        String[] strings = new String[raw.length];
        int i = 0 ;
        for (Enum e : raw){
            strings[i] = b ? e.name().toLowerCase() : e.name();
            i++;
        }
        return strings;
    }

    public boolean isTrueOrFalse(String ars) {
        ars = ars.toLowerCase();
        switch (ars) {
            case "true" ->{
                return true;
            }
            case "false" ->{
                return false;
            }
        }
        return false;
    }

    public String booleanToString(boolean bool) {
        return bool ? "<|Activo|>" : "<|Desactivado|>";
    }

    public List<String> listTabTime(String arg, boolean numberSpecial){
        List<String> listOthers = new ArrayList<>();
        if (numberSpecial) listOthers = new ArrayList<>(Arrays.stream(new String[]{"permanente", "maximo"}).toList());
        List<Character> chars = List.of('s','m','h','d');
        if (!arg.isEmpty() && Character.isDigit(arg.charAt(0))){
            if (chars.contains(arg.charAt(arg.length() - 1))){
                List<String> list = new ArrayList<>(List.of(arg));
                list.addAll(listOthers);
                return list;
            }else{
                return List.of(ChatColor.translateAlternateColorCodes('&', COLOR_ERROR + "Error. no tiene s, m, h, d. al final del argumento"));
            }
        }else {
            listOthers.add("##x");
            return listTab(arg, listOthers.toArray(new String[0]));
        }
    }

    /**
     * Variación de {@link #listTab(String, List, ModeTab)}
     */

    public @Nullable List<String> listTab(String arg, String[] args, ModeTab modeTab){
        return listTab(arg, Arrays.asList(args), modeTab);
    }

    /**
     * Variación de {@link #listTab(String, List, ModeTab)}
     */

    public @Nullable List<String> listTab(String arg, List<String> args){
        return listTab(arg, args, ModeTab.StartWithIgnoreCase);
    }

    /**
     * Es realiza la misma función que {@link #listTab(String, List, ModeTab)}. Pero
     * elimina el parámetro {@code ModeTab} y pone como por defecto {@code ModeTab.StartWithIgnoreCase}
     */

    public @Nullable List<String> listTab(String arg, String[] args){
        return listTab(arg, Arrays.stream(args).toList(), ModeTab.StartWithIgnoreCase);
    }

    /**
     * Filtra una lista de argumentos a través de un argumento que está escribiendo el usuario.
     * @param arg El argumento que se está escribiendo
     * @param args La lista de argumentos disponibles
     * @param mode El modo de filtrado
     * @return te vuelve la lista de argumento disponible para el argumento que se está
     * escribiendo actualmente
     */

    public @Nullable List<String> listTab(String arg,  List<String> args, @NotNull ModeTab mode){
        switch (mode){
            case Contains -> {
                return args.stream()
                        .filter(name -> name.contains(arg))
                        .collect(toList());
            }
            case ContainsIgnoreCase -> {
                return args.stream()
                        .filter(name -> name.toLowerCase().contains(arg.toLowerCase()))
                        .collect(toList());
            }
            case StartWith -> {
                return args.stream()
                        .filter(name -> name.startsWith(arg))
                        .collect(toList());
            }
            case StartWithIgnoreCase -> {
                return args.stream()
                        .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                        .collect(toList());
            }
        }
        return null;
    }

    /**
     * Convierte un string de este estilo {@code 20d} en ms
     * @param time el string que quieres convertir a long
     * @return el tiempo en ms
     */

    public long StringToMilliseconds(@NotNull String time, boolean numberSpecial) throws IllegalArgumentException{
        if (numberSpecial){
            if (time.equalsIgnoreCase("permanente")) return GlobalConstantes.NUMERO_PERMA;
            if (time.equalsIgnoreCase("maximo")) return Long.MAX_VALUE;
        }
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

    public void excuteForPlayer(@Nullable CommandSender sender, String arg, boolean safeMode, Consumer<DataTemporalPlayer> action){
        if (arg.charAt(0) == '*'){
            Bukkit.getOnlinePlayers().forEach(player ->  action.accept(new DataTemporalPlayer(player.getName(), player)));
            return;
        }
        List<String> names = new ArrayList<>(Arrays.stream(arg.replace("!", "").split(",")).toList());
        for (Player player :  Bukkit.getOnlinePlayers()) {
            if (arg.charAt(0) == '!') {
                if (!names.contains(player.getName())) {
                    action.accept(new DataTemporalPlayer(player.getName(), player));
                }else {
                    names.remove(player.getName());
                }
            } else {
                if (names.contains(player.getName())) {
                    names.remove(player.getName());
                    action.accept(new DataTemporalPlayer(player.getName(), player));
                }
            }
        }
        if (sender == null) return;
        if (names.isEmpty()) return;
        if (safeMode) {
            MessagesManager.sendMessage(sender, String.format("El jugador/es <|%s|> no esta conectado o no existe", names), TypeMessages.WARNING);
        }else {
            for (String name : names){
                action.accept(new DataTemporalPlayer(name, null));//tiene que dar uno si no yo me dio nulo
            }
        }
    }

    public List<String> tabForPlayer(String arg){
        if (arg.startsWith("*")){
            return List.of("*");
        }
        boolean b = arg.startsWith("!");
        List<String> namesList = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> namesList.add(player.getName()));
        if (arg.isEmpty()){
            namesList.add("*");
            namesList.add("!");
            return namesList;
        }
        String argNormalize = arg.endsWith(",") ? "" : Arrays.stream(arg.split(",")).toList().getLast().toLowerCase().replace("!", "");
        List<String> finalNamesList = namesList.stream()
                .filter(name -> name.toLowerCase().contains(argNormalize))
                .collect(toList());
        if (finalNamesList.isEmpty() || finalNamesList.getFirst().equals(argNormalize)) {
            if (arg.endsWith(",")){
                namesList.replaceAll(s -> arg + "," + s);
                return namesList;
            }else {
                return List.of(arg + ",");
            }
        }else {
            if (arg.contains(",")){
                finalNamesList.replaceAll(s -> arg.substring(0, arg.lastIndexOf(",")) + "," + s);
            }
            if (b && !finalNamesList.getLast().startsWith("!")){
                finalNamesList.replaceAll(s -> "!" + s);
            }
            return finalNamesList;
        }
    }

    /**
     * Comprueba que si el jugador tiene los permisos. Si el permiso tiene {@code *}
     * todos los jugadores tienen permiso, si el permiso comienza con {@code !} el
     * jugador no debe tener ese permiso. Se puede unir varios permisos con {@code ,}
     * estó permisos extras hace de "o" es decir la condición será verdadera cuando
     * cumpla uno de los permisos
     * @param permission Los permisos suele ser así {@code aviaterra.command.prueba}
     * @param player el jugador que le va hace el check
     * @return verdadero sí tiene permiso
     */

    public boolean hasPermission(String permission, Player player){
        if (permission.equals("*")){
            return true;
        }else {
            if (permission.charAt(0) == '!'){
                if (permission.substring(1).equalsIgnoreCase("op")){
                    return !player.isOp();
                }
            }else {
                if (permission.equalsIgnoreCase("op")){
                    return player.isOp();
                }
            }
        }
        for (String s : permission.replace("!","").split(",")){
            if (permission.charAt(0) == '!') {
                return !player.hasPermission(s);
            } else {
                return player.hasPermission(s);
            }
        }
        return false;
    }
}
