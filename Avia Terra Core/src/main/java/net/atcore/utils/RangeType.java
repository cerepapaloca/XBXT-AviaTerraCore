package net.atcore.utils;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.luckperms.api.model.group.Group;
import org.bukkit.Color;
import org.bukkit.permissions.Permission;

import static org.bukkit.Bukkit.getServer;

@Getter
public enum RangeType {
    SEPHIRAH(Color.fromRGB(0,0,0), "Sephirah", '\uDFEC', "", true),
    DEVELOPER(Color.fromRGB(255,0,220), "Developer", '\uDFED', "", true),
    SERAPHINE(Color.fromRGB(255,0,0), "Seraphine", '\uDFEE', "1299841338820268072", true),
    VIRTUE(Color.fromRGB(255,0,0), "Virtue", '\uDFEF', "", false),
    ARBITRO(Color.fromRGB(255,106,0), "Arbitro", '\uDFF1', "", false),
    GUARDIAN(Color.fromRGB(38,127,0), "Guardian", '\uDFF2', "", false),
    MOD(Color.fromRGB(0,127,127), "Mod", '\uDFF3', "", false),
    BUILDER(Color.fromRGB(0,19,127), "Builder", '\uDFF4', "1284309844588888074", false),
    PASSCODE(Color.fromRGB(0,255,255), "Passcode", '\uDFF5', "", false),
    DEFAULT(Color.fromRGB(96,96,96), "Usuario", '\uDFF6', "", false),
    PARTNER(Color.fromRGB(255,216,0), "Partner", '\uDFF7', "", false),
    STREAMER(Color.fromRGB(139,92,246), "Streamer", '\uDFF8', "", false),
    YOUTUBER(Color.fromRGB(255,0,0), "Youtube", '\uDFF9', "", false),
    TIKTOKER(Color.fromRGB(0,0,0), "TikTok", '\uDFFA', "", false);

    private final Color color;
    private final String displayName;
    private final char icon;
    private final String rolId;
    private final String permission;
    private final boolean op;
    private final Group group;

    RangeType(Color color, String displayName, char icon, String rolId, boolean op) {
        this.color = color;
        this.displayName = displayName;
        this.icon = icon;
        this.rolId = rolId;
        this.permission = "aviaterracore.group." + displayName.toLowerCase();
        this.op = op;
        this.group = AviaTerraCore.getLp().getGroupManager().getGroup(this.name().toLowerCase());
        Permission permission = new Permission(this.permission);
        getServer().getPluginManager().addPermission(permission);
    }
}
