package net.atcore.security;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.security.Login.SimulateOnlineMode;
import net.atcore.security.checker.AntiIlegal;
import net.atcore.security.checker.RoofNether;

public class SecuritySection implements Section {

    @Getter
    private static EncryptService encryptService;
    @Getter
    private static SimulateOnlineMode simulateOnlineMode;

    @Override
    public void enable() {
        encryptService = new EncryptService();
        simulateOnlineMode = new SimulateOnlineMode();
        new RoofNether();
        new AntiIlegal();
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
