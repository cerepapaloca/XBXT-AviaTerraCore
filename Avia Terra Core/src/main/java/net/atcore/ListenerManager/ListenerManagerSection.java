package net.atcore.ListenerManager;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Utils.GlobalUtils;
import net.atcore.Utils.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        return "Listener Manager";
    }
}
