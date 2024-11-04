package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public abstract class BaseWeaponUltraKill extends BaseWeapon {

    public BaseWeaponUltraKill(ListWeaponUltraKill weaponType, String displayName, int maxDistance, int manaCost, double precision, ListAmmo ammo) {
        super(displayName, new ItemStack(Material.GOLDEN_HORSE_ARMOR), maxDistance, weaponType.name(), precision);
        this.manaCost = manaCost;
        this.ammo = ArmamentUtils.getAmmo(ammo);
        this.weaponType = weaponType;
        updateLore(getItemArmament(), null);
    }

    private final BaseAmmo ammo;
    private final int manaCost;
    private final ListWeaponUltraKill weaponType;

    @Override
    public void shoot(Player player) {
        AviaTerraPlayer atp = AviaTerraCore.getPlayer(player);
        double manaPlayer = atp.getMana();
        if (manaCost < manaPlayer){
            atp.setMana(manaPlayer - manaCost);
            DataShoot dataShoot = executeShoot(player, ammo, null);
            onShoot(dataShoot);
            ammo.onShoot(dataShoot);
            updateLore(player.getInventory().getItemInMainHand(), null);
        }else{
            MessagesManager.sendMessage(player, "No tiene mana suficiente", TypeMessages.ERROR);
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
                Rango máximo: <|%s|>m
                """,
                this.ammo.getDamage(),
                manaCost,
                (100 - precision) + "%",
                maxDistance
        ), null, CategoryMessages.PRIVATE), true));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
    }

    public abstract void onShoot(DataShoot dataShoot);
}
