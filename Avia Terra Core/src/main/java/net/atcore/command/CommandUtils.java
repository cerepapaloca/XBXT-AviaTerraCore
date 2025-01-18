package net.atcore.command;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.ModeTab;
import net.atcore.utils.RangeType;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

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
                return List.of(ChatColor.translateAlternateColorCodes('&', "&cError. no tiene s, m, h, d. al final del argumento"));
            }
        }else {
            listOthers.add("##x");
            return listTab(arg, listOthers.toArray(new String[0]));
        }
    }

    /**
     * Variación de {@link #listTab(String, List, ModeTab)}
     */

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

    public @Nullable List<String> listTab(String arg, String... args){
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

    public void executeForPlayer(@Nullable CommandSender sender, @NotNull String arg, boolean safeMode, Consumer<TemporalPlayerData> action){
        if (arg.startsWith("*")){
            Bukkit.getOnlinePlayers().forEach(player ->  action.accept(new TemporalPlayerData(player.getName(), player)));
            return;
        }
        if (arg.startsWith("#") || arg.startsWith("!#")){
            Set<User> users = AviaTerraCore.getLp().getUserManager().getLoadedUsers();
            List<String> groups = new ArrayList<>(Arrays.stream(arg.replace("!", "").replace("#", "")
                    .split(",")).toList());
            Set<UUID> uuids = new HashSet<>();
            if (arg.startsWith("!")) { // Si está en opuesto Añade todos los jugadores a la lista
                for (Player player : Bukkit.getOnlinePlayers()) uuids.add(player.getUniqueId());
            }
            users.forEach(user -> {
                for (String group : groups) {
                    if (arg.startsWith("!")){ // Aquí se va borrando a los jugadores que pertenece a los grupos
                        if (user.getPrimaryGroup().equals(group)) uuids.remove(user.getUniqueId());
                    }else { // TODO falta hacer test mas completo de esto
                        if (user.getPrimaryGroup().equals(group)) uuids.add(user.getUniqueId());
                    }
                }
            });
            for (UUID uuid : uuids){
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) action.accept(new TemporalPlayerData(player.getName(), player));
            }

            return;
        }
        Set<String> names = new HashSet<>(Arrays.stream(arg.replace("!", "").split(",")).toList());
        for (Player player :  Bukkit.getOnlinePlayers()) {
            if (arg.charAt(0) == '!') { // En caso de que este invertido la lista
                if (!names.contains(player.getName())) { // No tiene que estar en la lista de jugadores
                    action.accept(new TemporalPlayerData(player.getName(), player));
                }/*else {
                    names.remove(player.getName());
                }*/
            } else {
                if (names.contains(player.getName())) {
                    // Se borra los nombres de los jugadores. En teoría si todos los jugadores de la lista están conectados debe estar vació al final del for
                    names.remove(player.getName());
                    action.accept(new TemporalPlayerData(player.getName(), player));
                }
            }
        }
        if (sender == null) return;
        if (names.isEmpty()) return; // Si no esta vaciá es por un jugador no se pudo borrar por que no esta conectado
        if (safeMode) {
            if (arg.charAt(0) != '!') MessagesManager.sendMessage(sender, String.format(Message.COMMAND_GENERIC_PLAYERS_NOT_FOUND.getMessage(sender), names), MessagesType.WARNING);
        }else {
            for (String name : names){ // Si no esta en modo seguro crea un TemporalPlayerData con los nombres de los usuarios
                action.accept(new TemporalPlayerData(name, null));
            }
        }
    }

    public List<String> tabForPlayer(String arg){
        if (arg.startsWith("*")){
            return List.of("*");
        }
        if (arg.startsWith("#") || arg.startsWith("!#")){ // En caso de que inicie con los grupos
            List<String> nameGroup = new ArrayList<>();
            Set<Group> groups = AviaTerraCore.getLp().getGroupManager().getLoadedGroups();
            groups.forEach(group -> { // agrega la lista de los nombres de los grupos
                if (arg.startsWith("!")){
                    nameGroup.add("!#" + group.getName());
                }else {
                    nameGroup.add("#" + group.getName());
                }
            });
            String argNormalize = arg.endsWith(",") ? "" : Arrays.stream(arg.split(",")).toList().getLast().toLowerCase()
                    .replace("!", "").replace("#", "");
            List<String> finalNamesList = new ArrayList<>(nameGroup.stream()
                    .filter(name -> name.toLowerCase().contains(argNormalize.toLowerCase())).toList());
            if (arg.endsWith(",")){
                groups.forEach(group -> finalNamesList.add(arg + group.getName()));
                return finalNamesList;
            }
            if (finalNamesList.isEmpty() || finalNamesList.getFirst().equals(arg)){
                return List.of(arg + ",");
            }
            if (arg.contains(",")){
                finalNamesList.replaceAll(s -> arg.substring(0, arg.lastIndexOf(",")) + "," + s.replace("#", "").replace("!", ""));
            }
            return finalNamesList;
        }
        boolean b = arg.startsWith("!");
        List<String> namesList = new ArrayList<>();
        if (arg.isEmpty()){ // Aquí es donde se le agrega las posibles opciones al jugador
            namesList.add("*");
            namesList.add("!");
            namesList.add("#");
            Bukkit.getOnlinePlayers().forEach(player -> namesList.add(player.getName()));
            return namesList;
        }
        Bukkit.getOnlinePlayers().forEach(player -> namesList.add(player.getName()));
        String argNormalize = arg.endsWith(",") ? "" : Arrays.stream(arg.split(",")).toList().getLast().toLowerCase().replace("!", "");
        List<String> finalNamesList = namesList.stream()
                .filter(name -> name.toLowerCase().contains(argNormalize))
                .collect(toList());
        if (finalNamesList.isEmpty() || finalNamesList.getFirst().equals(argNormalize)) {
            // Si el argumento termina en "," es por qué ya termino de escribir el nombre del usuario y está listo de escribir el siguiente
            if (arg.endsWith(",")){
                // Se añade una lista de usuarios conservando el argumento al principio
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
            if (arg.equals("!")){
                finalNamesList.add("!#");
            }
            return finalNamesList;
        }
    }

    /**
     * Comprueba que si el jugador tiene los permisos. Si el permiso tiene {@code *}
     * todos los jugadores logueados tienen permiso, pero si tiene {@code **} todos aunque
     * no estén logueados, si el permiso comienza con {@code !} el jgador no debe tener
     * ese permiso. Se puede unir varios permisos con {@code ,}estó permisos extras hace
     * de "o" es decir la condición será verdadera cuando cumpla uno de los permisos
     * @param permission Los permisos suele ser así {@code aviaterracore.command.prueba}
     * @param player el jugador que le va hace el check
     * @param limbo ¿Puede entrar en modo limbo?
     * @return verdadero sí tiene permiso
     */

    public boolean hasPermission(String permission, Player player, boolean limbo){
        //String permissionBase = AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.toLowerCase();
        //if (player.hasPermission(permissionBase)) return true;
        //permission = permission.replace(permissionBase, "");

        if (permission.equals("!*"))return false;
        if (LoginManager.checkLoginIn(player, true, limbo)) {
            if (player.isOp()) return true;
            if (permission.contains("!")) permission = "!" + permission.replace("!", "");
            if (permission.equals("*") || permission.equals("**")) {
                return true;
            }
            boolean b = false;
            for (String s : permission.replace("!","").split(",")){
                if (permission.startsWith("!")) {
                    b = b || !player.hasPermission(s);
                } else {
                    b = b || player.hasPermission(s);
                }
            }
            return b;
        }else {
            return permission.equals("**");
        }
    }

    @Contract(pure = true)
    public boolean hasPermission(@NotNull String permission, RangeType range){
        if (permission.equals("!**"))return false;
        if (range.isOp()) return true;
        if (permission.contains("!")) permission = "!" + permission.replace("!", "");
        if (permission.equals("*") || permission.equals("**")) {
            return true;
        }
        boolean b = false;
        for (String s : permission.replace("!","").split(",")){
            if (permission.startsWith("!")) {
                b = b || !range.getPermission().equals(s);
            } else {
                b = b || range.getPermission().equals(s);
            }
        }
        return b;
    }

    @Contract(pure = true)
    public String useToUseDisplay(String use){
        use = use.replace("<!", "<");
        use = use.replace("_", " ");
        return use;
    }
}
