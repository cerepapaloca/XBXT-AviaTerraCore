package net.atcore.security.check.checker;

import jdk.jfr.Experimental;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.check.CheckerUtils;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@Experimental
public class RangePurge extends InventoryChecker{
    @Override
    public void onCheck(Event event) {
        if (event instanceof InventoryEvent){
            for (ItemStack item : CheckerUtils.getItems((InventoryEvent) event)){
                if (item == null) continue;
                if (!item.getType().equals(Material.NAME_TAG)) continue;
                Long date = (Long) GlobalUtils.getPersistenData(item, "dateCreationRange", PersistentDataType.LONG);
                if (date == null)continue;
                if (date > Config.getPurgeTagRange()) continue;
                item.setAmount(0);
                MessagesManager.logConsole("Tag Eliminada", TypeMessages.INFO, CategoryMessages.PLAY);
            }
        }
    }
}
