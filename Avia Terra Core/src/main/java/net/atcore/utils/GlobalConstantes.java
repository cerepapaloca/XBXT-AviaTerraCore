package net.atcore.utils;

import lombok.Getter;
import org.bukkit.Color;

import java.util.HashMap;

import static net.atcore.utils.RangeList.*;

public record GlobalConstantes() {

    public GlobalConstantes(){
        RANGOS_COLORS.put(SEPHIRAH, new Range(Color.fromRGB(0,0,0), "Sephirah", '\uDFEC'));
        RANGOS_COLORS.put(DEVELOPER, new Range(Color.fromRGB(255,0,220), "Developer", '\uDFED'));
        RANGOS_COLORS.put(SERAPHINE, new Range(Color.fromRGB(0,0,0), "Seraphine", '\uDFEE'));
        RANGOS_COLORS.put(VIRTUE, new Range(Color.fromRGB(255,0,0), "Virtue", '\uDFEF'));
        RANGOS_COLORS.put(ARBITRO, new Range(Color.fromRGB(255,106,0), "Arbitro", '\uDFF1'));
        RANGOS_COLORS.put(GUARDIAN, new Range(Color.fromRGB(38,127,0), "Guardian", '\uDFF2'));
        RANGOS_COLORS.put(MOD, new Range(Color.fromRGB(0,127,127), "Mod", '\uDFF3'));
        RANGOS_COLORS.put(BUILDER, new Range(Color.fromRGB(0,19,127), "Builder", '\uDFF4'));
        RANGOS_COLORS.put(PASSCODE, new Range(Color.fromRGB(0,255,255), "Passcode", '\uDFF5'));
        RANGOS_COLORS.put(DEFAULT, new Range(Color.fromRGB(96,96,96), "Usuario", '\uDFF6'));
        RANGOS_COLORS.put(PARTNER, new Range(Color.fromRGB(255,216,0), "Partner", '\uDFF7'));
        RANGOS_COLORS.put(STREAMER, new Range(Color.fromRGB(139,92,246), "Streamer", '\uDFF8'));
        RANGOS_COLORS.put(YOUTUBER, new Range(Color.fromRGB(255,0,0), "Youtube", '\uDFF9'));
        RANGOS_COLORS.put(TIKTOKER, new Range(Color.fromRGB(0,0,0), "TikTok", '\uDFFA'));
    }

    public static final HashMap<RangeList, Range> RANGOS_COLORS = new HashMap<>();

    @Getter
    public static final long NUMERO_PERMA = -1;

}
