package net.atcore.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum TypeMessages {
    SUCCESS(new Tags("dark_green"), new Tags("green")),
    INFO(new Tags("dark_aqua"), new Tags("aqua")),
    WARNING(new Tags("yellow"), new Tags("gold")),
    ERROR(new Tags("red"), new Tags("dark_red")),
    KICK(new Tags("red"), new Tags("dark_red>", "b")),
    NULL(new Tags("gray"), new Tags("dark_red"));

    private final Tags mainColor;
    private final Tags secondColor;

    @Getter
    private static class Tags {

        Tags(String... tag) {
            tags.addAll(Arrays.stream(tag).toList());
        }

        private final List<String> tags = new ArrayList<>();
    }

    public String getMainColor() {
        return String.join("", mainColor.tags.stream().map(s -> "<" + s + ">").toList());
    }

    public String getSecondColor() {
        return String.join("", secondColor.tags.stream().map(s -> "<" + s + ">").toList());
    }

    public String getMainColorClose() {
        return String.join("", mainColor.tags.reversed().stream().map(s -> "</" + s + ">").toList());
    }

    public String getSecondColorClose() {
        return String.join("", secondColor.tags.reversed().stream().map(s -> "</" + s + ">").toList());
    }
}
