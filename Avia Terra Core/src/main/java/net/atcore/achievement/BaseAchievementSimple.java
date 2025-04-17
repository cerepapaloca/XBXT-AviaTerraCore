package net.atcore.achievement;

import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.event.Event;

/**
 * Los logros serán tareas simples por ejemplo ve a una ubicación, consigue un item
 */
public abstract class BaseAchievementSimple<T extends Event>  extends BaseAchievementOneStep<T, Object> {
    public BaseAchievementSimple(Material material, Class<? extends BaseAchievement<? extends Event, ?>> parent, AdvancementType type) {
        super(material, parent, type);
    }
}
