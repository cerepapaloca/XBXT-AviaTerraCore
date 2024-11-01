package net.atcore;

import lombok.Getter;
import lombok.Setter;
import net.atcore.moderation.ChatModeration;
import org.bukkit.entity.Player;
@Getter
@Setter
public class AviaTerraPlayer {

    public AviaTerraPlayer(Player player) {
        this.player = player;
    }

    private final Player player;
    private float pointChat = ChatModeration.MAX_PUNTOS;
    private int sanctionsChat = 1;//por circunstancias matemáticas tiene que ser 1
    //private final DataLogin dataLogin;
    private double Mana;
    private boolean isFreeze = false;

}
