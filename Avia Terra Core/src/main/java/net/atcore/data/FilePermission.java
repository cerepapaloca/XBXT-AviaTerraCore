package net.atcore.data;

import net.atcore.command.CommandManager;
import org.bukkit.configuration.ConfigurationSection;

public class FilePermission extends FileYaml {

    public FilePermission() {
        super("permisos", null);
    }

    @Override
    public void loadData() {
        ConfigurationSection section = fileConfiguration.getConfigurationSection("premisos");
        if (section != null) {
            section.getKeys(false).forEach(key -> {
                CommandManager.COMMANDS.put(key, section.getString(key));
            });
        }
    }

    @Override
    public void saveData() {

    }
}
