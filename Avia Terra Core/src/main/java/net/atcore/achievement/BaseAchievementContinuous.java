package net.atcore.achievement;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Los logros tendrán como requisito un valor alto por ejemplo distancia recorrida, tiempo jugador.
 */

public abstract class BaseAchievementContinuous<T extends Event> extends BaseAchievementOneStep<T, Double> {
    public BaseAchievementContinuous(Material material, Class<? extends BaseAchievement<? extends Event, ?>> parent, AdvancementType type) {
        super(material, parent, type);
    }

    @Override
    protected void grantAdvanced(Player player, Double value) {
        AviaTerraPlayer.DataProgressContinuos progress =  AviaTerraPlayer.getPlayer(player).getProgressContinuos(this);
        progress.addValue(value);
        super.grantAdvanced(player, value);
    }

    public abstract int getMetaValue();
}
