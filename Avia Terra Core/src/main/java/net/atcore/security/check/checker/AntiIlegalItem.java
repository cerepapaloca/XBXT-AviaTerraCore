package net.atcore.security.check.checker;

import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.check.CheckerUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;

public class AntiIlegalItem extends InventoryChecker {

    private static final Set<Material> ILEGAL_ITEMS = Set.of(BEDROCK, END_PORTAL_FRAME, COMMAND_BLOCK, BARRIER,
            STRUCTURE_VOID, STRUCTURE_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER, REINFORCED_DEEPSLATE);

    @Override
    public void onCheck(Event e) {
        if (e instanceof InventoryEvent event){
            HumanEntity player = event.getView().getPlayer();
            List<ItemStack> items = CheckerUtils.getItems(event);
            int i = 0;
            for (ItemStack item : items){
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                if (ILEGAL_ITEMS.contains(item.getType())){
                    item.setAmount(0);
                    i++;
                }
            }
            if (i == 0)return;
            MessagesManager.logConsole(String.format("Se eliminó <|%s|> items ilegales de <|%s|>",
                    i,
                    player.getName()
            ), TypeMessages.WARNING, CategoryMessages.PLAY);
        }
    }

}

