package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class WeaponPlayer extends AbstractAviaTerraPlayer {

    WeaponPlayer(AviaTerraPlayer player) {
        super(player);
        new BukkitRunnable() {
            boolean b = false;
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
                if (bukkitPlayer == null) return;
                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(bukkitPlayer.getUniqueId());

                if (bossBar == null){
                    createBossBar();
                }
                if (tabPlayer != null) {

                    if (MAX_AMMO < ammo) {
                        if (b) return;
                        b = true;
                        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + MAX_AMMO + "%"));
                        bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 3);
                    }else{
                        b = false;
                        bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
                        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + ammo + "%"));
                        ammo += 2;
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 4000);
                    }


                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 5, 5);
    }

    public static final double MAX_AMMO = 100;

    private BossBar bossBar;
    private double ammo = MAX_AMMO;

    private void createBossBar(){
        Player player = Bukkit.getPlayer(aviaTerraPlayer.getPlayer().getUniqueId());
        if (player != null) {
            bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player, 1f, BarColor.BLUE, BarStyle.NOTCHED_10);
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + ammo + "%"));
            bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
        }
    }
}
