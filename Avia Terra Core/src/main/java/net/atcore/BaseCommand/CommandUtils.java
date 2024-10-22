package net.atcore.BaseCommand;

import lombok.experimental.UtilityClass;
import net.atcore.Utils.ModeTab;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.atcore.Messages.MessagesManager.COLOR_ERROR;

@UtilityClass
public final class CommandUtils {

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

    public List<String> listTabTime(String arg, String...others){
        List<String> listOthers = new ArrayList<>(Arrays.stream(others).toList());
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
     * Es realiza la misma función que {@link #listTab(String, String[], ModeTab)}. Pero
     * elimina el parámetro {@code ModeTab} y pone como por defecto {@code ModeTab.StartWithIgnoreCase}
     *
     */

    public @Nullable List<String> listTab(String arg, String[] args){
        return listTab(arg, args, ModeTab.StartWithIgnoreCase);
    }

    /**
     * Filtra una lista de argumentos a través de un argumento que está escribiendo el usuario.
     * @param arg El argumento que se está escribiendo
     * @param args La lista de argumentos disponibles
     * @param mode El modo de filtrado
     * @return te vuelve la lista de argumento disponible para el argumento que se está
     * escribiendo actualmente
     */

    public @Nullable List<String> listTab(String arg, String[] args, @NotNull ModeTab mode){
        switch (mode){
            case Contains -> {
                return Arrays.stream(args)
                        .toList().stream()
                        .filter(name -> name.contains(arg))
                        .collect(Collectors.toList());
            }
            case ContainsIgnoreCase -> {
                return Arrays.stream(args)
                        .toList().stream()
                        .filter(name -> name.toLowerCase().contains(arg.toLowerCase()))
                        .collect(Collectors.toList());
            }
            case StartWith -> {
                return Arrays.stream(args)
                        .toList().stream()
                        .filter(name -> name.startsWith(arg))
                        .collect(Collectors.toList());
            }
            case StartWithIgnoreCase -> {
                return Arrays.stream(args)
                        .toList().stream()
                        .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

}
