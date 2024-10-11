package net.atcore.Moderation;

import lombok.Getter;
import net.atcore.Section;

import static net.atcore.Utils.RegisterManager.register;

public class ModerationSection implements Section {

    @Getter private static FreezeListener freezeListener;
    @Getter private static BanManager banManager;

    @Override
    public void enable() {
        register(freezeListener = new FreezeListener());
        register(new CheckBanListener());
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
