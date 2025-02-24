package net.atcore.moderation.ban;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.Config.*;
import static net.atcore.messages.MessagesManager.logConsole;
import static org.bukkit.Material.*;

public class CheckAutoBan {

    private static final HashMap<UUID, Long> timePunishChat = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceNew = new HashMap<>();
    private static final HashMap<UUID, Long> timeDifferenceOld = new HashMap<>();
    private static final HashMap<UUID, Integer> timeDifferenceCount = new HashMap<>();
    private static String lastMessage = "";
    private static final ArrayList<Player> ChatBotTime = new ArrayList<>();

    public static void checkAutoBanChat(Player player, String message) {
        if (!isAutoBan()) return;
        try {
            long currentTime = System.currentTimeMillis();
            if (Objects.equals(lastMessage, message)){
                ChatBotTime.add(player);
                new BukkitRunnable() {
                    public void run() {
                        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
                            if (ChatBotTime.size() > 1) {
                                for (Player player : ChatBotTime) {
                                    assert player != null;
                                    ModerationSection.getBanManager().banPlayer(player, Message.BAN_AUTO_BAN_BOT.getMessage(player)
                                            ,1000 * 60 * 60 * 24 * 5L, ContextBan.CHAT, Message.BAN_AUTHOR_AUTO_BAN.getMessage(player));
                                }
                                logConsole("Purga de bots terminada", TypeMessages.SUCCESS, CategoryMessages.BAN);
                            }
                            ChatBotTime.clear();
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
                    logConsole("Este jugador usa AutoBotChat: <|(" + timeDifferenceCount.get(player.getUniqueId()) + "/10)|> " + "Tiene una precision de <|"
                            + (DifferenceOld - DifferenceNew) + " ms|>", TypeMessages.WARNING, CategoryMessages.MODERATION);
                    if (timeDifferenceCount.get(player.getUniqueId()) >= 10){
                        ModerationSection.getBanManager().banPlayer(player, Message.BAN_AUTO_BAN_SPAM.getMessage(player), 1000 * 60 * 60 * 24 * 2L, ContextBan.CHAT,
                                Message.BAN_AUTHOR_AUTO_BAN.getMessage(player));
                        timeDifferenceCount.remove(player.getUniqueId());
                        timeDifferenceOld.remove(player.getUniqueId());
                        timeDifferenceNew.remove(player.getUniqueId());
                    }
                }
                timeDifferenceNew.put(player.getUniqueId() ,currentTime - lastTime);
                timeDifferenceOld.put(player.getUniqueId(), DifferenceNew);
            }
            timePunishChat.put(player.getUniqueId(), currentTime);
        }catch (Exception e) {
            MessagesManager.logConsole(String.format(Message.BAN_ERROR.getMessage(player), player.getName(), Message.BAN_AUTO_BAN_SPAM.getMessage(player)), TypeMessages.ERROR);
            throw new RuntimeException(e);
        }
    }

    public static void startTimeRemove() {
        new BukkitRunnable() {
            public void run() {
                timeDifferenceCount.replaceAll((uuid, count) -> count > 0 ? count - 1 : 0);
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 20, 20*60);
    }

    public static void checkDupe(@NotNull Player player, Inventory inventory) {
        new BukkitRunnable(){
            public void run() {
                Map<String, Integer> itemCounts = new HashMap<>();
                for (ItemStack item : inventory) {
                    if (item == null) continue;
                    if (item.getItemMeta() == null) continue;
                    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
                    if (dataContainer.has(GlobalUtils.KEY_ANTI_DUPE, PersistentDataType.STRING)) {
                        String uniqueId = dataContainer.get(GlobalUtils.KEY_ANTI_DUPE, PersistentDataType.STRING);
                        if (uniqueId != null){
                            if (uniqueId.equals("?")) {
                                ItemMeta meta = item.getItemMeta();
                                meta.getPersistentDataContainer().set(GlobalUtils.KEY_ANTI_DUPE, PersistentDataType.STRING, UUID.randomUUID().toString());
                                item.setItemMeta(meta);
                            }else{
                                itemCounts.put(uniqueId, itemCounts.getOrDefault(uniqueId, 0) + 1);
                            }
                        }
                    }
                }

                for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                    if (entry.getValue() > 1) {
                        inventory.clear();
                        if (!isAutoBan()) return;
                        AviaTerraCore.enqueueTaskAsynchronously( () -> {
                            try {
                                ModerationSection.getBanManager().banPlayer(player, Message.BAN_AUTO_BAN_DUPE.getMessage(player),
                                        1000 * 60 * 60 * 24 * 5L, ContextBan.GLOBAL, Message.BAN_AUTHOR_AUTO_BAN.getMessage(player));
                            }catch (Exception e) {
                                MessagesManager.logConsole(String.format(Message.BAN_ERROR.getMessage(player), player.getName(),  Message.BAN_AUTO_BAN_SPAM.getMessage(player)), TypeMessages.ERROR);
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
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
                MessagesManager.logConsole(String.format("Se eliminó <|%s|> de <|%s|>", item.getType().toString().toLowerCase(), player.getName()), TypeMessages.WARNING, CategoryMessages.PLAY);
                item.setAmount(0);
                b = true;
            }
        }
        if (!isAutoBan()) return;
        if (!b)return;

        AviaTerraCore.enqueueTaskAsynchronously( () -> {
            try {
                ModerationSection.getBanManager().banPlayer(player, Message.BAN_AUTO_BAN_ILEGAL_ITEMS.getMessage(player),
                        1000 * 60 * 60 * 24 * 10L, ContextBan.GLOBAL, Message.BAN_AUTHOR_AUTO_BAN.getMessage(player));
            }catch (Exception e) {
                MessagesManager.logConsole(String.format(Message.BAN_ERROR.getMessage(player), player.getName(),  Message.BAN_AUTO_BAN_SPAM.getMessage(player)), TypeMessages.ERROR);
                throw new RuntimeException(e);
            }
        });
        player.getInventory().clear();
    }
}
