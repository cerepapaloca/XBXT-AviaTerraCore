package net.atcore.Utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass//Le añade static a todos los métodos y a las variables
public final class GlobalUtils {

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

    public @NotNull String applyGradient(String input, char in) {
        if (input.contains("</#"))input = input.replace("/","");
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
            }
        }
        return  gradientText.toString();
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
     * @param mode El modo de filtrado sé según como lo pida la situación
     * @return te vuelve la lista de argumento disponible para el argumento que se está
     * escribiendo actualmente
     */

    public static @Nullable List<String> listTab(String arg, String[] args, ModeTab mode){
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
