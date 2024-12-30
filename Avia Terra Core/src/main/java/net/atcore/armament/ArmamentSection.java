package net.atcore.armament;

import net.atcore.Section;
import net.atcore.armament.ammo.MM45Otan;
import net.atcore.armament.ammo.MM45OtanVerde;
import net.atcore.armament.ammo.MMxxPrueba;
import net.atcore.armament.magazines.M4_30;
import net.atcore.armament.magazines.M4_60;
import net.atcore.armament.weapons.M16;
import net.atcore.armament.weapons.M4;
import net.atcore.armament.weapons.Test1;

public class ArmamentSection implements Section {

    //private static final String PACKAGE = "net.atcore.armament";

    @Override
    public void enable() {
        if (ArmamentUtils.ARMAMENTS.isEmpty()) initialize();

    }

    public static void initialize() {
        new MMxxPrueba();
        new MM45OtanVerde();
        new MM45Otan();
        ////////////
        new M4_60();
        new M4_30();
        ////////////
        new M16();
        new Test1();
        ////////////
        new M4();
        ////////////
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
