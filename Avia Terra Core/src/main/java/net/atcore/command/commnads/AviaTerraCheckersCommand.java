package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.data.DataSection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.check.BaseChecker;
import net.atcore.utils.AviaTerraScheduler;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AviaTerraCheckersCommand extends BaseTabCommand {

    public AviaTerraCheckersCommand() {
        super("checkers",
                new ArgumentUse("Checkers"),
                CommandVisibility.PRIVATE,
                null
        );
        addAlias("atc");
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        switch (args.length) {
            case 1 -> {
                for (BaseChecker<?> check : BaseChecker.REGISTERED_CHECKS){
                    if (check.getClass().getSimpleName().equalsIgnoreCase(args[0])) {
                        MessagesManager.sendString(sender, String.format("El %s está %s", check.getClass().getSimpleName(), CommandUtils.booleanToString(check.enabled, sender)), TypeMessages.INFO);
                        return;
                    }
                }
            }
            case 2 -> {
                for (BaseChecker<?> check : BaseChecker.REGISTERED_CHECKS){
                    if (check.getClass().getSimpleName().equalsIgnoreCase(args[0])) {
                        check.enabled = CommandUtils.isTrueOrFalse(args[1]);
                        MessagesManager.sendString(sender, String.format("El %s se cambio a %s", check.getClass().getSimpleName(), CommandUtils.booleanToString(check.enabled, sender)), TypeMessages.INFO);
                        AviaTerraScheduler.enqueueTaskAsynchronously(() -> DataSection.getConfigFile().saveData());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return BaseChecker.REGISTERED_CHECKS.stream().map(checker -> checker.getClass().getSimpleName().toLowerCase()).toList();
            }
            case 2 -> {
                return CommandUtils.listTab(args[1], "true", "false");
            }
        }
        return null;
    }
}
