package net.atcore.moderation.ban;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public enum ContextBan {
    CHAT() {
        private static final Set<String> SET_COMMANDS_SOCIAL = Set.of("tell", "w", "whipper", "r");
        @Override
        public DataBan onContext(Player player, Event event) {
            if (event instanceof AsyncChatEvent e) {
                return ban(player, e);
            }
            if (event instanceof PlayerCommandPreprocessEvent e) {
                String command = e.getMessage().split(" ")[0].substring(1).toLowerCase();
                if (SET_COMMANDS_SOCIAL.contains(command)) {
                    return ban(player, e);
                }
            }
            return null;
        }

        private DataBan ban(Player player, Cancellable e) {
            IsBan isBan = BanManager.checkBan(player, Objects.requireNonNull(player.getAddress()).getAddress(), ContextBan.CHAT);
            if (isBan.equals(IsBan.YES)) {
                e.setCancelled(true);
                DataBan dataBan = BanManager.getDataBan(player.getName()).get(ContextBan.CHAT);
                MessagesManager.sendString(player, BanManager.formadMessageBan(dataBan), TypeMessages.KICK);
                return dataBan;
            }
            return null;
        }

        @Override
        public void onBan(Player player, DataBan dataBan){
            MessagesManager.sendString(player, BanManager.formadMessageBan(dataBan), TypeMessages.KICK);
        }
    },
    GLOBAL() {
        @Override
        public DataBan onContext(Player player, Event event) {
            if (event instanceof PlayerLoginEvent e) {
                IsBan isBan = BanManager.checkBan(player, e.getAddress(), ContextBan.GLOBAL);
                if (isBan.equals(IsBan.YES)) {
                    DataBan dataBan = BanManager.getDataBan(player.getName()).get(ContextBan.GLOBAL);
                    GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(dataBan));
                    return dataBan;
                }
            }
            return null;
        }

        @Override
        public void onBan(Player player, DataBan dataBan) {
            GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(dataBan));
        }
    };

    public abstract @Nullable DataBan onContext(Player player, Event event);

    public abstract void onBan(Player player, DataBan dataBan);
}
