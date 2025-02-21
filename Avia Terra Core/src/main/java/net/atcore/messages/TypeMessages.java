package net.atcore.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeMessages {
    SUCCESS("<dark_green>", "<green>"),
    INFO("<dark_aqua>", "<aqua>"),
    WARNING("<yellow>", "<gold>"),
    ERROR("<red>","<dark_red>"),
    KICK("<red>","<dark_red><b>"),
    NULL("<gray>","<dark_gray>"),;

    private final String mainColor;
    private final String secondColor;
}
