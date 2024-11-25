package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessageSection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class ConsoleDiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        User author = event.getAuthor();
        String message = event.getMessage().getContentRaw();
        String channel = event.getChannel().getId();
        if (channel.equals(MessageSection.CONSOLE_ID)){
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
                MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s"
                        , author.getName() + "(" + author.getId() + ")" , message), TypeMessages.INFO, CategoryMessages.COMMANDS, false);
            });
        }
    }
}
