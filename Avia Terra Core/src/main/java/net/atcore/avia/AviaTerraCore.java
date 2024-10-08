package net.atcore.avia;

import net.atcore.avia.BaseCommand.CommandSection;
import net.atcore.avia.Messages.TypeMessages;
import net.atcore.avia.Utils.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static net.atcore.avia.Messages.MessagesManager.sendMessage;
import static net.atcore.avia.Messages.MessagesManager.sendMessageConsole;

public final class AviaTerraCore extends JavaPlugin {

    public static AviaTerraCore plugin;
    public static long timeCurrent;

    @Override
    public void onLoad(){
        plugin = this;
    }

    @Override
    public void onEnable() {
        timeCurrent = System.currentTimeMillis();
        sendMessageConsole("Avia Terra Iniciando...", TypeMessages.INFO, false);
        RegisterManager.register(new CommandSection());
        sendMessageConsole("Avia Terra Iniciado. " + "&6" + (System.currentTimeMillis() - timeCurrent) + "ms", TypeMessages.SUCCESS, false);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
    }
}
