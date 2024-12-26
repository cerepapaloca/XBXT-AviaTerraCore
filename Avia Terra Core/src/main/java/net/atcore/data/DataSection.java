package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.data.sql.DataBaseBan;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.data.yml.CommandsFile;
import net.atcore.data.yml.ConfigFile;
import net.atcore.data.yml.MessageFile;
import net.atcore.data.yml.FliesCacheLimbo;

import java.util.HashSet;

import static net.atcore.utils.RegisterManager.register;

public class DataSection implements Section {

    @Getter private final static HashSet<DataBaseMySql> dataBases = new HashSet<>();
    @Getter private final static HashSet<FileYaml> fileYaml = new HashSet<>();
    @Getter private static DataBaseMySql mySQLConnection;
    @Getter private static FliesCacheLimbo fliesCacheLimbo;
    @Getter private static ConfigFile configFile;

    @Override
    public void enable() {
        register(mySQLConnection = new DataBaseBan());
        MessageFile messageFile = new MessageFile();
        ConfigFile configFile = new ConfigFile();
        register(new DataBaseRegister());
        register(new CommandsFile());
        register(messageFile);
        register(configFile);
        DataSection.configFile = configFile;
        messageFile.reloadConfig(ActionInReloadYaml.LOAD);
        fliesCacheLimbo = new FliesCacheLimbo();
        fliesCacheLimbo.reloadConfigs();
        for (DataBaseMySql db : dataBases) db.createTable();
        for (FileYaml fileYaml : fileYaml) fileYaml.loadData();
    }

    @Override
    public void disable() {
        //DataSection.getMySQLConnection().close();
        DataBaseBan.listDataBanByNAME.clear();
        DataBaseBan.listDataBanByIP.clear();
    }

    @Override
    public void reloadConfig() {
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            for (DataBaseMySql dataBaseMySql : dataBases) dataBaseMySql.reloadDatabase();
            for (FileYaml file : fileYaml) file.reloadConfig(ActionInReloadYaml.LOAD);
        });
    }

    @Override
    public String getName() {
        return "Datos";
    }
}
