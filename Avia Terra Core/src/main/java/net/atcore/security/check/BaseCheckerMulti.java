package net.atcore.security.check;


import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.*;

/**
 * Es escuchar multiples eventos pero {@link #onCheck(Event) onCheck} tendrá como parámetro {@link Event}
 */

public abstract class BaseCheckerMulti extends BaseChecker<Event>  {

    protected final List<Class<? extends Event>> eventClasses = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public BaseCheckerMulti(Class<? extends Event>... events) {
        eventClasses.addAll(Arrays.asList(events));
        for (Class<? extends Event> eventClass : events) {
            Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.LOWEST, eventExecutor, AviaTerraCore.getInstance());
        }
    }

    @Override
    protected void instance(Event event) {
        for (Class<? extends Event> eventClass : eventClasses) {
            if (eventClass.isInstance(event)) {
                onCheck(event);
                return;
            }
        }
    }
}
