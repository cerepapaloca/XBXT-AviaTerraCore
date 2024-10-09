package net.atcore.moderation;

import lombok.Getter;
import net.atcore.Section;

import static net.atcore.Utils.RegisterManager.register;

public class ModerationSection implements Section {

    @Getter private static FreezeListener freezeListener;

    @Override
    public void enable() {
        register(freezeListener = new FreezeListener());
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
