package net.atcore.Service;

import lombok.Getter;
import net.atcore.Section;

public class ServiceSection implements Section {

    @Getter
    private static Encrypt encrypt;
    @Getter
    private static SimulateOnlineMode simulateOnlineMode;

    @Override
    public void enable() {
        encrypt = new Encrypt();
        simulateOnlineMode = new SimulateOnlineMode();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Servicios";
    }
}
