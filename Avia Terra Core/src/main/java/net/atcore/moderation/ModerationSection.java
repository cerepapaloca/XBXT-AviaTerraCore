package net.atcore.moderation;

import lombok.Getter;
import net.atcore.moderation.ban.BanManager;
import net.atcore.Section;

public class ModerationSection implements Section {

    @Override
    public void enable() {
        ChatModeration.tickEvent();
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

    @Override
    public boolean isImportant() {
        return true;
    }
}
