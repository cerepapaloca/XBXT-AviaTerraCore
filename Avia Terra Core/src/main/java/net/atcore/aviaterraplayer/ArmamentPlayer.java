package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.AviaTerraCore;
import net.atcore.armament.ArmamentUtils;
import net.atcore.armament.BaseWeapon;
import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class ArmamentPlayer extends AbstractAviaTerraPlayer {

    ArmamentPlayer(AviaTerraPlayer player) {
        super(player);
        /*new BukkitRunnable() {
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
        }.runTaskTimer(AviaTerraCore.getInstance(), 5, 5);*/
    }

    public void onReload(){
        ItemStack itemArmament = aviaTerraPlayer.getPlayer().getInventory().getItemInMainHand();
        BaseWeapon baseWeapon = ArmamentUtils.getWeapon(aviaTerraPlayer.getPlayer());
        if (baseWeapon instanceof BaseWeaponUltraKill weapon){
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (itemArmament.getItemMeta() != null){
                        Integer amountAmmo = (Integer) GlobalUtils.getPersistenData(itemArmament,"AmountAmmo", PersistentDataType.INTEGER);
                        if (amountAmmo != null && amountAmmo > 0){
                            if (amountAmmo <= weapon.getMaxAmmo()){
                                amountAmmo++;
                                GlobalUtils.setPersistentData(itemArmament, "AmountAmmo", PersistentDataType.INTEGER, amountAmmo);
                                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(aviaTerraPlayer.getUuid());
                                if (bossBar == null){
                                    createBossBar();
                                }
                                if (tabPlayer != null){
                                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + amountAmmo));
                                    bossBar.setProgress((float) ((amountAmmo / weapon.getMaxAmmo())*100));
                                    TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 3);
                                }
                            }else {
                                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + weapon.getMaxAmmo()));
                            }
                        }else {
                            cancel();
                        }
                    }else {
                        cancel();
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 1L, weapon.getDelay());
        }
    }

    private BossBar bossBar;

    public void createBossBar(){
        Player player = Bukkit.getPlayer(aviaTerraPlayer.getPlayer().getUniqueId());
        if (player != null) {
            bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player, 1f, BarColor.BLUE, BarStyle.NOTCHED_10);
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l ?"));
        }
    }
}
