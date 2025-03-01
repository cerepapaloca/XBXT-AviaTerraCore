package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.Message;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class PruebaCommand extends BaseCommand {

    public PruebaCommand() {
        super("prueba",
                new ArgumentUse("prueba")
                        .addArg("Alfa", "Beta", "Gamma")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addNote("Note")
                        .addTime(true),
                CommandVisibility.PRIVATE,
                "es un comando de prueba"
        );
        addAlias("test");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, Message.TEST_MESSAGE);
        LoginManager.checkLogin((Player) sender, false,true);
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
