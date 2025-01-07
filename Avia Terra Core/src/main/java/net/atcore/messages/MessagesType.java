package net.atcore.messages;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;

@SuppressWarnings("deprecation")
@Getter
public enum MessagesType {
    SUCCESS('2', "a"),
    INFO('3', "b"),
    WARNING('e', "6"),
    ERROR('c',"4"),
    KICK('c',"4l"),
    NULL('7',"8");

    MessagesType(char mainColor, String secondColor){
        this.mainColor = mainColor;
        this.secondColor = secondColor;
    }

    private final char mainColor;
    private final String secondColor;

    @Contract(pure = true)
    public String getMainColor() {
        return ChatColor.COLOR_CHAR + Character.toString(mainColor);
    }

    @Contract(pure = true)
    public String getSecondColor() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < secondColor.length(); i++) {
            s.append(ChatColor.COLOR_CHAR).append(secondColor.charAt(i));
        }
        return s.toString();
    }
}
