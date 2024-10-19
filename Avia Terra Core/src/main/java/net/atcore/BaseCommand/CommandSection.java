package net.atcore.BaseCommand;

import lombok.Getter;
import net.atcore.BaseCommand.Commnads.*;
import net.atcore.Section;

import static net.atcore.Utils.RegisterManager.register;


public class CommandSection implements Section {

    @Getter private static CommandHandler commandHandler;

    @Override
    public void enable() {
        commandHandler = new CommandHandler();
        register(new CommandFreeze());
        register(new CommandPrueba());
        register(new CommandAviaTerra());
        register(new CommandUnban());
        register(new CommandBan());
        register(new CommandCheckBan());
        register(new CommandLogin());
        register(new CommandRegister());
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Commands";
    }
}
