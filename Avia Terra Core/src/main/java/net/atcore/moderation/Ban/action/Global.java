package net.atcore.moderation.Ban.action;

import net.atcore.moderation.Ban.*;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLoginEvent;

public class Global implements RunActionBan {
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
}
