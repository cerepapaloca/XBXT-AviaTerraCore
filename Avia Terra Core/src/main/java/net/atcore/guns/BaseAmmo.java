package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class BaseAmmo {

    protected BaseAmmo(ListAmmo list, double damage, String name) {
        this(list, damage, name, Color.fromRGB(80,80,80), false, 1F);
    }

    protected BaseAmmo(ListAmmo list, double damage, String name, Color color, boolean isTrace, float densityTrace) {
        this.nameAmmo = name;
        this.damage = damage;
        this.list = list;
        this.color = color;
        this.isTrace = isTrace;
        this.densityTrace = densityTrace;
    }

    private final ListAmmo list;
    private final String nameAmmo;
    private double damage;
    private Color color;
    private boolean isTrace;
    private float densityTrace;

    public abstract void onShoot(DataShoot dataShoot);
}
