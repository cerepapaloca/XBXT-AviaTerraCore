package net.atcore.moderation.ban;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Objects;

public enum ContextBan {
    CHAT() {
        @Override
        public boolean onContext(Player player, Event event) {
            if (event instanceof AsyncPlayerChatEvent e) {
                IsBan isBan = BanManager.checkBan(player, Objects.requireNonNull(player.getAddress()).getAddress(), ContextBan.CHAT);
                if (isBan.equals(IsBan.YES)) {
                    e.setCancelled(true);
                    DataBan dataBan = BanManager.getDataBan(player.getName()).get(ContextBan.CHAT);
                    MessagesManager.sendMessage(player, " \n" + BanManager.formadMessageBan(dataBan), MessagesType.KICK);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onBan(Player player, DataBan dataBan){
            MessagesManager.sendMessage(player, BanManager.formadMessageBan(dataBan), MessagesType.KICK);
        }
    },
    GLOBAL() {
        @Override
        public boolean onContext(Player player, Event event) {
            if (event instanceof PlayerLoginEvent e) {
                IsBan isBan = BanManager.checkBan(player, e.getAddress(), ContextBan.GLOBAL);
                if (isBan.equals(IsBan.YES)) {
                    DataBan dataBan = BanManager.getDataBan(player.getName()).get(ContextBan.GLOBAL);
                    GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(dataBan));
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onBan(Player player, DataBan dataBan) {
            GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(dataBan));
        }
    };

    public abstract boolean onContext(Player player, Event event);

    public abstract void onBan(Player player, DataBan dataBan);
}
