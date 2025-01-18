package net.atcore.moderation;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.Config;
import net.atcore.aviaterraplayer.ModerationPlayer;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesType;
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
        ModerationPlayer mp = aviaTerraPlayer.getModerationPlayer();
        double puntos = Math.min(mp.getPointChat() + ((System.currentTimeMillis() - mp.getLastChat())/1000D)*Config.getLevelModerationChat(), MAX_PUNTOS);

        mp.setLastChat(System.currentTimeMillis());
        if (puntos < 0) {//si su puntos son negativos se lo hace saber
            mp.setPointChat(puntos);
            sendMessageConsole(bukkitPlayer.getName() + " » &7" + message + "&c [ELIMINADO: Spam]", MessagesType.INFO, CategoryMessages.MODERATION);
            double second = (puntos)/(Config.getLevelModerationChat());//formula para calcular el tiempo que le fata para volver a escribir
            long secondLong = Math.round(Math.abs(second));
            sendMessage(bukkitPlayer, "mensaje eliminado por Spam espera <|" + secondLong + "|> segundos", MessagesType.ERROR);
            return true;
        }else {
            puntos = puntos - ((message.length()*3F) + 70F);//por cada letra más puntos le resta y por cada mensaje resta 15
            if (puntos < 0) {//si los puntos llega a estar en negativo lo multiplica por la cantidad de veces que fue penalizado en 2 minutos
                mp.setSanctionsChat(mp.getSanctionsChat() + 1); //aumenta la penalización
                puntos *= mp.getSanctionsChat();
            }
            mp.setPointChat(puntos);
            return false;
        }
    }

    public static void tickEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ModerationPlayer moderationPlayer = AviaTerraPlayer.getPlayer(player).getModerationPlayer();
                    int i = moderationPlayer.getSanctionsChat();
                    moderationPlayer.setSanctionsChat(i > 1 ? i - 1 : 1);
                });
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 0, 20*60*2);
    }

    private static final Set<String> badWords = Set.of(".xyz", ".net", ".org", ".com", ".lat", ".gg", ".tv", "http", "nigga", "nazi", "卐");

    public static boolean antiBanWord(Player player, String message){
        message = message.toLowerCase();
        if (Config.getLevelModerationChat() == 0) return false;
        for (String word : badWords){
            if (message.contains(word) ||
                    message.contains(word.replace("i", "1")) ||
                    message.contains(word.replace("l", "1")) ||
                    message.contains(word.replace("a", "4")) ||
                    message.contains(word.replace("o", "0")) ||
                    message.contains(word.replace("n", "ñ")) ||
                    message.contains(word.replace("ñ", "n"))
            ){
                sendMessageConsole( player.getName() + " » &7" + message + "&c [ELIMINADO: Malas Palabras]", MessagesType.INFO, CategoryMessages.MODERATION);
                sendMessage(player, "mensaje eliminado por qué contiene palabras inadecuadas", MessagesType.ERROR);
                return true;
            }
        }
        return false;
    }
}
