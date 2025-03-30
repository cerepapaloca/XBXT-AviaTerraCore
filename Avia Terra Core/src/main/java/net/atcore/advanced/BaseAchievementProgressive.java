package net.atcore.advanced;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAchievementProgressive<T extends Event> extends BaseAchievement<T> {
    public BaseAchievementProgressive(Material material, String title, String description, String path, AdvancementType type) {
        super(material, title, description, path, type);

    }

    @Override
    protected void onProgressAdvanced(Player player, Object data) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getProgress(this).getProgress();
        progress.update(advancements.value().requirements());
        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        if (remaining.isEmpty()) return;
        progress.grantProgress(remaining.getFirst());
    }


}
