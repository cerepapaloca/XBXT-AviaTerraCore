package net.atcore.BaseCommand;

import lombok.Getter;
import net.atcore.BaseCommand.Commnads.CommandAviaTerra;
import net.atcore.BaseCommand.Commnads.CommandFreeze;
import net.atcore.BaseCommand.Commnads.CommandPrueba;
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
