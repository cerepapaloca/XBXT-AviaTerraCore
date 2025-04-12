package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.data.DataSection;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendFormatMessage;
import static net.atcore.messages.MessagesManager.sendMessage;

public class AviaTerraRemoveCommand extends BaseTabCommand {

    public AviaTerraRemoveCommand() {
        super("aviaTerraRemove", new ArgumentUse("AviaTerraRemove"), CommandVisibility.PRIVATE, "Borra cosas");
        addAlias("atr");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length >= 1) {
            AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                switch (args[0]) {
                    case "session" -> {
                        CommandUtils.executeForPlayer(sender, args[1], true, (name, player) -> {
                            LoginManager.getDataLogin(player).setSession(null);
                            //GlobalUtils.kickPlayer(dataTemporalPlayer.player(), "Vuelve a iniciar sesión");
                        });
                        sendMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_SUCCESSFUL);
                    }
                    case "register" -> {
                        CommandUtils.executeForPlayer(sender, args[1], false, (name, player) -> {
                            if (!DataBaseRegister.removeRegister(name, sender.getName())) {
                                sendFormatMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_ERROR, name);
                            }
                            LoginManager.removeDataLogin(name);
                        });
                        sendMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_SUCCESSFUL);
                    }
                    case "password" -> {
                        CommandUtils.executeForPlayer(sender, args[1], false, (name, player) -> {
                            if (!DataBaseRegister.updatePassword(name, null)) {
                                sendFormatMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_ERROR, name);
                            }
                            LoginData data = LoginManager.getDataLogin(name);
                            if (data != null) {
                                data.getRegister().setPasswordShaded(null);
                            }else {
                                sendFormatMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_ERROR, name);
                            }
                        });
                        sendMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_SUCCESSFUL);
                    }
                    case "apf" -> {
                        CommandUtils.executeForPlayer(sender, args[1], false, (name, player) -> {
                            UUID uuid;
                            if (player != null) {
                                uuid = player.getUniqueId();
                            }else {
                                uuid = GlobalUtils.getUUIDByName(name);
                            }
                            DataSection.getPlayersDataFiles().deleteConfigFile(uuid.toString());
                        });
                        sendMessage(sender, Message.COMMAND_AVIA_TERRA_REMOVE_SUCCESSFUL);
                    }
                }
            });
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        List<String> root = new ArrayList<>(List.of("session", "register", "password", "apf"));
        if (args.length == 1) {
            return CommandUtils.listTab(args[0], root);
        }
        return List.of();
    }
}
