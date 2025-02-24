package net.atcore.security.checker;

import com.google.gson.reflect.TypeToken;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

public abstract class BaseChecker<T extends Event> implements Listener {

    public static final HashSet<BaseChecker<? extends Event>> REGISTERED_CHECKS = new HashSet<>();

    protected boolean byPassOp = true;
    public boolean enabled = true;

    @SuppressWarnings("unchecked")
    public BaseChecker() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class<T> eventClass = (Class<T>) TypeToken.get(type).getRawType();
        EventExecutor eventExecutor = (listener, event) -> {
            if (!enabled) return;
            if (byPassOp) {
                if (event instanceof PlayerEvent playerEvent) {
                    if (playerEvent.getPlayer().isOp()) {
                        return;
                    }
                }else if (event instanceof InventoryInteractEvent interactEvent) {
                    if (interactEvent.getWhoClicked().isOp()) {
                        return;
                    }
                }
            }

            if (eventClass.isInstance(event)) {
                T t = eventClass.cast(event);
                onCheck(t);
            }
        };
        REGISTERED_CHECKS.add(this);
        Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.LOWEST, eventExecutor, AviaTerraCore.getInstance());
    }

    public abstract void onCheck(T event);

}