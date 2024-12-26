package net.atcore.data.yml;

import net.atcore.command.CommandManager;
import net.atcore.data.FileYaml;
import org.bukkit.configuration.ConfigurationSection;

public class CommandsFile extends FileYaml {

    public CommandsFile() {
        super("comandos", null, true);
    }

    @Override
    public void loadData() {
        loadConfig();
        ConfigurationSection section = fileYaml.getConfigurationSection("comandos");
        if (section != null) {
            CommandManager.COMMANDS.clear();
            section.getKeys(false).forEach(key -> {
                CommandManager.COMMANDS.put(key, fileYaml.getString("comandos." + key));
            });
            CommandManager.COMMANDS.putAll(CommandManager.COMMANDS_AVIA_TERRA);
        }
    }

    @Override
    public void saveData() {

    }
}
