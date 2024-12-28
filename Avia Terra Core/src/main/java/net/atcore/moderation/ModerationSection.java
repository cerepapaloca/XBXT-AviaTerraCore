package net.atcore.moderation;

import lombok.Getter;
import net.atcore.moderation.ban.CheckAutoBan;
import net.atcore.moderation.ban.BanManager;
import net.atcore.Section;

public class ModerationSection implements Section {

    @Getter private static BanManager banManager;

    @Override
    public void enable() {
        banManager = new BanManager();
        ChatModeration.tickEvent();
        CheckAutoBan.startTimeRemove();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Moderación";
    }
}
