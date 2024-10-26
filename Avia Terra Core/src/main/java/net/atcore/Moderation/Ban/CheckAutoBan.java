package net.atcore.Moderation.Ban;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.ModerationSection;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.net.SocketException;
import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;
import static org.bukkit.Material.*;

public class CheckAutoBan {

    private static final HashMap<UUID, Long> timePunishChat = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceNew = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceOld = new HashMap<>();
    private static final HashMap<UUID, Integer> timeDifferenceCount = new HashMap<>();
    private static String lastMessage = "";
    private static final ArrayList<Player> ChatBotTime = new ArrayList<>();

    public static void checkAutoBanChat(Player player, String message) throws SocketException {
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
                                ModerationSection.getBanManager().banPlayer(player, "Uso de bots",1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, "Servidor");
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

            if ((DifferenceOld - DifferenceNew) < 30 && (DifferenceOld - DifferenceNew) > -30) {
                timeDifferenceCount.put(player.getUniqueId(), timeDifferenceCount.getOrDefault(player.getUniqueId(), 0) + 1);
                sendMessageConsole("Este jugador usa AutoBotChat: <|(" + timeDifferenceCount.get(player.getUniqueId()) + "/5)|> " + "Tiene una precision de <|"
                        + (DifferenceOld - DifferenceNew) + " ms|>", TypeMessages.WARNING, CategoryMessages.MODERATION);
                if (timeDifferenceCount.get(player.getUniqueId()) >= 5){
                    ModerationSection.getBanManager().banPlayer(player, "Por enviar mensajes automatizado en el chat",1000 * 60 * 60 * 24 * 2L, ContextBan.CHAT, "Servidor");
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

    public static void startTimeRemove() {
        new BukkitRunnable() {
            @Override
            public void run() {
                timeDifferenceCount.replaceAll((uuid, count) -> count > 0 ? count - 1 : 0);
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 20, 20*60);
    }

    public static void checkDupe(@NotNull Player player, Inventory inventory) {
        Map<String, Integer> itemCounts = new HashMap<>();

        for (ItemStack item : inventory) {
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
                inventory.clear();
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        ModerationSection.getBanManager().banPlayer(player, "Por estar dupeando",
                                1000 * 60 * 60 * 24 * 5L, ContextBan.GLOBAL, "Servidor"));

            }
        }
    }

    private static final Set<Material> ILEGAL_ITEMS = Set.of(BEDROCK, END_PORTAL_FRAME, COMMAND_BLOCK, BARRIER,
            STRUCTURE_VOID, STRUCTURE_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER, REINFORCED_DEEPSLATE);

    public static void checkAntiIlegalItems(Player player ,Inventory inventory) {
        if (!Config.isCheckAntiIllegalItems())return;
        if (player.isOp()) return;

        boolean b = false;
        for (ItemStack item: inventory.getContents()){
            if (item == null) continue;
            if (ILEGAL_ITEMS.contains(item.getType())){
                item.setType(Material.AIR);
                b = true;
            }
        }
        if (!b)return;
        ModerationSection.getBanManager().banPlayer(player, "Obtención de item ilegal",1000 * 60 * 60 * 24 * 10L, ContextBan.GLOBAL, "Servidor");
        player.getInventory().clear();
    }
}
