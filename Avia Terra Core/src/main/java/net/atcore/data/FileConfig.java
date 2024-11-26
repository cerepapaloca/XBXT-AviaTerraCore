package net.atcore.data;

import net.atcore.messages.CategoryMessages;
import net.atcore.messages.ConsoleDiscord;

public class FileConfig extends FileYaml {

    public FileConfig() {
        super("config", null);
    }

    @Override
    public void loadData() {
        for (CategoryMessages messages : CategoryMessages.values()) {
            String message = fileConfiguration.getString("canales-de-discord." + messages.name().toLowerCase());
            messages.setIdChannel(message);
        }
        ConsoleDiscord.consoleId = fileConfiguration.getString("canales-de-discord.console");
    }

    @Override
    public void saveData() {

    }
}
