package net.atcore.BaseCommand;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public abstract class BaseCommand {

    private final String name;
    private final String usage;
    private final String[] permissions;
    private final String description;
    private final Boolean isHide;
    private final String[] subCommands;

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull  String[] permission, @NotNull Boolean isHide, @Nullable String description) {
        this(name, usage, permission, isHide, description ,null);
    }

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull String permission, @NotNull Boolean isHide, @Nullable String description) {
        this(name, usage, permission,false , description ,null);
    }

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull  String[] permissions, @NotNull  Boolean isHide, @Nullable String description, @Nullable String[] subCommands) {
        this.name = name;
        this.usage = usage;
        this.permissions = permissions;
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
        this.subCommands = subCommands;
    }

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull  String permissions, @NotNull  Boolean isHide, @Nullable String description, @Nullable String[] subCommands) {
        this.name = name;;
        this.usage = usage;
        this.permissions = new String[]{permissions};
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
        this.subCommands = subCommands;
    }

    public Optional<Player> getSenderAsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return Optional.of((Player) sender);
        } else return Optional.empty();
    }

    public abstract void execute(CommandSender sender, String[] args);

}
