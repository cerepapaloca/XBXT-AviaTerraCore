package net.atcore.messages;

import net.atcore.Section;

public class MessageSection implements Section {



    @Override
    public void enable() {
        ConsoleDiscord.startConsoleAndBot();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        ConsoleDiscord.handler.shutdown();
        ConsoleDiscord.startConsoleAndBot();
    }

    @Override
    public String getName() {
        return "Mensajes";
    }
}
