package net.atcore.Service;

import lombok.Getter;
import net.atcore.Section;

public class ServiceSection implements Section {

    @Getter
    private static Encrypt encrypt;

    @Override
    public void enable() {
        encrypt = new Encrypt();
        new SimulateOnlineMode();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "";
    }
}
