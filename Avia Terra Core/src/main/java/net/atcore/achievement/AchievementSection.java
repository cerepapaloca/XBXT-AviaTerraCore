package net.atcore.achievement;

import net.atcore.Section;
import net.atcore.achievement.achievements.ExplorateAchievement;
import net.atcore.achievement.achievements.InventoryAchievement;
import net.atcore.achievement.achievements.RootAchievement;

public class AchievementSection implements Section {
    @Override
    public void enable() {
        /*new TestSimple();
        new TestContinuous();
        new TestProgressive();
        new TestStep();*/
        new RootAchievement();
        new ExplorateAchievement();
        new InventoryAchievement();
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "Logros";
    }

    @Override
    public boolean isImportant() {
        return false;
    }

    @Override
    public void reload() {

    }
}
