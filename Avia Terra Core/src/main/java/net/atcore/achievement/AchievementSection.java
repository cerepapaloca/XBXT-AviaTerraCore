package net.atcore.achievement;

import net.atcore.Section;
import net.atcore.achievement.achievements.*;

public class AchievementSection implements Section {
    @Override
    public void enable() {
        /*new TestSimple();
        new TestContinuous();
        new TestProgressive();
        new TestStep();*/
        new RootAchievement();
        new TravelsHighway4Achievement();
        new TravelsHighway3Achievement();
        new TravelsHighway2Achievement();
        new TravelsHighway1Achievement();
        new FindHighwayAchievement();
        new GetShulkerBoxAchievement();
        new AllShulkerBoxAchievement();
        new FullShulkerBoxAchievement();
        new DeathByWhitherAchievement();
        new GetEndCrystalAchievement();
        new RenameWitherAchievement();
        new FullChestFullShulkerBoxAchievement();
        new GetEnderChestAchievement();
        new KillPlayerAchievement();
        new EscapesUsedChorusFruit();
        BaseAchievement.createNode();
        PlayerInventoryChangeEvent.start();
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
    // consigue un ender chest
}
