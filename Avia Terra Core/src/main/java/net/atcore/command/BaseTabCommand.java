package net.atcore.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BaseTabCommand extends BaseCommand {

    public BaseTabCommand(String name, ArgumentUse usage, String permission, String description, boolean requiereConfirm) {
        super(name, usage, permission, description, requiereConfirm);
    }

    public BaseTabCommand(String name, ArgumentUse usage, String description, boolean requiredConfirm) {
        super(name, usage, description, requiredConfirm);
    }

    public abstract List<String> onTab(CommandSender sender, String[] args);
}
