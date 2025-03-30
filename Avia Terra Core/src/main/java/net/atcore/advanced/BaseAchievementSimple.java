package net.atcore.advanced;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAchievementSimple<T extends Event>  extends BaseAchievement<T> {
    public BaseAchievementSimple(Material material, String title, String description, String path) {
        super(material, title, description, path);
    }

    @Override
    public List<List<String>> createRequirements() {
        List<List<String>> requirements = new ArrayList<>();
        requirements.add(List.of("impossible"));
        return requirements;
    }

    @Override
    public Map<String, Criterion<?>> createCriteria(){
        Map<String, Criterion<?>> criteria = new HashMap<>();
        criteria.put("impossible", new Criterion<>(
                new ImpossibleTrigger(),
                new ImpossibleTrigger.TriggerInstance()
        ));
        return criteria;
    }

    @Override
    public void onProgressAdvanced(Player player) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getAchievementProgress(this);
        progress.update(advancements.value().requirements());
        progress.grantProgress("impossible");
    }
}
