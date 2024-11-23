package net.atcore.messages;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

@Getter
public enum CategoryMessages {
    PRIVATE(null),
    COMMANDS("1308198706264277093"),
    BAN("1294324328401207389"),
    MODERATION("1294324285602795550"),
    LOGIN("1299444352409669746"),
    PVP(null);

    CategoryMessages(String idChannel) {
        this.idChannel = idChannel;
    }

    @SuppressWarnings("NonFinalFieldInEnum")
    @Setter
    private String idChannel;

}
