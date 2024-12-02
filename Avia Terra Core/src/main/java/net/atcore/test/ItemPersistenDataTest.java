package net.atcore.test;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.TypeMessages;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public final class ItemPersistenDataTest implements RunTest {

    @Override
    public void runTest(AviaTerraPlayer player) {
        if (player.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null){
            player.sendMessage("Tiene que tener un item en la mano", TypeMessages.ERROR);
            return;
        }
        Map<String, Object> map = getPersistentData(player.getPlayer().getInventory().getItemInMainHand());
        if (map.isEmpty()){
            player.sendMessage("No contiene datos el item", TypeMessages.INFO);
            return;
        }
        for (String key : map.keySet()) {
            player.sendMessage(String.format("Llave: <|%s|> Contiene: <|%s|>", key, map.get(key)), TypeMessages.INFO);
        }
    }

    public Map<String, Object> getPersistentData(ItemStack item) {
        Map<String, Object> dataMap = new HashMap<>();

        if (item == null || !item.hasItemMeta()) {
            return dataMap; // Retorna un mapa vacío si el item es null o no tiene metadatos
        }

        PersistentDataHolder holder = item.getItemMeta();
        assert holder != null;
        PersistentDataContainer container = holder.getPersistentDataContainer();

        // Recorre cada NamespacedKey en el contenedor
        for (NamespacedKey key : container.getKeys()) {
            // Identifica el tipo de dato almacenado
            Object value = getValue(container, key);
            if (value != null) {
                dataMap.put(key.toString(), value);
            }
        }

        return dataMap;
    }

    @SuppressWarnings("deprecation")
    private Object getValue(PersistentDataContainer container, NamespacedKey key) {
        // Intenta obtener el valor para varios tipos de datos
        if (container.has(key, PersistentDataType.STRING)) {
            return container.get(key, PersistentDataType.STRING);
        } else if (container.has(key, PersistentDataType.INTEGER)) {
            return container.get(key, PersistentDataType.INTEGER);
        } else if (container.has(key, PersistentDataType.FLOAT)) {
            return container.get(key, PersistentDataType.FLOAT);
        } else if (container.has(key, PersistentDataType.DOUBLE)) {
            return container.get(key, PersistentDataType.DOUBLE);
        } else if (container.has(key, PersistentDataType.LONG)) {
            return container.get(key, PersistentDataType.LONG);
        } else if (container.has(key, PersistentDataType.BYTE)) {
            return container.get(key, PersistentDataType.BYTE);
        } else if (container.has(key, PersistentDataType.BYTE_ARRAY)) {
            return container.get(key, PersistentDataType.BYTE_ARRAY);
        } else if (container.has(key, PersistentDataType.INTEGER_ARRAY)) {
            return container.get(key, PersistentDataType.INTEGER_ARRAY);
        } else if (container.has(key, PersistentDataType.LONG_ARRAY)) {
            return container.get(key, PersistentDataType.LONG_ARRAY);
        } else if (container.has(key, PersistentDataType.TAG_CONTAINER)) {
            return container.get(key, PersistentDataType.TAG_CONTAINER);
        } else if (container.has(key, PersistentDataType.TAG_CONTAINER_ARRAY)) {
            return container.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);
        }
        // Si el tipo no es reconocido, retorna null
        return null;
    }
}
