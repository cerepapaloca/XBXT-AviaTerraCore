package net.atcore.armament;

import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.armament.ammo.MM45Otan;
import net.atcore.armament.ammo.MM45OtanVerde;
import net.atcore.armament.ammo.MMxxPrueba;
import net.atcore.armament.magazines.M4_30;
import net.atcore.armament.magazines.M4_60;
import net.atcore.armament.weapons.M16;
import net.atcore.armament.weapons.M4;
import net.atcore.armament.weapons.Test1;
import net.atcore.messages.MessagesManager;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

public class ArmamentSection implements Section {

    private static final String PACKAGE = "net.atcore.armament";

    @Override
    public void enable() {
        if (ArmamentUtils.ARMAMENTS.isEmpty()) initialize();

    }

    public static void initialize() {
        ArmamentUtils.ARMAMENTS.add(new MM45Otan());
        ArmamentUtils.ARMAMENTS.add(new MM45OtanVerde());
        ArmamentUtils.ARMAMENTS.add(new MMxxPrueba());
        ///////////////////
        ArmamentUtils.ARMAMENTS.add(new M4_30());
        ArmamentUtils.ARMAMENTS.add(new M4_60());
        ///////////////////
        ArmamentUtils.ARMAMENTS.add(new M4());
        //////////////////
        ArmamentUtils.ARMAMENTS.add(new M16());
        ArmamentUtils.ARMAMENTS.add(new Test1());
        /*Reflections reflections = new Reflections(PACKAGE);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Initializer.class);
        for (Class<?> clazz : annotatedClasses) {
            try {
                Class<? extends BaseArmament> armament = clazz.asSubclass(BaseArmament.class);
                ArmamentUtils.ARMAMENTS.add(armament.getConstructor().newInstance());
            }catch (ClassCastException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e ) {
                MessagesManager.sendException("Error al cargar esta clase " + clazz.getName(), e);
            }
        }*/
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "Armamento";
    }

    @Override
    public void reload() {

    }
}
