package net.atcore.security;

import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AntiBot {

    private static double discrepancy = 0;
    private static final List<String> names = new ArrayList<>();
    private static final List<Long> timeDelta = new ArrayList<>();
    private static final int lastes = 3;
    private static final int tolerance = 1000*20;

    public static boolean checkBot(InetAddress ip, @NotNull String name){
        discrepancy /= lastes;// hasta que Net no arregle lo de la ip no implementar el sistema para la ip
        if (LoginManager.isNewPlayer(name)){
            discrepancy *= 1.2;
        }else {
            discrepancy *= 0.9;
        }
        long delta = System.currentTimeMillis();
        for (int i = 0; i < Math.min(lastes, names.size()); i++){
            if (Objects.equals(names.get(i), name)){
                return false;
            }
            discrepancy += calcularSimilitud(names.get(i), name);
            if (i >= 1){
                delta = timeDelta.get(i) - timeDelta.get(i - 1);
                discrepancy += (tolerance - delta)*0.001;
            }
        }
        Bukkit.getLogger().warning("nivel de discrepancia" +  discrepancy + " | " + ((tolerance - delta)*0.001) + " | " + delta);
        names.addFirst(name);
        timeDelta.addFirst(System.currentTimeMillis());
        if (timeDelta.size() > lastes){
            timeDelta.removeLast();
            names.removeLast();
        }
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
