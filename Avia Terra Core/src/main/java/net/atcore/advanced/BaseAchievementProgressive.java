package net.atcore.advanced;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;

public abstract class BaseAchievementProgressive<T extends Event> extends BaseAchievement<T> {
    public BaseAchievementProgressive(Material material, String title, String description, String path) {
        super(material, title, description, path);

    }

    @Override
    public List<List<String>> createRequirements() {
        List<List<String>> requirements = new ArrayList<>();
        //List<String> requirement =new ArrayList<>();
        for (int i = 1; i <= getMaxProgress(); i++) {

            requirements.add(List.of("step_" + i));
            //requirement.add("step_" + i);
        }
        //requirements.add(requirement);
        return requirements;
    }

    @Override
    public Map<String, Criterion<?>> createCriteria(){
        Map<String, Criterion<?>> criteria = new HashMap<>();
        for (int i = 1; i <= getMaxProgress(); i++) {
            criteria.put("step_" + i, new Criterion<>(
                    new ImpossibleTrigger(),
                    new ImpossibleTrigger.TriggerInstance()
            ));
        }
        return criteria;
    }

    @Override
    public void onProgressAdvanced(Player player) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getAchievementProgress(this);
        progress.update(advancements.value().requirements());
        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        if (remaining.isEmpty()) return;
        progress.grantProgress(remaining.getFirst());
        sendAchievement(player, progress);
    }

    protected abstract int getMaxProgress();
}
