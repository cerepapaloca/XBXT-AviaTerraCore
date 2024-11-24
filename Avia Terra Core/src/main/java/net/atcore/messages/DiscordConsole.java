package net.atcore.messages;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DiscordConsole extends Handler {
    private final TextChannel channel;

    public DiscordConsole(TextChannel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getMessage() != null) {
            // Enviar mensaje al canal de Discord
            channel.sendMessage("**[Consola]** " + record.getMessage()).queue();
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
