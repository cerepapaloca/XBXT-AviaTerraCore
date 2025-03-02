package net.atcore.security.check.checker;

import net.atcore.armament.ArmamentUtils;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.check.CheckerUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AntiArmament extends InventoryChecker {

    @Override
    public void onCheck(Event e) {
        if (e instanceof InventoryEvent event) {
            List<ItemStack> items = CheckerUtils.getItems(event);
            HumanEntity player = event.getView().getPlayer();
            List<ItemStack> itemsToRemove = new ArrayList<>();
            int i = 0;
            for (ItemStack item : items){
                if (item == null) continue;
                if (item.getItemMeta() == null) continue;
                if (ArmamentUtils.getArmament(item) == null) continue;
                itemsToRemove.add(item);
                i++;
            }
            itemsToRemove.forEach(item -> item.setAmount(0));
            if (i == 0) return;
            MessagesManager.logConsole(String.format("Se eliminó <|%s|> armas de <|%s|>",
                    i,
                    player.getName()
            ), TypeMessages.WARNING, CategoryMessages.PLAY);
        }
    }


}
