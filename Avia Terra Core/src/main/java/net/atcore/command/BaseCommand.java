package net.atcore.command;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class BaseCommand {

    private final String name;
    private final String usage;
    private final String[] permissions;
    private final String description;
    private final Boolean isHide;

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull Boolean isHide, @Nullable String description) {
        this(name, usage, (AviaTerraCore.getInstance().getName() + ".command." + name).toLowerCase(), isHide, description);
    }

    public BaseCommand(@NotNull String name, @NotNull  String usage, @NotNull  String[] permissions, @NotNull  Boolean isHide, @Nullable String description) {
        this.name = name;
        this.usage = usage;
        this.permissions = permissions;
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
    }

    public BaseCommand(@NotNull String name, @NotNull  String usage, @Nullable  String permissions, @NotNull  Boolean isHide, @Nullable String description) {
        this.name = name;
        this.usage = usage;
        this.permissions = new String[]{permissions};
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
    }

    public abstract void execute(CommandSender sender, String[] args);

}
