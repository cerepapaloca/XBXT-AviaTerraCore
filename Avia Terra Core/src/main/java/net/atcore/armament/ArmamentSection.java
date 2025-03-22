package net.atcore.armament;

import net.atcore.Section;
import net.atcore.armament.ammo.*;
import net.atcore.armament.ammo.Shot;
import net.atcore.armament.magazines.*;
import net.atcore.armament.weapons.*;

public class ArmamentSection implements Section {

    //private static final String PACKAGE = "net.atcore.armament";

    @Override
    public void enable() {
        if (ArmamentUtils.ARMAMENTS.isEmpty()) initialize();

    }

    public static void initialize() {
        new OtanMediumPenetration();
        new OtanMediumTrace();
        new OtanMediumNormal();
        new UURSMediumPenetration();
        new UURSMediumTrace();
        new UURSMediumNormal();
        new Shot();
        ////////////
        new OtanMedium60();
        new OtanMedium30();
        new OtanMedium15();
        new UURSMedium60();
        new UURSMedium30();
        new UURSMedium15();
        ////////////
        new AK47();
        new AR15();
        new M4();
        new M16();
        new SKS();

        new Test1();
        new Test2();
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

    @Override
    public boolean isImportant() {
        return true;
    }
}
