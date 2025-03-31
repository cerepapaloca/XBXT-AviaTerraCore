package net.atcore.achievement;

import net.atcore.Section;
import net.atcore.achievement.achievements.TestContinuous;
import net.atcore.achievement.achievements.TestProgressive;
import net.atcore.achievement.achievements.TestSimple;
import net.atcore.achievement.achievements.TestStep;

public class AchievementSection implements Section {
    @Override
    public void enable() {
        new TestSimple();
        new TestContinuous();
        new TestProgressive();
        new TestStep();
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
