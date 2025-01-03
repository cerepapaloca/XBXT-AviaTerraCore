package net.atcore.misc;

import net.atcore.Config;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FrameDupe {

    public static void dupeItem(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof ItemFrame itemFrame){
            if (Config.getChaceDupeFrame() > Math.random()){
                ItemStack frame = itemFrame.getItem();
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), frame);
            }
        }
    }
}
