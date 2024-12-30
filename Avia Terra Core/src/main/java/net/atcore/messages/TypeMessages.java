package net.atcore.messages;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;

@Getter
public enum TypeMessages {
    SUCCESS('2', "a"),
    INFO('3', "b"),
    WARNING('e', "6"),
    ERROR('c',"4"),
    KICK('c',"4l"),
    NULL('7',"8");

    TypeMessages(char mainColor, String secondColor){
        this.mainColor = mainColor;
        this.secondColor = secondColor;
    }

    private final char mainColor;
    private final String secondColor;

    @Deprecated
    @Contract(pure = true)
    public String getMainColor() {
        return '&' + Character.toString(mainColor);
    }

    @Contract(pure = true)
    public String getMainColorWithColorChart() {
        return ChatColor.COLOR_CHAR + Character.toString(mainColor);
    }

    @Contract(pure = true)
    public String getSecondColor() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < secondColor.length(); i++) {
            s.append("&").append(secondColor.charAt(i));
        }
        return s.toString();
    }
}
