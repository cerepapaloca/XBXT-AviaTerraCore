package net.atcore.data;

import net.atcore.command.CommandManager;
import org.bukkit.configuration.ConfigurationSection;

public class FileCommands extends FileYaml {

    public FileCommands() {
        super("comandos", null);
    }

    @Override
    public void loadData() {
        ConfigurationSection section = fileConfiguration.getConfigurationSection("comandos");
        if (section != null) {
            CommandManager.COMMANDS.clear();
            section.getKeys(false).forEach(key -> {
                CommandManager.COMMANDS.put(key, fileConfiguration.getString("comandos." + key));
            });
            CommandManager.COMMANDS.putAll(CommandManager.COMMANDS_AVIA_TERRA);
        }
    }

    @Override
    public void saveData() {
        //fileConfiguration.set("premisos", CommandManager.COMMANDS);
        //saveConfig();
    }
}
