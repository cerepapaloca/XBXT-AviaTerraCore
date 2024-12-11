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
import net.atcore.armament.Compartment;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class ArmamentPlayer extends AbstractAviaTerraPlayer implements Compartment {

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

    @Override
    public boolean reload(Player player){
        BaseWeapon baseWeapon = ArmamentUtils.getWeapon(aviaTerraPlayer.getPlayer());

        if (baseWeapon instanceof BaseWeaponUltraKill weapon){
            new BukkitRunnable() {
                public void run() {
                    ItemStack itemArmament = aviaTerraPlayer.getPlayer().getInventory().getItemInMainHand();
                    if (player.isOnline()){
                        if (itemArmament.getItemMeta() != null){
                            Integer amountAmmo = (Integer) GlobalUtils.getPersistenData(itemArmament,"AmountAmmo", PersistentDataType.INTEGER);
                            if (amountAmmo != null){
                                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(aviaTerraPlayer.getUuid());
                                if (tabPlayer != null){
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, weapon.getDelay() + 1, 2, true, false, false));
                                    if (bossBar == null){
                                        createBossBar();
                                    }
                                    if (amountAmmo < weapon.getMaxAmmo()){
                                        amountAmmo++;
                                        GlobalUtils.setPersistentData(itemArmament, "AmountAmmo", PersistentDataType.INTEGER, amountAmmo);
                                        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + amountAmmo));
                                        bossBar.setProgress((((float) amountAmmo / (float) weapon.getMaxAmmo())*100));
                                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), weapon.getDelay()*20 + 20);
                                        return;
                                    }else {
                                        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + weapon.getMaxAmmo()));
                                        bossBar.setProgress((((float) amountAmmo / (float) weapon.getMaxAmmo())*100));
                                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 10);
                                    }
                                }
                            }
                        }
                    }
                    if (player.isOnline()) player.removePotionEffect(PotionEffectType.SLOWNESS);
                    cancel();
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 1L, weapon.getDelay());
            return true;
        }
        return false;
    }

    @Override
    public boolean outCompartment(Player player, ItemStack item) {
        return false;
    }

    private BossBar bossBar;

    public void createBossBar(){
        Player player = Bukkit.getPlayer(aviaTerraPlayer.getPlayer().getUniqueId());
        if (player != null) {
            bossBar = TabAPI.getInstance().getBossBarManager().createBossBar("timerBossBar" + player, 1f, BarColor.BLUE, BarStyle.NOTCHED_10);
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l ?"));
            bossBar.setProgress(0.5f);
        }
    }
}
