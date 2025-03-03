package net.atcore.listener;

import lombok.Getter;
import net.atcore.Section;

import static net.atcore.utils.RegisterManager.register;

public class ListenerSection implements Section {

    @Getter
    private static ChatListener chatListener;

    @Override
    public void enable() {
        chatListener = new ChatListener();
        register(chatListener,
                new InventoryListener(),
                new JoinAndQuitListener(),
                new PlayerListener(),
                new NuVotifierListener(),
                new DeathListener()
        );
        PacketListenerManager.registerEvents();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Listener";
    }

    @Override
    public boolean isImportant() {
        return true;
    }
}
