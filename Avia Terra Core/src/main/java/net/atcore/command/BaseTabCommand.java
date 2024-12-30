package net.atcore.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BaseTabCommand extends BaseCommand {

    public BaseTabCommand(String name, UseArgs usage, String permission, String description) {
        super(name, usage, permission, description);
    }

    public BaseTabCommand(String name, UseArgs usage, String description) {
        super(name, usage, description);
    }

    public abstract List<String> onTab(CommandSender sender, String[] args);
}
