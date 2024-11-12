package net.atcore.command;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Test.TypeTest;
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
    private final ModeAutoTab modeAutoTab;

    public BaseCommand(@NotNull String name,
                       @NotNull  String usage,
                       @NotNull Boolean isHide,
                       @Nullable String description,
                       @NotNull ModeAutoTab modeAutoTab) {
        this(name, usage, (AviaTerraCore.getInstance().getName() + ".command." + name).toLowerCase(), isHide, description, modeAutoTab);
    }

    public BaseCommand(@NotNull String name,
                       @NotNull  String usage,
                       @NotNull  String[] permissions,
                       @NotNull  Boolean isHide,
                       @Nullable String description,
                       @NotNull ModeAutoTab modeAutoTab) {
        this.name = name;
        this.usage = usage;
        this.permissions = permissions;
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
        this.modeAutoTab = modeAutoTab;
    }

    public BaseCommand(@NotNull String name,
                       @NotNull  String usage,
                       @Nullable  String permissions,
                       @NotNull  Boolean isHide,
                       @Nullable String description,
                       @NotNull ModeAutoTab modeAutoTab) {
        this.name = name;
        this.usage = usage;
        this.permissions = new String[]{permissions};
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
        this.isHide = isHide;
        this.modeAutoTab = modeAutoTab;
    }

    public abstract void execute(CommandSender sender, String[] args) throws Exception;

}
