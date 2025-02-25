package net.atcore.security;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.security.Login.SimulateOnlineMode;
import net.atcore.security.check.checker.AntiIlegalBlock;
import net.atcore.security.check.checker.AntiIlegalItem;
import net.atcore.security.check.checker.AntiOP;
import net.atcore.security.check.checker.RoofNether;

public class SecuritySection implements Section {

    @Getter
    private static EncryptService encryptService;
    @Getter
    private static SimulateOnlineMode simulateOnlineMode;

    @Override
    public void enable() {
        encryptService = new EncryptService();
        simulateOnlineMode = new SimulateOnlineMode();
        new AntiIlegalItem();
        new RoofNether();
        new AntiOP();
        new AntiIlegalBlock();
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
