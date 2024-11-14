package net.atcore.utils;

import lombok.Getter;
import org.bukkit.Color;

@Getter
public enum RangeList {
    SEPHIRAH(Color.fromRGB(0,0,0), "Sephirah", '\uDFEC', true),
    DEVELOPER(Color.fromRGB(255,0,220), "Developer", '\uDFED', true),
    SERAPHINE(Color.fromRGB(255,0,0), "Seraphine", '\uDFEE', true),
    VIRTUE(Color.fromRGB(255,0,0), "Virtue", '\uDFEF', true),
    ARBITRO(Color.fromRGB(255,106,0), "Arbitro", '\uDFF1', true),
    GUARDIAN(Color.fromRGB(38,127,0), "Guardian", '\uDFF2', true),
    MOD(Color.fromRGB(0,127,127), "Mod", '\uDFF3', true),
    BUILDER(Color.fromRGB(0,19,127), "Builder", '\uDFF4', true),
    PASSCODE(Color.fromRGB(0,255,255), "Passcode", '\uDFF5', false),
    DEFAULT(Color.fromRGB(96,96,96), "Usuario", '\uDFF6', false),
    PARTNER(Color.fromRGB(255,216,0), "Partner", '\uDFF7', false),
    STREAMER(Color.fromRGB(139,92,246), "Streamer", '\uDFF8', false),
    YOUTUBER(Color.fromRGB(255,0,0), "Youtube", '\uDFF9', false),
    TIKTOKER(Color.fromRGB(0,0,0), "TikTok", '\uDFFA', false);

    private final Color color;
    private final String displayName;
    private final char icon;
    private final boolean staff;

    RangeList(Color color, String displayName, char icon, boolean staff) {
        this.color = color;
        this.displayName = displayName;
        this.icon = icon;
        this.staff = staff;
    }

    public String getPermission(){
        return "aviaterra.range." + displayName.toLowerCase();
    }
}
