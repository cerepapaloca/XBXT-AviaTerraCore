package net.atcore.security;

import net.atcore.Config;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public final class AntiBot {

    private static double discrepancy = 0;
    private static final List<String> NAMES = new ArrayList<>();
    private static final List<Long> LIST_DELTA = new ArrayList<>();
    private static long lastTime = System.currentTimeMillis();
    private static String lastName = "";
    private static final int TOLERANCE = 50;
    private static final long laste = 3;

    public static boolean checkBot(InetAddress ip, @NotNull String name){
        if (!Config.isAntiBot()) return false;
        if (lastName.equals(name)) return false;
        lastName = name;
        //Bukkit.getLogger().warning("A | " +  discrepancy);
        double oldDiscrepancy = discrepancy;
        long newDelta = (System.currentTimeMillis() - lastTime);
        lastTime = System.currentTimeMillis();
        LIST_DELTA.addLast(newDelta);
        NAMES.addLast(name);

        double result = 0;
        for (int i = 0; i < Math.min(laste, LIST_DELTA.size()); i++){
            if (LIST_DELTA.size() > 1)
                if (i > 0) {
                    double result1 = (TOLERANCE - (Math.abs(LIST_DELTA.get(i - 1) - LIST_DELTA.get(i)))) * 0.05;
                    result1 = Math.max(result1, -0.5);
                    //Bukkit.getLogger().warning("BA | " + result1 + " | " + LIST_DELTA.get(i - 1) + " = " + LIST_DELTA.get(i));
                    result += result1;
                    result += calcularSimilitud(NAMES.get(i - 1), name) - 0.2;
                    //Bukkit.getLogger().warning(NAMES + " | " + name  + " = " + NAMES.get(i - 1));
                    //Bukkit.getLogger().warning("BB | " + result + " | " + (calcularSimilitud(NAMES.get(i - 1), name) - 0.2)*0.8);
                    result = Math.min(result, 1.5*(laste - 1));
                    result = Math.max(result, -0.8*(laste - 1));
                    result += (1 - Math.abs(NAMES.get(i).length() - name.length())) * 0.3;
                    //Bukkit.getLogger().warning("BC | " + result + " | " + (2 - Math.abs(NAMES.get(i - 1).length() - name.length())) * 0.2);
                    result = Math.min(result, 1.5*(laste - 1));
                    result = Math.max(result, -0.8*(laste - 1));

                }
        }
        discrepancy += (result/(laste - 1));
        discrepancy = Math.min(discrepancy, 1.5);
        discrepancy = Math.max(discrepancy, -0.5);
        /*Bukkit.getLogger().warning("C | " + discrepancy + " | " + (result/(laste - 1)));
        Bukkit.getLogger().warning("D | " + LIST_DELTA);*/
        if (NAMES.size() >= laste){
            NAMES.removeFirst();
            LIST_DELTA.removeFirst();

        }


        /*if (discrepancy > 1){
            Bukkit.getLogger().warning("**BLOCK**");
        }else {
            Bukkit.getLogger().warning("**PASS**");
        }*/
        MessagesManager.sendMessageConsole(String.format("AntiBot <|%s|> -> <|%s|>", Math.round(oldDiscrepancy*100)+"%", Math.round(discrepancy*100))+"%", MessagesType.INFO);
        return discrepancy > 1;
    }

    public static double calcularSimilitud(String texto1, String texto2) {
        int distancia = levenshteinAlgorithm(texto1, texto2);
        int longitudMaxima = Math.max(texto1.length(), texto2.length());
        return (1 - (double) distancia / longitudMaxima);
    }

    public static int levenshteinAlgorithm(String texto1, String texto2) {
        int[][] matriz = new int[texto1.length() + 1][texto2.length() + 1];

        // inicializar la matriz
        for (int i = 0; i <= texto1.length(); i++) {
            for (int j = 0; j <= texto2.length(); j++) {
                if (i == 0) {
                    matriz[i][j] = j;
                } else if (j == 0) {
                    matriz[i][j] = i;
                } else {
                    int costo = (texto1.charAt(i - 1) == texto2.charAt(j - 1)) ? 0 : 1;
                    matriz[i][j] = Math.min(Math.min(matriz[i - 1][j] + 1, matriz[i][j - 1] + 1),
                            matriz[i - 1][j - 1] + costo);
                }
            }
        }
        return matriz[texto1.length()][texto2.length()];
    }
}
