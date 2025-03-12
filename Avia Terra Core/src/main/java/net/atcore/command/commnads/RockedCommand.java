package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RockedCommand extends BaseCommand {

    public RockedCommand() {
        super("rocked", new ArgumentUse("rocked"), CommandVisibility.PUBLIC, "?");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            new BukkitRunnable() {

                private int count = 3;

                @Override
                public void run() {
                    if (count == 0) {
                        cancel();
                        player.setVelocity(player.getVelocity().setY(10));
                        player.getWorld().playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
                        BukkitTask task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 2);
                            }
                        }.runTaskTimer(AviaTerraCore.getInstance(), 2, 2);

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                task.cancel();
                                player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                                player.damage(100, player);
                                player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 5, 0.5, 0.5, 0.5);
                                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 15, 0.5, 0.5, 0.5);
                            }
                        }.runTaskLater(AviaTerraCore.getInstance(), 20L*2);
                    }else {
                        player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 0.667420f);
                        MessagesManager.sendTitle(player, Message.COMMAND_ROCKED_TITLE.getMessage(player), String.valueOf(count), 10,10, 0, TypeMessages.INFO);
                        count--;
                        if (count == 0) {
                            player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1.334840F);
                        }
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 0L, 20L);
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
