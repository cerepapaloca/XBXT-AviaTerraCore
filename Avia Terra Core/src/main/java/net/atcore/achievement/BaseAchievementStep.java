package net.atcore.achievement;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.*;
/**
 * Los logros tendrán una lista de tareas por realizar, por ejemplo consigue unos items en específico
 */
public abstract class BaseAchievementStep<T extends Event> extends BaseAchievement<T> {
    public BaseAchievementStep(Material material, Class<? extends BaseAchievement<? extends Event>> parent, AdvancementType type) {
        super(material, parent, type);
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
    public void onProgressAdvanced(AdvancementProgress progress, Object data) {
        progress.update(advancements.value().requirements());
        if (data instanceof String s) {
            progress.grantProgress(s);
        }else {
            throw new IllegalArgumentException("step must be a string");
        }
    }

    protected abstract List<String> listSteps();
}
