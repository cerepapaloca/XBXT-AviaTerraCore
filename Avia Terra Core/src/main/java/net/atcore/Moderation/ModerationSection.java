package net.atcore.Moderation;

import lombok.Getter;
import net.atcore.Moderation.Ban.CheckAutoBan;
import net.atcore.Moderation.Ban.ManagerBan;
import net.atcore.Section;

public class ModerationSection implements Section {

    @Getter private static ManagerBan banManager;

    @Override
    public void enable() {
        banManager = new ManagerBan();
        ChatModeration.tickEvent();
        CheckAutoBan.startTimeRemove();
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
