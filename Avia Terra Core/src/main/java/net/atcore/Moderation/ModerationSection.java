package net.atcore.Moderation;

import lombok.Getter;
import net.atcore.Moderation.Ban.BanManager;
import net.atcore.Section;

public class ModerationSection implements Section {

    @Getter private static BanManager banManager;

    @Override
    public void enable() {
        banManager = new BanManager();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Moderation";
    }
}
