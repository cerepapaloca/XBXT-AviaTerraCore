package net.atcore.messages;

import lombok.Getter;

@Getter
public enum TypeMessages {
    SUCCESS("<dark_green>", "<green>"),
    INFO("<dark_aqua>", "<aqua>"),
    WARNING("<yellow>", "<gold>"),
    ERROR("<red>","<dark_red>"),
    KICK("<red>","<dark_red><b>"),
    NULL("<gray>","<dark_gray>"),;

    TypeMessages(String mainColor, String secondTag) {
        this.mainColor = mainColor;
        this.secondColor = secondTag;
    }

    private final String mainColor;
    private final String secondColor;
}
