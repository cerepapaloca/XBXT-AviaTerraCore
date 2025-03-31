package net.atcore.achievement;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class BaseAchievementContinuous<T extends Event> extends BaseAchievementSimple<T> {
    public BaseAchievementContinuous(Material material, String path, AdvancementType type) {
        super(material, path, type);
    }

    @Override
    protected void grantAdvanced(Player player, Object value) {
        AviaTerraPlayer.DataProgressContinuos progress =  AviaTerraPlayer.getPlayer(player).getProgressInteger(this);
        if (value instanceof Integer i) {
            progress.addValue(i);
            super.grantAdvanced(player, value);
        }else {
            throw new IllegalArgumentException("value must be an integer");
        }
    }

    public abstract int getMetaValue();
}
