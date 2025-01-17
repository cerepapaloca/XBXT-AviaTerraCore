package net.atcore.data.yml;

import net.atcore.command.CommandManager;
import net.atcore.data.FileYaml;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class CommandsFile extends FileYaml {

    public CommandsFile() {
        super("comandos", null, true, true);
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
        }
    }

    @Override
    public void saveData() {

    }
}
