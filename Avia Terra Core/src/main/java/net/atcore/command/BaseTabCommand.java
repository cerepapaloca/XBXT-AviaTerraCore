package net.atcore.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BaseTabCommand extends BaseCommand {

    public BaseTabCommand(String name, String usage, String permission, String description) {
        super(name, usage, permission, description, ModeAutoTab.NONE);
    }

    public BaseTabCommand(String name, String usage, String description) {
        super(name, usage, description, ModeAutoTab.NONE);
    }

    public abstract List<String> onTab(CommandSender sender, String[] args);
}
