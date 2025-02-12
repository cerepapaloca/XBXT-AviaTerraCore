package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PassiveRestartCommand extends BaseCommand {

    private BukkitTask task;

    public PassiveRestartCommand() {
        super("passiveRestart",
                new ArgumentUse("passiveRestart").addArgOptional().addArg("cancel"),
                CommandVisibility.PRIVATE,
                "reinicia el servidor cuando no hay nadié conectado"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("cancel")) {
                task.cancel();
                MessagesManager.sendMessage(sender, Message.COMMAND_PASSIVE_RESTART_CANCEL);
            }
        }else {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (Bukkit.getOnlinePlayers().isEmpty()) {
                        MessagesManager.sendMessage(sender, Message.COMMAND_PASSIVE_RESTART_START);
                        Bukkit.shutdown();
                        cancel();
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 20*10, 20*5);
            MessagesManager.sendMessage(sender, Message.COMMAND_PASSIVE_RESTART_INIT);
        }
    }
}
