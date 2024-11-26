package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;

import java.util.HashSet;

import static net.atcore.utils.RegisterManager.register;

public class DataSection implements Section {

    @Getter private final static HashSet<DataBaseMySql> dataBases = new HashSet<>();
    @Getter private final static HashSet<FileYaml> fileYaml = new HashSet<>();
    @Getter private static DataBaseMySql mySQLConnection;

    @Override
    public void enable() {
        //register(mySQLConnection = new DataBaseBan());
        //register(new DataBaseRegister());
        register(new FileCommands());
        register(new FileConfig());
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
            for (FileYaml filePermission : fileYaml) filePermission.reloadConfig();
        });
    }

    @Override
    public String getName() {
        return "Datos";
    }
}
