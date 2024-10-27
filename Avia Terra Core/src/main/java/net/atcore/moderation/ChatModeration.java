package net.atcore.moderation;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.messages.MessagesManager.sendMessageConsole;


public class ChatModeration {

    private static final float MAX_PUNTOS = 200;

    private static final HashMap<UUID, Float> puntosDeChat = new HashMap<>();
    private static final HashMap<UUID, Integer> counts = new HashMap<>();

    public static boolean antiSpam(Player player, String message) {
        UUID uuid = player.getUniqueId();
        float puntos = puntosDeChat.getOrDefault(uuid, MAX_PUNTOS);
        if (puntos < 0) {//si su puntos son negativos se lo hace saber
            puntosDeChat.put(uuid, puntos);
            sendMessageConsole( player.getName() + " » &7" + message + "&c [ELIMINADO: Spam]", TypeMessages.INFO, CategoryMessages.MODERATION);
            float second = (puntos)/(20F * Config.getLevelModerationChat());//formula para calcular el tiempo que le fata para volver a escribir
            second = second * 10F;
            int secondInt = Math.round(Math.abs(second));
            sendMessage(player, "mensaje eliminado por Spam espera <|" + secondInt * 0.1F + "|> segundos", TypeMessages.ERROR);
            return true;
        }else {
            puntos = puntos - ((message.length()*3F) + 70F);//por cada letra más puntos le resta y por cada mensaje resta 15
            if (puntos < 0) {//si los puntos llega a estar en negativo lo multiplica por la cantidad de veces que fue penalizado en 2 minutos
                counts.put(uuid ,counts.getOrDefault(uuid, 1) + 1);//aumenta la penalización
                puntos = puntos * counts.get(uuid);
            }
            puntosDeChat.put(uuid, puntos);
            return false;
        }
    }

    public static void tickEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                puntosDeChat.replaceAll((uuid, punto) -> punto > MAX_PUNTOS ? MAX_PUNTOS : punto + Config.getLevelModerationChat());
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                counts.replaceAll((uuid, count) -> count > 1 ? count - 1 : 1);
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 0, 20*60*2);
    }

    private static final Set<String> badWords = Set.of(".xyz", ".net", ".org", ".com", ".lat", ".gg", ".tv", "http", "nigga", "nazi");

    public static boolean antiBanWord(Player player, String message){
        message = message.toLowerCase();
        for (String word : badWords){
            if (message.contains(word) ||
                    message.contains(word.replace("i", "1")) ||
                    message.contains(word.replace("l", "1")) ||
                    message.contains(word.replace("a", "4")) ||
                    message.contains(word.replace("o", "0")) ||
                    message.contains(word.replace("n", "ñ")) ||
                    message.contains(word.replace("ñ", "n"))
            ){
                sendMessageConsole( player.getName() + " » &7" + message + "&c [ELIMINADO: Malas Palabras]", TypeMessages.INFO, CategoryMessages.MODERATION);
                sendMessage(player, "mensaje eliminado por qué contiene palabras inadecuadas", TypeMessages.ERROR);
                return true;
            }


        }
        return false;
    }
}
