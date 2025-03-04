package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.armament.ArmamentUtils;
import net.atcore.armament.BaseWeapon;
import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.Compartment;
import net.atcore.utils.GlobalUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
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
    private BukkitTask bossBarTask;
    @Nullable
    private BukkitTask shootTask;

    private long lastShoot = 0;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);



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
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, weapon.getReloadDelay() + 1, 2, true, false, false));
                                sendBossBar(player, weapon, amountAmmo);
                                weapon.updateLore(itemArmament, null);
                                if (amountAmmo < weapon.getMaxAmmo()) {
                                    GlobalUtils.setPersistentData(itemArmament, "AmountAmmo", PersistentDataType.INTEGER, amountAmmo + 1);
                                    return;
                                }
                            }

                        }
                    }
                    if (player.isOnline()) player.removePotionEffect(PotionEffectType.SLOWNESS);
                    cancel();
                }

                @Override
                public void cancel(){
                    super.cancel();
                    isReloading = false;
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 1L, weapon.getReloadDelay());
            return true;
        }else {
            return false;
        }
    }

    public void sendBossBar(Player player, BaseWeaponUltraKill weapon, int amountAmmo) {
        bossBar.removeViewer(player);
        Component title = AviaTerraCore.getMiniMessage().deserialize("<aqua>Cantidad De Munición:</aqua> <gold>" + amountAmmo + "</gold>");
        float progres = (((float) amountAmmo / (float) weapon.getMaxAmmo()));
        bossBar.progress(progres);
        bossBar.name(title);
        bossBar.addViewer(player);
        if (bossBarTask != null) bossBarTask.cancel();
        bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                bossBar.removeViewer(aviaTerraPlayer.getPlayer());
                aviaTerraPlayer.getPlayer().sendMessage("AA");
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*5);
    }

    @Override
    public boolean outCompartment(Player player, ItemStack item) {
        return false;
    }
}
