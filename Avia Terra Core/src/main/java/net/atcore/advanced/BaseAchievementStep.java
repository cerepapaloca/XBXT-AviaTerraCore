package net.atcore.advanced;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BaseAchievementStep<T extends Event> extends BaseAchievement<T> {
    public BaseAchievementStep(Material material, String title, String description, String path, AdvancementType type) {
        super(material, title, description, path, type);
    }

    @Override
    public int getMetaProgress() {
        return listSteps().size();
    }

    @Override
    protected @NotNull BaseAchievement.BaseProperties createProperties() {
        Map<String, Criterion<?>> criteria = new HashMap<>();
        for (String s : listSteps()) {
            criteria.put(s, new Criterion<>(
                    new ImpossibleTrigger(),
                    new ImpossibleTrigger.TriggerInstance()
            ));
        }

        List<List<String>> requirements = new ArrayList<>();
        for (String s : listSteps()) {
            requirements.add(List.of(s));
        }
        return new BaseProperties(criteria, requirements);
    }

    @Override
    public void onProgressAdvanced(Player player, Object data) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getProgress(this).getProgress();
        progress.update(advancements.value().requirements());
        if (data instanceof String s) {
            progress.grantProgress(s);
        }else {
            throw new IllegalArgumentException("step must be a string");
        }
    }

    protected abstract List<String> listSteps();
}
