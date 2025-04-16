package net.atcore.placeholder;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.atcore.Section;
import net.atcore.messages.MessagesManager;
import net.atcore.placeholder.holders.ActiveTimeHolder;
import net.atcore.placeholder.holders.CurrentTpsHolder;
import net.atcore.placeholder.holders.PingHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PlaceHolderSection implements Section {

    public static final HashSet<BasePlaceHolder> HOLDERS = new HashSet<>();
    @Getter
    private static boolean isActivePlaceHolderApi = false;

    @Override
    public void enable(){
        new ActiveTimeHolder();
        new CurrentTpsHolder();
        new PingHolder();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderHandler().register();
            isActivePlaceHolderApi = true;
        }
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

    public static String applyPlaceholders(Player player, String input){
        if (isActivePlaceHolderApi) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }else {
            return PlaceHolderLocalHandler.processPlaceholders(player, input);
        }
    }
}
