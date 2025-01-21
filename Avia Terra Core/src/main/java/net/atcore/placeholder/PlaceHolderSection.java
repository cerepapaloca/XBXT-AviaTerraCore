package net.atcore.placeholder;

import net.atcore.Section;
import net.atcore.placeholder.holders.ActiveTimeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PlaceHolderSection implements Section {

    public static final HashSet<BasePlaceHolder> HOLDERS = new HashSet<>();

    @Override
    public void enable(){
        new ActiveTimeHolder();
        new PlaceHolderHandler().register();
    }

    @Override
    public void disable(){

    }

    @Override
    public void reload(){

    }

    @Override
    public @NotNull String getName(){
        return "placeholder";
    }
}
