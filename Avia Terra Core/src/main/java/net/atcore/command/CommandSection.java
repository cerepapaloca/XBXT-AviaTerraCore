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
                new HomeCommandAliase(),
                new TpaCommand(),
                new SayCommand(),
                new NameColorCommand(),
                new ForceLoginCommand(),
                new PremiumCommand(),
                new ConfirmCommand(),
                new LogOutCommand(),
                new PassiveRestartCommand(),
                new VoteCommand(),
                new DiscordCommand(),
                new HelpCommand()
        );
    }

    @Override
    public void disable() {
        CommandHandler.AVIA_TERRA_COMMANDS.clear();
        commandHandler = null;
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
