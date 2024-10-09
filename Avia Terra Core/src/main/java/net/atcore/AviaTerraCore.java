package net.atcore;

import net.atcore.BaseCommand.CommandSection;
import net.atcore.Messages.TypeMessages;
import net.atcore.Utils.RegisterManager;
import net.atcore.moderation.ModerationSection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

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
        RegisterManager.register(new ModerationSection());
        sendMessageConsole("Avia Terra Iniciado. " + "&6" + (System.currentTimeMillis() - timeCurrent) + "ms", TypeMessages.SUCCESS, false);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
    }
}
