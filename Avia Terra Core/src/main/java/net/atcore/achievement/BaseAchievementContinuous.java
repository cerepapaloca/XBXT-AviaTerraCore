package net.atcore.achievement;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Los logros tendrán como requisito un valor alto por ejemplo distancia recorrida, tiempo jugador.
 */

public abstract class BaseAchievementContinuous<T extends Event> extends BaseAchievementSimple<T> {
    public BaseAchievementContinuous(Material material, Class<? extends BaseAchievement<? extends Event>> parent, AdvancementType type) {
        super(material, parent, type);
    }

    @Override
    protected void grantAdvanced(Player player, Object value) {
        AviaTerraPlayer.DataProgressContinuos progress =  AviaTerraPlayer.getPlayer(player).getProgressInteger(this);
        if (value instanceof Double i) {
            progress.addValue(i);
            super.grantAdvanced(player, value);
        }else {
            throw new IllegalArgumentException("value must be an integer");
        }
    }

    public abstract int getMetaValue();
}
