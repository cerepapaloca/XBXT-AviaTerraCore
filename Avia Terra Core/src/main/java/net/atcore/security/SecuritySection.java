package net.atcore.security;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.security.Login.SimulateOnlineMode;

public class SecuritySection implements Section {

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
        return "Seguridad";
    }
}
