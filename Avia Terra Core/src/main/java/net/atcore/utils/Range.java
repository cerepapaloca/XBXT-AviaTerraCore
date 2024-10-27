package net.atcore.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;

@Getter
@Setter
public class Range {

    public Range(Color color, String name, char icon) {
        this.color = color;
        this.name = name;
        this.icon = icon;
    }

    private final Color color;
    private final String name;
    private final char icon;
}
