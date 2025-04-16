package net.atcore.placeholder;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class BasePlaceHolder {

    protected final String identifier;

    public BasePlaceHolder(String identifier) {
        this.identifier = identifier;
        PlaceHolderSection.HOLDERS.add(this);
    }

    @NotNull
    public abstract String onPlaceholderRequest(@Nullable Player player);
}
