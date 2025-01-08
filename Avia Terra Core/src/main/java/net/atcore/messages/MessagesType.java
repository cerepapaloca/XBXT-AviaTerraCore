package net.atcore.messages;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.Tag;

@Getter
public enum MessagesType {
    SUCCESS("<dark_green>", "<green>"),
    INFO("<dark_aqua>", "<aqua>"),
    WARNING("<yellow>", "<gold>"),
    ERROR("<red>","<dark_red>"),
    KICK("<red>","<dark_red><b>"),
    NULL("<gray>","<dark_gray>"),;

    MessagesType(String mainColor, String secondTag) {
        this.mainColor = mainColor;
        this.secondColor = secondTag;
    }

    private final String mainColor;
    private final String secondColor;
}
