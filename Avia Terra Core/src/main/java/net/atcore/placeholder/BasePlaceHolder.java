package net.atcore.placeholder;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class BasePlaceHolder {

    protected final String identifier;

    public BasePlaceHolder(String identifier) {
        this.identifier = identifier;
        PlaceHolderSection.HOLDERS.add(this);
    }

    public abstract String onPlaceholderRequest(Player player);
}
