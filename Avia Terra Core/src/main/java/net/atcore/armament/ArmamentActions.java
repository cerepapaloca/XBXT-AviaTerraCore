package net.atcore.armament;

import lombok.experimental.UtilityClass;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ArmamentActions {

    public boolean shootAction(Action action, Player player) {
        if (action.equals(Action.RIGHT_CLICK_AIR) ||action.equals(Action.RIGHT_CLICK_BLOCK)) {
            BaseWeapon weapon = ArmamentUtils.getWeapon(player);
            if (weapon == null) return false;
            weapon.preProcessShoot(player);

            return true;
        }else {
            return false;
        }
    }

    public boolean outAction(ClickType clickType, Player player, ItemStack currentItem) {
        if (currentItem.getItemMeta() == null) return false;
        if (clickType == ClickType.SWAP_OFFHAND) {
            Compartment compartment = ArmamentUtils.getCompartment(currentItem);
            if (compartment != null) {
                if (compartment.outCompartment(player, currentItem)){
                    player.getWorld().playSound(player.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1, 1);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean reloadAction(Player player, ItemStack item) {
        if (item.getItemMeta() == null) return false;
        Compartment compartment = ArmamentUtils.getCompartment(item);
        if (compartment != null){
            compartment.reload(player);
            return true;
        }else {
            AviaTerraPlayer aviaPlayer = AviaTerraPlayer.getPlayer(player);
            return aviaPlayer.getArmamentPlayer().reload(player);
        }
    }

}
