package net.atcore.command.commnads;

import net.atcore.achievement.BaseAchievement;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.ModeTab;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import java.util.List;

public class AviaTerraAchievementCommand extends BaseTabCommand {

    public AviaTerraAchievementCommand() {
        super("aviaTerraAchievement", new ArgumentUse("aviaTerraAchievement"), CommandVisibility.PRIVATE, "añades o eliminas los logros de un jugador");
        addAlias("ata");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length > 2) {
            boolean all  = args[2].equals("*");
            ResourceLocation location = all ? null : ResourceLocation.parse(args[2]);
            BaseAchievement<? extends Event, ?> baseAchievement = all ? null : BaseAchievement.getAchievement(location);
            if (baseAchievement != null || all) {
                switch (args[1]) {
                    case "add" -> CommandUtils.executeForPlayer(sender, args[0], false, (name, player) -> {
                        if (baseAchievement != null) {
                            baseAchievement.grantAchievement(player, true);
                        }else {
                            BaseAchievement.getAllAchievement().forEach(achievement -> achievement.grantAchievement(player, true));
                        }
                        MessagesManager.sendString(sender, String.format("Se añadió el logro %s de %s", args[2], args[0]), TypeMessages.SUCCESS);
                    });
                    case "remove" -> CommandUtils.executeForPlayer(sender, args[0], false, (name, player) -> {
                        if (baseAchievement != null) {
                            baseAchievement.revokeAchievement(player, true);
                        }else {
                            BaseAchievement.getAllAchievement().forEach(achievement -> achievement.revokeAchievement(player, true));
                        }
                        MessagesManager.sendString(sender, String.format("Se elimino el logro %s de %s", args[2], args[0]), TypeMessages.SUCCESS);
                    });
                    default -> MessagesManager.sendString(sender, "Tiene que poner add o remove", TypeMessages.ERROR);
                }
            }else MessagesManager.sendString(sender, "El logro no existe", TypeMessages.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return CommandUtils.tabForPlayer(args[0]);
            }
            case 2 -> {
                return CommandUtils.listTab(args[1], List.of("add", "remove"));
            }
            case 3 -> {
                return CommandUtils.listTab(args[2], BaseAchievement.getAllAchievement().stream().map(achievement -> achievement.id.getNamespace() + ":" + achievement.id.getPath()).toList(), ModeTab.CONTAINS_IGNORE_CASE);
            }
            default -> {
                return List.of();
            }
        }
    }
}
