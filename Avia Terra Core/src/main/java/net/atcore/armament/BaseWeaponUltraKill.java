package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public abstract class BaseWeaponUltraKill extends BaseWeapon {

    public BaseWeaponUltraKill(String displayName, int maxDistance, int cost, double precision, ListAmmo ammo) {
        super(new ItemStack(Material.GOLDEN_HORSE_ARMOR), maxDistance, displayName, precision);
        this.cost = cost;
        this.ammo = ammo.getAmmo();
        updateLore(getItemArmament(), null);
    }

    private final BaseAmmo ammo;
    private final int cost;

    @Override
    public void shoot(Player player) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        double ammoPlayer = atp.getWeaponPlayer().getAmmo();
        if (cost < ammoPlayer){
            atp.getWeaponPlayer().setAmmo(ammoPlayer - cost);
            DataShoot dataShoot = executeShoot(player, ammo, null);
            onShoot(dataShoot);
            ammo.onShoot(dataShoot);
            updateLore(player.getInventory().getItemInMainHand(), null);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_HIT, SoundCategory.PLAYERS, 1, 1.3f);
        }else{
            MessagesManager.sendMessage(player, "No tienes munición suficiente", TypeMessages.ERROR);
        }
    }

    @Override
    public void updateLore(ItemStack itemStack, ItemStack itemAuxiliar){
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(String.format("""
                Daño: <|%s|>
                Coste: <|%s|>
                Presión: <|%s|>
                Rango máximo: <|%sm|>
                """,
                this.ammo.getDamage(),
                cost,
                (100 - precision) + "%",
                maxDistance
        ), null, false, false), true));
        itemStack.setItemMeta(meta);
    }
}
