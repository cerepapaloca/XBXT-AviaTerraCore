package net.atcore.advanced;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class BaseAchievementSimple<T extends Event>  extends BaseAchievement<T> {
    public BaseAchievementSimple(Material material, String title, String description, String path, AdvancementType type) {
        super(material, title, description, path, type);
    }

    @Override
    public int getMetaProgress() {
        return 1;
    }

    @Override
    protected void onProgressAdvanced(Player player, Object data) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getProgress(this).getProgress();
        progress.update(advancements.value().requirements());
        progress.grantProgress("1");
    }
}
