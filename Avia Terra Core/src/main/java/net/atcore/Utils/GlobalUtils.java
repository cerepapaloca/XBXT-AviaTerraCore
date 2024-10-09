package net.atcore.Utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class GlobalUtils {

    public static @NotNull String applyGradient(String input){
        return applyGradient(input , 'r');
    }

    public static @NotNull String applyGradient(String input, char in) {
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
                //No Tengo ni una idea de como funciona esto pero funciona
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
}
