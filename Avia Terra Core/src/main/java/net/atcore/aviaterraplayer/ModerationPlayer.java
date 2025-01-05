package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import net.atcore.moderation.ChatModeration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ModerationPlayer extends AbstractAviaTerraPlayer {

    public ModerationPlayer(AviaTerraPlayer aviaTerraPlayer) {
        super(aviaTerraPlayer);
    }

    private int sanctionsChat = 1;//por circunstancias matemáticas tiene que ser 1
    private double pointChat = ChatModeration.MAX_PUNTOS;
    private long lastChat = System.currentTimeMillis();
    private boolean isFreeze = false;
    private List<UUID> manipulatorInventoryPlayer = new ArrayList<>();
    private UUID manipulatedInventoryPlayer = null;

}
