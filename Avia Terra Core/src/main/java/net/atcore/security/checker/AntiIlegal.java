package net.atcore.security.checker;

import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

import static org.bukkit.Material.*;

public class AntiIlegal extends BaseChecker<InventoryClickEvent> {

    private static final Set<Material> ILEGAL_ITEMS = Set.of(BEDROCK, END_PORTAL_FRAME, COMMAND_BLOCK, BARRIER,
            STRUCTURE_VOID, STRUCTURE_BLOCK, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER, REINFORCED_DEEPSLATE);

    @Override
    public void onCheck(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        List<ItemStack> items = new ArrayList<>(Arrays.stream(human.getOpenInventory().getTopInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
        items.addAll(Arrays.stream(human.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
        items.add(event.getCurrentItem());
        for (ItemStack item : items){
            if (item == null) continue;
            if (item.getType() == Material.AIR) continue;
            if (ILEGAL_ITEMS.contains(item.getType())){
                MessagesManager.logConsole(String.format("Se eliminó <|%s|> de <|%s|>",
                        item.getType().toString().toLowerCase(),
                        event.getWhoClicked().getName()
                ), TypeMessages.WARNING, CategoryMessages.PLAY);
                item.setAmount(0);
            }
        }
    }
}
