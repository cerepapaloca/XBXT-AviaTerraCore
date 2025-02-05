package net.atcore.command;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class BaseCommand {

    protected final String name;
    protected final String permissions;
    protected final String description;
    protected final ArgumentUse usage;
    protected final boolean requiredConfirm;
    @Setter
    protected String messageConfirm = "Tiene que confirmar con <|<Click:suggest_command:/confirm>/confirm</click>|> para ejecutar este comandos";

    public BaseCommand(@NotNull String name,
                       @NotNull ArgumentUse usage,
                       @Nullable String description,
                       boolean requiredConfirm
    ) {
        this(name, usage, "", description, requiredConfirm);
    }

    public BaseCommand(@NotNull String name,
                       @NotNull ArgumentUse usage,
                       @NotNull String permissions,
                       @Nullable String description,
                       boolean requiredConfirm
    ) {
        this.name = name;
        this.usage = usage;
        this.requiredConfirm = requiredConfirm;
        this.permissions = permissions.equals("*") || permissions.equals("**") ? permissions : AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + name.toLowerCase() + "," + permissions;
        this.description = description == null || description.isEmpty() ? "&oSin Descripción" : description;
    }

    public abstract void execute(CommandSender sender, String[] args) throws Exception;

}
