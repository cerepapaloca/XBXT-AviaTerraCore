package net.atcore.security.check;

import com.google.gson.reflect.TypeToken;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * A través de esta clase se puede añadir un evento para una situación específica e independiente del resto del plugin.
 * @param <T> El evento que se tiene que escuchar y el parámetro que se vá usar en {@link #onCheck(Event)}
 */

public abstract class BaseChecker<T extends Event> implements Listener {

    public static final HashSet<BaseChecker<? extends Event>> REGISTERED_CHECKS = new HashSet<>();


    protected final EventExecutor eventExecutor;

    /**
     * Los jugadores op serán ignorados
     */

    protected boolean bypassOp = true;
    public boolean enabled = true;
    private Class<T> eventClass;

    @SuppressWarnings("unchecked")
    public BaseChecker() {
        Type type;
        eventExecutor = (listener, event) -> {
            if (!enabled) return;
            if (bypassOp) {
                if (event instanceof PlayerEvent playerEvent) {
                    if (playerEvent.getPlayer().isOp()) {
                        return;
                    }
                }else if (event instanceof InventoryEvent inventoryEvent) {
                    if (inventoryEvent.getView().getPlayer().isOp()) {
                        return;
                    }
                }
            }
            instance(event);
        };
        try {
            type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            eventClass = (Class<T>) TypeToken.get(type).getRawType();
            REGISTERED_CHECKS.add(this);
            Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.LOWEST, eventExecutor, AviaTerraCore.getInstance());
        }catch (ClassCastException e) {
            eventClass = null;
        }
        REGISTERED_CHECKS.add(this);
    }

    protected void instance(Event event) {
        if (eventClass.isInstance(event)) {
            T t = eventClass.cast(event);
            onCheck(t);
        }
    }

    public abstract void onCheck(T event);

}