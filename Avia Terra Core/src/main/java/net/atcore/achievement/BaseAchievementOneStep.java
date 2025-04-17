package net.atcore.achievement;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAchievementOneStep<T extends Event, D> extends BaseAchievement<T, D> {
    public BaseAchievementOneStep(Material material, Class<? extends BaseAchievement<? extends Event, ?>> parent, AdvancementType advancementType) {
        super(material, parent, advancementType);
    }

    @Override
    public int getMetaProgress() {
        return 1;
    }

    @Override
    protected void onProgressAdvanced(AdvancementProgress progress, Object data) {
        progress.update(advancements.value().requirements());
        progress.grantProgress("complete");
    }

    @Override
    @Contract(" -> new")
    protected @NotNull BaseAchievement.BaseProperties createProperties() {
        Map<String, Criterion<?>> criteria = new HashMap<>();
        criteria.put("complete", new Criterion<>(
                new ImpossibleTrigger(),
                new ImpossibleTrigger.TriggerInstance()
        ));

        List<List<String>> requirements = new ArrayList<>();
        requirements.add(List.of("complete"));
        return new BaseProperties(criteria, requirements);
    }
}
