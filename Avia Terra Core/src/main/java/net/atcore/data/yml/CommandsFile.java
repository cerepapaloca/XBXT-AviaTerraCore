package net.atcore.data.yml;

import net.atcore.command.CommandManager;
import net.atcore.command.CommandVisibility;
import net.atcore.data.FileYaml;
import net.atcore.messages.MessagesManager;
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
                String visibility = fileYaml.getString("comandos." + key);
                if (visibility != null){
                    try {
                        CommandManager.COMMANDS.put(key, CommandVisibility.valueOf(visibility.toUpperCase()));
                    }catch (Exception e){
                        MessagesManager.sendWaringException("Invalid visibility: " + visibility, e);
                    }
                }
            });
        }//TODO: Arreglar esto que se buega con los permisos ** y cuando hay reload
    }

    @Override
    public void saveData() {

    }
}
