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
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ArmamentPlayer extends AbstractAviaTerraPlayer implements Compartment {

    ArmamentPlayer(AviaTerraPlayer player) {
        super(player);
    }

    private boolean isReloading = false;
    @Nullable
    private BukkitTask shootTask;

    @Override
    public boolean reload(Player player){
        BaseWeapon baseWeapon = ArmamentUtils.getWeapon(aviaTerraPlayer.getPlayer());
        if (baseWeapon instanceof BaseWeaponUltraKill weapon){
            new BukkitRunnable() {
                public void run() {
                    isReloading = true;
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
                                        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
                                        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + weapon.getMaxAmmo()));
                                        bossBar.setProgress((((float) amountAmmo / (float) weapon.getMaxAmmo())*100));
                                        TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 10);
                                    }
                                    weapon.updateLore(itemArmament, null);
                                }
                            }
                        }
                    }
                    if (player.isOnline()) player.removePotionEffect(PotionEffectType.SLOWNESS);
                    cancel();
                }

                @Override
                public void cancel(){
                    Bukkit.getScheduler().cancelTask(getTaskId());
                    isReloading = false;
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 1L, weapon.getDelay());
            return true;
        }else {
            return false;
        }
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
