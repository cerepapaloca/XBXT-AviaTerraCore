package net.atcore.achievement;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Los logros tendrán una lista de tareas por realizar, por ejemplo consigue unos items en específico
 */
public abstract class BaseAchievementStep<T extends Event> extends BaseAchievement<T, String> {
    public BaseAchievementStep(Material material, Class<? extends BaseAchievement<? extends Event, ?>> parent, AdvancementType type) {
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
    public void onProgressAdvanced(AdvancementProgress progress, String data) {
        progress.update(advancements.value().requirements());
        progress.grantProgress(data);
    }

    protected abstract List<String> listSteps();
}
