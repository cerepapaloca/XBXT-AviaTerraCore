package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import org.bukkit.command.CommandSender;

public class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login",
                "/login <contraseña>",
                "aviaterra.command.login",
                false,
                "Te logueas"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
