package net.atcore.command;

import lombok.Getter;
import net.atcore.command.commnads.*;
import net.atcore.Section;

import static net.atcore.utils.RegisterManager.register;


public class CommandSection implements Section {

    @Getter private static CommandHandler commandHandler;

    @Override
    public void enable() {
        commandHandler = new CommandHandler();
        register(new InfoCommand(),
                new FreezeCommand(),
                new PruebaCommand(),
                new AviaTerraCommand(),
                new UnbanCommand(),
                new BanCommand(),
                new CheckBanCommand(),
                new LoginCommand(),
                new RegisterCommand(),
                new AddRangeCommand(),
                new KickCommand(),
                new RemoveRegisterCommand(),
                new ChangePasswordCommand(),
                new RemoveSessionCommand(),
                new WeaponCommand(),
                new seeInventoryCommand(),
                new AviaTerraPruebasCommand(),
                new LinkCommand(),
                new TellCommand()
        );
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Comandos";
    }
}
