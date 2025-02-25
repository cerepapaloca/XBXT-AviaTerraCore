package net.atcore.security.check.checker;

import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.check.BaseCheckerMulti;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;

public class AntiIlegalItem extends BaseCheckerMulti {

    private static final Set<Material> ILEGAL_ITEMS = Set.of(BEDROCK, END_PORTAL_FRAME, COMMAND_BLOCK, BARRIER,
            STRUCTURE_VOID, STRUCTURE_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER, REINFORCED_DEEPSLATE);

    @SuppressWarnings("unchecked")
    public AntiIlegalItem() {
        super(InventoryOpenEvent.class, InventoryClickEvent.class);
    }

    @Override
    public void onCheck(Event e) {
        if (e instanceof InventoryEvent event){
            HumanEntity player = event.getView().getPlayer();
            List<ItemStack> items = new ArrayList<>(Arrays.stream(player.getOpenInventory().getTopInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
            items.addAll(Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
            for (ItemStack item : items){
                if (item == null) continue;
                if (item.getType() == Material.AIR) continue;
                if (ILEGAL_ITEMS.contains(item.getType())){
                    MessagesManager.logConsole(String.format("Se eliminó <|%s|> de <|%s|>",
                            item.getType().toString().toLowerCase(),
                            player.getName()
                    ), TypeMessages.WARNING, CategoryMessages.PLAY);
                    item.setAmount(0);
                }
            }
        }
    }

}

