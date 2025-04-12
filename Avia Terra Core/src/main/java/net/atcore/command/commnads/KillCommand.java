package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

public class KillCommand extends BaseCommand {

    public KillCommand() {
        super("kill",
                new ArgumentUse("kill"),
                CommandVisibility.PUBLIC,
                "Te matás"
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            try {
                player.damage(10000, DamageSource.builder(DamageType.PLAYER_ATTACK).withCausingEntity(player).build());
            }catch (Exception e){
                player.setHealth(0);
                MessagesManager.sendWaringException("Error al matar al jugador.", e);
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
