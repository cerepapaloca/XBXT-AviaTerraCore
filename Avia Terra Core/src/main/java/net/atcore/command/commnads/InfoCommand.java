package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesType;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class InfoCommand extends BaseCommand {

    public InfoCommand() {
        super("info",
                new ArgumentUse("/info"),
                "Te logueas",
                false
        );
    }

    private static final int SPACE = 24;

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        User user = AviaTerraCore.getLp().getUserManager().getUser(sender.getName());
        String groupName;
        if (user != null) {
            groupName = user.getPrimaryGroup();
        }else {
            groupName = "? (usuario)";
        }
        sendMessage(sender,"|" + applySpace("Nombres") + "|" + applySpace("Localización") + "|" + applySpace("Rango") + "|" + applySpace("UUID"),
                MessagesType.INFO, CategoryMessages.PRIVATE, false);
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendMessage(sender,
                    "|" + applySpace(p.getName())
                            + "|" + applySpace(locationToString(p.getLocation()))
                            + "|" + applySpace(groupName)
                            + "|" + applySpace(p.getUniqueId().toString()),
                    MessagesType.INFO, CategoryMessages.PRIVATE, false);        }
        sendMessage(sender," ", MessagesType.INFO, CategoryMessages.PRIVATE, false);
        sendMessage(sender,String.format("jugadores <|%s/%s|>", Bukkit.getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers()), MessagesType.INFO, CategoryMessages.PRIVATE, false);
        sendMessage(sender,String.format("TPS %s", Bukkit.getServer().getServerTickManager().getTickRate()), MessagesType.INFO, CategoryMessages.PRIVATE, false);

    }

    private String applySpace(String s){
        return  s + " ".repeat(Math.max(0, SPACE - s.length()));
    }

    private String locationToString(Location loc){
        return "[" + loc.getWorld().getName() + "] " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}
