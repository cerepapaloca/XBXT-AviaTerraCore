package net.atcore.command;

import lombok.Getter;
import net.atcore.command.Commnads.*;
import net.atcore.Section;

import static net.atcore.utils.RegisterManager.register;


public class CommandSection implements Section {

    @Getter private static CommandHandler commandHandler;

    @Override
    public void enable() {
        commandHandler = new CommandHandler();
        register(new FreezeCommand());
        register(new PruebaCommand());
        register(new AviaTerraCommand());
        register(new UnbanCommand());
        register(new BanCommand());
        register(new CheckBanCommand());
        register(new LoginCommand());
        register(new RegisterCommand());
        register(new AddRangeCommand());
        register(new KickCommand());
        register(new RemoveRegisterCommand());
        register(new ChangePasswordCommand());
        register(new RemoveSessionCommand());
        register(new WeaponCommand());
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
