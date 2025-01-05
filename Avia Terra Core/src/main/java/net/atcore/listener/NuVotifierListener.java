package net.atcore.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.atcore.data.DataSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class NuVotifierListener implements Listener {

    public static final HashSet<String> LIST_VOTE = new HashSet<>();

    @EventHandler(priority= EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        String name = event.getVote().getUsername();
        Player player = Bukkit.getPlayer(name);
        LIST_VOTE.add(name);
        if (player != null && player.isOnline()) {
            GlobalUtils.addRangeVote(player);
        }else {
            DataSection.getCacheVoteFile().saveData();
        }

    }
}
