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
                new SeeInventoryCommand(),
                new AviaTerraDebugCommand(),
                new LinkCommand(),
                new TellCommand(),
                new HomeCommand(),
                new TpaCommand(),
                new SayCommand(),
                new NameColorCommand(),
                new ForceLoginCommand(),
                new PremiumCommand(),
                new confirmCommand(),
                new PassiveRestartCommand(),
                new VoteCommand(),
                new DiscordCommand()
        );
    }

    @Override
    public void disable() {
        commandHandler.getCommands().clear();
        commandHandler = null;
        CommandManager.COMMANDS_AVIA_TERRA.clear();
        CommandManager.COMMANDS.clear();
    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Comandos";
    }
}
