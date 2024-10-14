package net.atcore.ListenerManager;

import net.atcore.Section;
import net.atcore.Utils.RegisterManager;

public class ListenerManagerSection implements Section {

    @Override
    public void enable() {
        RegisterManager.register(new ChatListener());
        RegisterManager.register(new InventoryListener());
        RegisterManager.register(new JoinAndExitListener());
        RegisterManager.register(new PlayerListener());
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Listener Manager";
    }
}
