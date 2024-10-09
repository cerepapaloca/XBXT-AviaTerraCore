package net.atcore.BaseCommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BaseTabCommand extends BaseCommand {

    public BaseTabCommand(String name, String usage, String permission, boolean isHide, String description) {
        super(name, usage, permission, isHide, description);
    }

    public BaseTabCommand(String name, String usage, String permission, String description) {
        super(name, usage, permission, description);
    }

    public BaseTabCommand(String name, String usage, String permission, boolean isHide, String description, String[] subCommands) {
        super(name, usage, permission, isHide, description, subCommands);
    }

    public abstract List<String> onTab(CommandSender sender, String[] args);
}
