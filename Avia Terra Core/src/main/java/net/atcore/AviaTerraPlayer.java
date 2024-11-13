package net.atcore;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ChatModeration;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AviaTerraPlayer {

    public AviaTerraPlayer(Player player) {
        this.uuid = player.getUniqueId();
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

    private static HashMap<UUID, AviaTerraPlayer> players = new HashMap<>();

    private BossBar bossBar;
    public static final double MAX_AMMO = 100;
    private final UUID uuid;
    private float pointChat = ChatModeration.MAX_PUNTOS;
    private int sanctionsChat = 1;//por circunstancias matemáticas tiene que ser 1
    private double ammo = MAX_AMMO;
    private boolean isFreeze = false;
    private InventorySection inventorySection = null;
    private List<UUID> manipulatorInventoryPlayer = new ArrayList<>();
    private UUID manipulatedInventoryPlayer = null;

    public void sendMessage(String message, TypeMessages type) {
        MessagesManager.sendMessage(GlobalUtils.getPlayer(uuid), message, type);
    }

    private void createBossBar(){
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player, 1f, BarColor.BLUE, BarStyle.NOTCHED_10);
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + ammo + "%"));
            bossBar.setProgress((float) ((ammo / MAX_AMMO)*100));
        }
    }

    @Contract(pure = true)
    public static AviaTerraPlayer getPlayer(UUID uuid){
        return players.get(uuid);
    }

    @Contract(pure = true)
    public static AviaTerraPlayer getPlayer(Player player){
        return players.get(player.getUniqueId());
    }

    @Contract(pure = true)
    public Player getPlayer(){
        return GlobalUtils.getPlayer(uuid);
    }

    public static void addPlayer(Player player){
        if (!players.containsKey(player.getUniqueId())){
            players.put(player.getUniqueId(), new AviaTerraPlayer(player));
        }
    }

}
