package net.atcore.messages;

import net.atcore.Section;

public class MessageSection implements Section {



    @Override
    public void enable() {
        DiscordBot.startDiscordBot();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        DiscordBot.handler.shutdown();
        DiscordBot.startDiscordBot();
    }

    @Override
    public String getName() {
        return "Mensajes";
    }
}
