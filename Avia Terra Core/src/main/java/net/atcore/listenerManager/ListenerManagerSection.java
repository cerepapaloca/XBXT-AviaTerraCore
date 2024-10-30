package net.atcore.listenerManager;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.utils.RegisterManager;

public class ListenerManagerSection implements Section {

    @Getter
    private static ChatListener chatListener;

    @Override
    public void enable() {
        RegisterManager.register(chatListener = new ChatListener());
        RegisterManager.register(new InventoryListener());
        RegisterManager.register(new JoinAndExitListener());
        RegisterManager.register(new PlayerListener());
        new PacketListener();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Listener";
    }
}
