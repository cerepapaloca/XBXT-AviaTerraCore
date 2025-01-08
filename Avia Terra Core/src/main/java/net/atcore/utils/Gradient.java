package net.atcore.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
@Deprecated
/**
 * Creas un degradado para el texto más complejo y com más colores.
 * <p>
 * Para crear un degradado tiene que usar {@link #addGradient(Color, double) addGradient()} y añadir un
 * {@link java.awt.Color} y una proporción luego usar {@link #getText()} para tener el resultado final
 * <blockquote><pre>
 *     Gradient gradient = new Gradient("Un bonito degradado");
 *     gradient.addGradient(Color.RED, 1)
 *         .addGradient(Color.GREEN, 1)
 *         .addGradient(Color.RED, 1);
 *     MessagesManager.sendMessageConsole(gradient.getText(), TypeMessages.NULL, CategoryMessages.PRIVATE, false);
 * </pre></blockquote>
 */

@SuppressWarnings("unused")
public class Gradient {

    private final char[] colors;

    public Gradient(String text, char... colors) {
        this.text = text;
        this.colors = colors;
    }

    private static class GradientSegment {
        Color color;
        double proportion;

        GradientSegment(Color color, double proportion) {
            this.color = color;
            this.proportion = proportion;
        }
    }

    private final String text;
    private final List<GradientSegment> segments = new ArrayList<>();
    private double totalProportion = 0;

    /**
     * Añades un gradiente al texto donde este será lineal
     * @param color El color tiene que ser de Java y no de Bukkit
     * @param proportion Es la cantidad de color que va a ocupar en el texto ejemplo si tiene un proportion de
     *                   1 1 2 el tercer degradado va a ocupar la mitad del texto
     */

    public Gradient addGradient(Color color, double proportion) {
        if (proportion <= 0) {
            throw new IllegalArgumentException("La proporción debe ser mayor que 0.");
        }
        segments.add(new GradientSegment(color, proportion));
        totalProportion += proportion;
        return this;
    }

    /**
     * Realiza él degrado al texto
     * @return Texto con el gradado aplicado
     */

    @Contract(pure = true)
    public String getText(){
        int length = text.length();
        StringBuilder gradientText = new StringBuilder();
        StringBuilder coloredText = new StringBuilder();
        for (char c : colors) coloredText.append('&').append(c);
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            gradientText.append(ChatColor.of(GlobalUtils.javaColorToStringHex(getColor(ratio)))).append(coloredText).append(text.charAt(i));
        }
        return ChatColor.translateAlternateColorCodes('&', gradientText.toString());
    }

    @Override
    public String toString() {
        return getText();
    }

    public String getRawText() {
        return text;
    }

    /**
     * Obtienes el color del degradado en un punto exacto
     * @param value Tiene que ser mayor que 0 y menor que 1
     * @return Obtienes el color en un punto exacto <strong>ojo la variable color es de java no de bukkit</strong>
     */
    @Contract(pure = true)
    public Color getColor(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("El valor debe estar entre 0 y 1. El valor es: " + value);
        }

        double accumulatedProportion = 0;
        for (int i = 0; i < segments.size() - 1; i++) {
            GradientSegment start = segments.get(i);
            GradientSegment end = segments.get(i + 1);

            double startProportion = accumulatedProportion / totalProportion;
            double endProportion = (accumulatedProportion + end.proportion) / totalProportion;

            if (value >= startProportion && value <= endProportion) {
                double localValue = normalize(startProportion, endProportion, value);
                return blendColors(start.color, end.color, localValue);
            }
            accumulatedProportion += end.proportion;
        }

        return segments.getLast().color;
    }

    private static @NotNull Color blendColors(@NotNull Color c1, @NotNull Color c2, double ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    private static double normalize(double min, double max, double value) {
        return (value - min) / (max - min);
    }
}