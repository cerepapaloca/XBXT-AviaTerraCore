package net.atcore.achievement;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Los logros tendrá requisito un valor bajo por ejemplo: haz algo X veces.
 * Este logro se puede ver progreso que va consiguiendo
 */

public abstract class BaseAchievementProgressive<T extends Event> extends BaseAchievement<T, Integer> {
    public BaseAchievementProgressive(Material material, Class<? extends BaseAchievement<? extends Event, ?>> parent, AdvancementType type) {
        super(material, parent, type);

    }

    @Override
    protected void onProgressAdvanced(AdvancementProgress progress, Integer data) {
        progress.update(advancements.value().requirements());
        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        for (int i = 0; i < (data == null ? 1 : data); i++) {
            if (remaining.isEmpty()) return;
            progress.grantProgress(remaining.removeFirst());
        }
    }
}
