package net.atcore.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.atcore.AviaTerraCore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public final class PlaceHolderHandler extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return AviaTerraCore.getInstance().getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return AviaTerraCore.getInstance().getDescription().getAuthors().getFirst();
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
        return PlaceHolderSection.HOLDERS.stream().filter(placeHolder -> placeHolder.getIdentifier().equals(identifier)).findFirst().map(placeHolder -> placeHolder.onPlaceholderRequest(player)).orElse(null);
    }
}
