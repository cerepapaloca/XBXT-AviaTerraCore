package net.atcore.utils;

import lombok.Getter;
import org.bukkit.Color;

@Getter
public enum RangeList {
    SEPHIRAH(Color.fromRGB(0,0,0), "Sephirah", '\uDFEC'),
    DEVELOPER(Color.fromRGB(255,0,220), "Developer", '\uDFED'),
    SERAPHINE(Color.fromRGB(255,0,0), "Seraphine", '\uDFEE'),
    VIRTUE(Color.fromRGB(255,0,0), "Virtue", '\uDFEF'),
    ARBITRO(Color.fromRGB(255,106,0), "Arbitro", '\uDFF1'),
    GUARDIAN(Color.fromRGB(38,127,0), "Guardian", '\uDFF2'),
    MOD(Color.fromRGB(0,127,127), "Mod", '\uDFF3'),
    BUILDER(Color.fromRGB(0,19,127), "Builder", '\uDFF4'),
    PASSCODE(Color.fromRGB(0,255,255), "Passcode", '\uDFF5'),
    DEFAULT(Color.fromRGB(96,96,96), "Usuario", '\uDFF6'),
    PARTNER(Color.fromRGB(255,216,0), "Partner", '\uDFF7'),
    STREAMER(Color.fromRGB(139,92,246), "Streamer", '\uDFF8'),
    YOUTUBER(Color.fromRGB(255,0,0), "Youtube", '\uDFF9'),
    TIKTOKER(Color.fromRGB(0,0,0), "TikTok", '\uDFFA');

    private final Color color;
    private final String displayName;
    private final char icon;
    private final String name;

    RangeList(Color color, String displayName, char icon) {
        this.color = color;
        this.displayName = displayName;
        this.icon = icon;
        this.name = this.name();
    }

    public String getPermission(){
        return "aviaterra.range." + displayName.toLowerCase();
    }
}
