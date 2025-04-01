package net.atcore.achievement;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Los logros tendrá requisito un valor bajo por ejemplo: haz algo X veces.
 * Este logro se puede ver progreso que va consiguiendo
 */

public abstract class BaseAchievementProgressive<T extends Event> extends BaseAchievement<T> {
    public BaseAchievementProgressive(Material material, Class<? extends BaseAchievement<? extends Event>> parent, AdvancementType type) {
        super(material, parent, type);

    }

    @Override
    protected void onProgressAdvanced(AdvancementProgress progress, Object data) {
        progress.update(advancements.value().requirements());
        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        if (remaining.isEmpty()) return;
        progress.grantProgress(remaining.getFirst());
    }


}
