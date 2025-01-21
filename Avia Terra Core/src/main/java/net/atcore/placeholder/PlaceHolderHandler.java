package net.atcore.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.atcore.AviaTerraCore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderHandler extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return AviaTerraCore.getInstance().getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return AviaTerraCore.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return AviaTerraCore.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        for (BasePlaceHolder placeHolder : PlaceHolderSection.HOLDERS){
            if (placeHolder.getIdentifier().equals(identifier)) return placeHolder.onPlaceholderRequest(player);
        }
        return null;
    }
}
