package net.atcore.achievement;

import net.atcore.Section;
import net.atcore.achievement.achievements.*;

import static net.atcore.achievement.BaseAchievement.createNode;

public class AchievementSection implements Section {
    @Override
    public void enable() {
        /*new TestSimple();
        new TestContinuous();
        new TestProgressive();
        new TestStep();*/
        new RootAchievement();
        new Explorate5Achievement();
        new Explorate4Achievement();
        new Explorate3Achievement();
        new Explorate2Achievement();
        new Explorate1Achievement();
        new InventoryAchievement();
        new AllShulkerBoxAchievement();
        new ShulkerFullAchievement();
        new DeathByWhitherAchievement();
        createNode();
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
