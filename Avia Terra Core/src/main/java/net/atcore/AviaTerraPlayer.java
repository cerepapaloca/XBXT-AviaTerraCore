package net.atcore;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.moderation.ChatModeration;
import net.atcore.inventory.InventorySection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

                    if (MAX_AMMO < ammo) {
                        if (b) return;
                        b = true;
                        bossBar.setTitle("Cantidad De Munición: " + MAX_AMMO + "%");
                        bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 3);
                    }else{
                        b = false;
                        bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
                        bossBar.setTitle("Cantidad De Munición: " + ammo + "%");
                        ammo += 2;
                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 4000);
                    }


                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 5, 5);
    }

    private static HashMap<UUID, AviaTerraPlayer> players = new HashMap<>();

    private BossBar bossBar;
    public static final double MAX_AMMO = 100;
    private final Player player;
    private float pointChat = ChatModeration.MAX_PUNTOS;
    private int sanctionsChat = 1;//por circunstancias matemáticas tiene que ser 1
    //private final DataLogin dataLogin;
    private double ammo = MAX_AMMO;
    private boolean isFreeze = false;
    private InventorySection inventorySection = null;
    private List<Player> manipulatorInventoryPlayer = new ArrayList<>();
    private Player manipulatedInventoryPlayer = null;

    private void createBossBar(){
        bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player.getName(), 1f, BarColor.GREEN, BarStyle.NOTCHED_10);
        bossBar.setTitle("Cantidad De Munición: " + ammo);
        bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
    }

    public static AviaTerraPlayer getPlayer(UUID uuid){
        return players.get(uuid);
    }

    public static AviaTerraPlayer getPlayer(Player player){
        return players.get(player.getUniqueId());
    }

    public static void addPlayer(Player player){
        if (!players.containsKey(player.getUniqueId())){
            players.put(player.getUniqueId(), new AviaTerraPlayer(player));
        }
    }

}
