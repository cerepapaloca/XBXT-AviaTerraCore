package net.atcore.misc;

import lombok.experimental.UtilityClass;
import net.atcore.Config;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Dupe {

    public static void frameDupe(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof ItemFrame itemFrame){
            if (Config.getChaceDupeFrame() > Math.random()){
                ItemStack frame = itemFrame.getItem();
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frame);
            }
        }
    }
}
