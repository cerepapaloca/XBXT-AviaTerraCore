package net.atcore.listener;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static net.atcore.AviaTerraCore.jda;
import static net.atcore.utils.RegisterManager.*;

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
}
