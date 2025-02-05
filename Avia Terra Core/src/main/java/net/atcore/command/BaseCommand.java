package net.atcore.command;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * El commando base de Avia Terra, al crear una instancia de este se tiene que usar
 * {@link net.atcore.utils.RegisterManager#register(BaseCommand...) register} para que
 * se pueda usar
 */

@Getter
public abstract class BaseCommand extends Command {

    protected final String aviaTerraPermissions;
    protected final String canonicalPermission;
    protected final ArgumentUse aviaTerraUsage;
    protected final boolean requiredConfirm;
    protected final boolean hasBukkitPermission;
    protected String messageConfirm = "Tiene que confirmar con <|<Click:suggest_command:/confirm>/confirm</click>|> para ejecutar este comandos";

    /**
     * La lista de alias reales de los comandos es decir no incluyen los alias de {@link CommandAliase},
     * No confundir con {@link #getAliases()} este regresa todos los alias
     */

    private final List<String> aviaTerraAliases = new ArrayList<>();

    protected void addAlias(String... alias) {
        aviaTerraAliases.addAll(Arrays.stream(alias).map(String::toLowerCase).toList());
        var list = new ArrayList<>(aviaTerraAliases);
        list.addAll(getAliases());
        setAliases(list);
    }

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
        super(name);
        this.aviaTerraUsage = usage;
        this.requiredConfirm = requiredConfirm;
        this.canonicalPermission = AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + getName().toLowerCase();
        this.hasBukkitPermission = (!permissions.equals("*") && !permissions.equals("**"));
        this.aviaTerraPermissions = hasBukkitPermission ? canonicalPermission + "," + permissions : permissions;

        setDescription(description == null || description.isEmpty() ? "&oSin Descripción" : description);
        setUsage(usage.toString());
        if (hasBukkitPermission) {
            setPermission(canonicalPermission);
            Bukkit.getLogger().info("Using " + canonicalPermission);
        }
        if (this instanceof CommandAliase commandAliase) {
            List<String> aliases = new ArrayList<>(aviaTerraAliases);
            aliases.addAll(commandAliase.getCommandsAliases().stream().map(String::toLowerCase).toList());
            Bukkit.getLogger().info("Using Aliases: " + aliases);
            setAliases(aliases);
        }
    }

    public abstract void execute(CommandSender sender, String[] args) throws Exception;

    /**
     * No se en que situación se usará este execute
     */

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        MessagesManager.logConsole("Se ejecuto el método execute de BaseCommand", TypeMessages.WARNING);
        CommandSection.getCommandHandler().onCommand(sender, this, commandLabel, args);
        return true;
    }

}
