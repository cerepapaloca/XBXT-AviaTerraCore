package net.atcore.command;

import lombok.experimental.UtilityClass;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.ModeTab;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.atcore.messages.MessagesManager.COLOR_ERROR;

@UtilityClass
public final class CommandUtils {

    /**
     * Crea una lista para el tab usando un enum
     * @param raw la clase de enum
     * @return lista de enum en string
     */

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

    public @Nullable List<String> listTab(String arg, List<String> args){
        return listTab(arg, args, ModeTab.StartWithIgnoreCase);
    }

    /**
     * Es realiza la misma función que {@link #listTab(String, List, ModeTab)}. Pero
     * elimina el parámetro {@code ModeTab} y pone como por defecto {@code ModeTab.StartWithIgnoreCase}
     *
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

}
