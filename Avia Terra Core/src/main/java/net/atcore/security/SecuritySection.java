package net.atcore.security;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.data.DataSection;
import net.atcore.security.Login.SimulateOnlineMode;
import net.atcore.security.check.BaseChecker;
import net.atcore.security.check.checker.*;

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
        new AntiArmament();
        new FixItems();
        new RangePurge();
        new AntiDupe();
        init();
    }

    private void init() {
        for (BaseChecker<?> check : BaseChecker.REGISTERED_CHECKS) {
            check.enabled = DataSection.getConfigFile().getConfig().getBoolean("checker." + check.getClass().getSimpleName(), check.enabled);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public String getName() {
        return "Seguridad";
    }

    @Override
    public boolean isImportant() {
        return true;
    }
}
