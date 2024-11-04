package net.atcore;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.moderation.ChatModeration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

@Getter
@Setter
public class AviaTerraPlayer {

    public AviaTerraPlayer(Player player) {
        this.player = player;


        new BukkitRunnable() {
            boolean b = false;
            public void run() {
                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());

                if (bossBar == null){
                    createBossBar();
                }
                if (tabPlayer != null) {

                    if (MAX_MANA < mana ) {
                        if (b) return;
                        b = true;
                        bossBar.setTitle("Cantidad de Mana: " + MAX_MANA + "%");
                        bossBar.setProgress((float) ((mana/MAX_MANA)*100));
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 3);
                    }else{
                        b = false;
                        bossBar.setProgress((float) ((mana/MAX_MANA)*100));
                        bossBar.setTitle("Cantidad de Mana: " + mana + "%");
                        mana += 2;
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 4000);
                    }


                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 5, 5);
    }

    private BossBar bossBar;
    public static final double MAX_MANA = 100;
    private final Player player;
    private float pointChat = ChatModeration.MAX_PUNTOS;
    private int sanctionsChat = 1;//por circunstancias matemáticas tiene que ser 1
    //private final DataLogin dataLogin;
    private double mana;
    private boolean isFreeze = false;

    private void createBossBar(){
        bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player.getName(), 1f, BarColor.GREEN, BarStyle.NOTCHED_10);
        bossBar.setTitle("Cantidad de Mana: " + mana);
        bossBar.setProgress((float) ((mana/MAX_MANA)*100));
    }

}
