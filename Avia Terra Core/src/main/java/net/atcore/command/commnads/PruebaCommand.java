package net.atcore.command.commnads;

import net.atcore.command.*;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.PREFIX;
import static net.atcore.messages.MessagesManager.sendMessage;

public class PruebaCommand extends BaseCommand {

    public PruebaCommand() {
        super("prueba",
                new ArgumentUse("prueba")
                        .addArg("Alfa", "Beta", "Gamma")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addNote("Note")
                        .addTime(true),
                "!*",
                "es un comando de prueba"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, "Hola Mundo!", MessagesType.SUCCESS);
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

    }
}
