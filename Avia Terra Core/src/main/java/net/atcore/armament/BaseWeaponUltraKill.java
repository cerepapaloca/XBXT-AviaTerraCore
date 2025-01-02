package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BossBar;
import net.atcore.aviaterraplayer.ArmamentPlayer;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public abstract class BaseWeaponUltraKill extends BaseWeapon {

    public BaseWeaponUltraKill(String displayName,
                               int maxDistance,
                               int reloadDelay,
                               int maxAmmo,
                               double vague,
                               Class<? extends BaseAmmo> ammo,
                               WeaponMode mode,
                               int cadence
    ) {
        super(new ItemStack(Material.GOLDEN_HORSE_ARMOR), maxDistance, displayName, vague, mode, cadence);
        this.reloadDelay = reloadDelay;
        this.ammo = ArmamentUtils.getAmmo(ammo);
        this.maxAmmo = maxAmmo;
        GlobalUtils.setPersistentData(itemArmament, "AmountAmmo", PersistentDataType.INTEGER, 0);
        updateLore(getItemArmament(), null);
    }

    private final BaseAmmo ammo;
    private final int reloadDelay;
    private final int maxAmmo;

    @Override
    public void processShoot(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getItemMeta() == null)return;
        AviaTerraPlayer aviaPlayer = AviaTerraPlayer.getPlayer(player);
        if (aviaPlayer.getArmamentPlayer().isReloading()){
            MessagesManager.sendTitle(player, "", MessagesType.ERROR.getMainColor() + "Estas Recargando", 0, 10, 30, MessagesType.ERROR);
            return;
        }
        Integer amountAmmo = (Integer) GlobalUtils.getPersistenData(item,"AmountAmmo", PersistentDataType.INTEGER);
        if (amountAmmo != null){
            if (amountAmmo > 0) {
                processRayShoot(player, ammo, null);
                amountAmmo--;
                GlobalUtils.setPersistentData(item, "AmountAmmo", PersistentDataType.INTEGER, amountAmmo);
                ArmamentPlayer armamentPlayer = AviaTerraPlayer.getPlayer(player).getArmamentPlayer();
                if (armamentPlayer.getBossBar() == null){
                    armamentPlayer.createBossBar();
                }
                BossBar bossBar = armamentPlayer.getBossBar();
                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
                if (tabPlayer != null){
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', "&3&lCantidad De Munición: &6&l" + amountAmmo));
                    bossBar.setProgress((((float) amountAmmo / (float) maxAmmo)*100));
                    TabAPI.getInstance().getBossBarManager().sendBossBarTemporarily(tabPlayer, bossBar.getName(), 3);
                }
                updateLore(player.getInventory().getItemInMainHand(), null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, SoundCategory.PLAYERS, 1.1f, 0.8f);
                return;
            }
        }
        MessagesManager.sendTitle(player, "", MessagesType.ERROR.getMainColor() + "Sin munición", 0, 10, 30, MessagesType.ERROR);
    }

    @Override
    public void updateLore(ItemStack itemStack, ItemStack itemAuxiliar){
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(String.format("""
                Daño: <|%s|>
                Presión: <|%s|>
                Munición: <|%s|>
                Rango máximo: <|%sm|>
                """,
                this.ammo.getDamage(),
                (100 - vague) + "%",
                GlobalUtils.getPersistenData(itemStack,"AmountAmmo", PersistentDataType.INTEGER),
                maxDistance
        ), null, false, false), true));
        itemStack.setItemMeta(meta);
    }
}
