package net.atcore.moderation;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.messages.MessagesManager.sendMessageConsole;


public class ChatModeration {

    public static final float MAX_PUNTOS = 200;

    public static boolean antiSpam(Player bukkitPlayer, String message) {
        AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(bukkitPlayer);
        float puntos = aviaTerraPlayer.getPointChat();
        if (puntos < 0) {//si su puntos son negativos se lo hace saber
            aviaTerraPlayer.setPointChat(puntos);
            sendMessageConsole( bukkitPlayer.getName() + " » &7" + message + "&c [ELIMINADO: Spam]", TypeMessages.INFO, CategoryMessages.MODERATION);
            float second = (puntos)/(20F * Config.getLevelModerationChat());//formula para calcular el tiempo que le fata para volver a escribir
            second *= 10F;
            int secondInt = Math.round(Math.abs(second));
            sendMessage(bukkitPlayer, "mensaje eliminado por Spam espera <|" + secondInt * 0.1F + "|> segundos", TypeMessages.ERROR);
            return true;
        }else {
            puntos = puntos - ((message.length()*3F) + 70F);//por cada letra más puntos le resta y por cada mensaje resta 15
            if (puntos < 0) {//si los puntos llega a estar en negativo lo multiplica por la cantidad de veces que fue penalizado en 2 minutos
                aviaTerraPlayer.setSanctionsChat(aviaTerraPlayer.getSanctionsChat() + 1); //aumenta la penalización
                puntos *= aviaTerraPlayer.getSanctionsChat();
            }
            aviaTerraPlayer.setPointChat(puntos);
            return false;
        }
    }

    public static void tickEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(player);
                    float f = aviaTerraPlayer.getPointChat();
                    aviaTerraPlayer.setPointChat(f > MAX_PUNTOS ? MAX_PUNTOS : f + Config.getLevelModerationChat());
                });
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 1, 1);//TODO optimiza esto por favor

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(player);
                    int i = aviaTerraPlayer.getSanctionsChat();
                    aviaTerraPlayer.setSanctionsChat(i > 1 ? i - 1 : 1);
                });
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 0, 20*60*2);
    }

    private static final Set<String> badWords = Set.of(".xyz", ".net", ".org", ".com", ".lat", ".gg", ".tv", "http", "nigga", "nazi", "卐");

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
