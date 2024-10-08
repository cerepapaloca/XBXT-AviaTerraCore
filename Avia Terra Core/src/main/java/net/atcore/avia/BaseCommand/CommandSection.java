package net.atcore.avia.BaseCommand;

import lombok.Getter;
import net.atcore.avia.BaseCommand.Commnads.CommandPrueba;
import net.atcore.avia.Section;
import net.atcore.avia.Utils.RegisterManager;


public class CommandSection implements Section {

    @Getter private static CommandHandler commandHandler;

    @Override
    public void enable() {
        commandHandler = new CommandHandler();
        RegisterManager.register(new CommandPrueba());
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
