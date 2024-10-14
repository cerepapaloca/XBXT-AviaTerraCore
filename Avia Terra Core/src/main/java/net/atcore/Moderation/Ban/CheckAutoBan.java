package net.atcore.Moderation.Ban;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.ModerationSection;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class CheckAutoBan implements Listener {

    private static final HashMap<UUID, Long> timePunishChat = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceNew = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceOld = new HashMap<>();
    private static final HashMap<UUID, Integer> timeDifferenceCount = new HashMap<>();
    private static String lastMessage = "";
    private static final ArrayList<Player> ChatBotTime = new ArrayList<>();

    public static void checkAutoBanChat(Player player, String message) {
        long currentTime = System.currentTimeMillis();
        if (Objects.equals(lastMessage, message)){
            ChatBotTime.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
                        if (ChatBotTime.size() > 1) {
                            for (Player player : ChatBotTime) {
                                assert player != null;
                                ModerationSection.getBanManager().banPlayer(player, "Por Bot",1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, "Servidor");
                            }
                            sendMessageConsole("Purga terminada", TypeMessages.SUCCESS);
                            ChatBotTime.clear();
                        }else{
                            ChatBotTime.clear();
                        }
                    });
                }
            }.runTaskLater(AviaTerraCore.getInstance(), 2);
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
                    ModerationSection.getBanManager().banPlayer(player, "Por Bot",1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, "Servidor");
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

    public static void checkDupe(@NotNull Player player) {
        Map<String, Integer> itemCounts = new HashMap<>();

        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (item.getItemMeta() == null) continue;

            PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
            if (dataContainer.has(GlobalUtils.KEY_ANTI_DUPE, PersistentDataType.STRING)) {
                String uniqueId = dataContainer.get(GlobalUtils.KEY_ANTI_DUPE, PersistentDataType.STRING);

                itemCounts.put(uniqueId, itemCounts.getOrDefault(uniqueId, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            if (entry.getValue() > 1) {
                player.getInventory().clear();
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> ModerationSection.getBanManager().banPlayer(player, "Por estar dupeando",1000 * 60 * 60 * 24 * 5L, ContextBan.GLOBAL, "Servidor"));

            }
        }
    }
}
