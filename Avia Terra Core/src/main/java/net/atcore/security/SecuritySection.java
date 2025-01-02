package net.atcore.security;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.security.Login.SimulateOnlineMode;

public class SecuritySection implements Section {

    @Getter
    private static EncryptService encryptService;
    @Getter
    private static SimulateOnlineMode simulateOnlineMode;

    @Override
    public void enable() {
        encryptService = new EncryptService();
        simulateOnlineMode = new SimulateOnlineMode();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Seguridad";
    }
}
