package net.atcore.placeholder;

import lombok.Getter;
import net.atcore.Section;
import net.atcore.messages.MessagesManager;
import net.atcore.placeholder.holders.ActiveTimeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PlaceHolderSection implements Section {

    public static final HashSet<BasePlaceHolder> HOLDERS = new HashSet<>();
    @Getter
    private static boolean isActive = false;

    @Override
    public void enable(){
        new ActiveTimeHolder();
        try {
            new PlaceHolderHandler().register();
        }catch (Exception e) {
            MessagesManager.sendWaringException("PlaceHolderAPI no esta instalado, ignora este warn si eres consiente de esto", e);
        }
        isActive = true;
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

    @Override
    public boolean isImportant() {
        return false;
    }
}
