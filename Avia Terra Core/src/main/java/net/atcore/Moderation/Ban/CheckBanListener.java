package net.atcore.Moderation.Ban;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class CheckBanListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(@NotNull PlayerLoginEvent event) {//No sé por qué cuando es la primeras vez que entras al servidor se dispara 4 veces seguidas
        Player player = event.getPlayer();
        String s = BanManager.checkBan(player, event.getAddress(), ContextBan.GLOBAL);
        if (s != null && !s.isEmpty()) {
            event.setKickMessage(s);
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String s = BanManager.checkBan(player, ContextBan.CHAT);
        if (s != null && !s.isEmpty()) {
            event.setCancelled(true);
            event.setMessage(s);
        }
    }

    private static final HashMap<UUID, Long> timePunishChat = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceNew = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceOld = new HashMap<>();
    private static final HashMap<UUID, Integer> timeDifferenceCount = new HashMap<>();
    private static String lastMessage = "";
    private static final ArrayList<Player> ChatBotTime = new ArrayList<>();

    @EventHandler
    public static void checkAutoBanChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        long currentTime = System.currentTimeMillis();
        if (Objects.equals(lastMessage, message)){
            ChatBotTime.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.PLUGIN, () -> {
                        if (ChatBotTime.size() > 1) {
                            for (Player player : ChatBotTime) {
                                assert player != null;
                                BanManager.banPlayer(player, "Por Bot",1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, "Servidor");
                            }
                            sendMessageConsole("Purga terminada", TypeMessages.SUCCESS);
                            ChatBotTime.clear();
                        }else{
                            ChatBotTime.clear();
                        }
                    });
                }
            }.runTaskLater(AviaTerraCore.PLUGIN, 2);
        }else {
            lastMessage = message;
        }

        if (timePunishChat.containsKey(player.getUniqueId())) {
            long lastTime = timePunishChat.get(player.getUniqueId());
            long DifferenceOld = timeDifferenceOld.getOrDefault(player.getUniqueId(), -1000L);
            long DifferenceNew = timeDifferenceNew.getOrDefault(player.getUniqueId(), 1000L);

            if ((DifferenceOld - DifferenceNew) < 50 && (DifferenceOld - DifferenceNew) > -50) {
                timeDifferenceCount.put(player.getUniqueId(), timeDifferenceCount.getOrDefault(player.getUniqueId(), 0) + 1);
                sendMessageConsole("Este jugador usa AutoBotChat: (" + timeDifferenceCount.get(player.getUniqueId()) + "/3) " + "Tiene una precision de "
                        + (DifferenceOld - DifferenceNew) + " ms", TypeMessages.WARNING);
                if (timeDifferenceCount.get(player.getUniqueId()) >= 3){
                    BanManager.banPlayer(player, "Por Bot",1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, "Servidor");
                    timeDifferenceCount.remove(player.getUniqueId());
                    timeDifferenceOld.remove(player.getUniqueId());
                    timeDifferenceNew.remove(player.getUniqueId());
                }
            }
            timeDifferenceNew.put(player.getUniqueId() ,currentTime - lastTime);
            timeDifferenceOld.put(player.getUniqueId(), DifferenceNew);
        }
        timePunishChat.put(player.getUniqueId(), currentTime);
    }
}
