package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.data.html.Email;
import net.atcore.data.md.Discord;
import net.atcore.data.sql.DataBaseBan;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.data.yml.*;
import net.atcore.data.yml.ymls.FliesCacheLimbo;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;

import java.util.HashSet;

import static net.atcore.utils.RegisterManager.register;

public class DataSection implements Section {

    public final static HashSet<File> FILES = new HashSet<>();
    public final static HashSet<DataBaseMySql> DATA_BASE = new HashSet<>();
    @Getter private static FliesCacheLimbo fliesCacheLimbo;
    @Getter private static ConfigFile configFile;

    @Override
    public void enable() {

        MessageFile messageFile = new MessageFile();
        ConfigFile configFile = new ConfigFile();
        new DataBaseBan();
        new DataBaseRegister();
        new CommandsFile();
        new Discord();
        new Email();
        DataSection.configFile = configFile;
        messageFile.reloadConfig(ActionInReloadYaml.LOAD);
        fliesCacheLimbo = new FliesCacheLimbo();
        fliesCacheLimbo.reloadConfigs();
        for (DataBaseMySql db : DATA_BASE) db.createTable();
        for (File fileYaml : FILES) fileYaml.loadData();
    }

    @Override
    public void disable() {
        //DataSection.getMySQLConnection().close();
        FILES.clear();
        DATA_BASE.clear();
        DataBaseBan.listDataBanByNAME.clear();
        DataBaseBan.listDataBanByIP.clear();
    }

    @Override
    public void reload() {
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            for (DataBaseMySql dataBaseMySql : DATA_BASE) dataBaseMySql.reload();
            for (File file : FILES) {
                if (file instanceof FileYaml yaml) {
                    yaml.reloadConfig(ActionInReloadYaml.LOAD);
                }else {
                    file.reloadFile();
                    file.loadData();
                }
                if (!AviaTerraCore.isStarting()) MessagesManager.sendMessageConsole(String.format("Archivo %s recargador exitosamente", file), TypeMessages.SUCCESS);
            }
        });
    }

    @Override
    public String getName() {
        return "Datos";
    }
}
