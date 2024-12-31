package net.atcore.listener;

import net.atcore.command.CommandManager;
import net.atcore.messages.ConsoleDiscord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ConsoleDiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Member member = event.getMember();
        assert member != null;

        Message message = event.getMessage();
        String channel = event.getChannel().getId();
        if (channel.equals(ConsoleDiscord.consoleId)){
            if (message.getContentRaw().startsWith("/")) CommandManager.processCommandFromDiscord(message, member);
        }
    }
}
