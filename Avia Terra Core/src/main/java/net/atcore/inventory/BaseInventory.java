package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseInventory {

    public BaseInventory(int size, Message name) {
        this.name = name;
        this.size = size;
    }

    protected Message name;
    protected int size;
    protected InventorySection section;

    public abstract Inventory createInventory(AviaTerraPlayer player);

    protected Inventory createNewInventory(@Nullable AviaTerraPlayer player) {
        if (player == null) {
            return Bukkit.createInventory(null, size, name.getMessageLocatePrivate());
        }else {
            return Bukkit.createInventory(player.getPlayer(), size, name.getMessage(player.getPlayer()));
        }

    }
}
