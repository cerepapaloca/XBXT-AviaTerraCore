package net.atcore.moderation.Ban.action;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class Chat implements RunActionBan {
    @Override
    public boolean onContext(Player player, Event event) {
        if (event instanceof AsyncPlayerChatEvent e) {
            IsBan isBan = BanManager.checkBan(player, Objects.requireNonNull(player.getAddress()).getAddress(), ContextBan.CHAT);
            if (isBan.equals(IsBan.YES)) {
                e.setCancelled(true);
                DataBan dataBan = BanManager.getDataBan(player.getName()).get(ContextBan.CHAT);
                MessagesManager.sendMessage(player, " \n" + BanManager.formadMessageBan(dataBan), TypeMessages.KICK);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBan(Player player, DataBan dataBan) {
        MessagesManager.sendMessage(player, BanManager.formadMessageBan(dataBan), TypeMessages.KICK);
    }
}
