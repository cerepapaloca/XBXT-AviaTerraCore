package net.atcore.Data;

import lombok.Getter;
import net.atcore.Section;

import java.util.HashSet;

import static net.atcore.Utils.RegisterManager.register;

public class DataSection implements Section {

    @Getter private final static HashSet<DataBaseMySql> dataBases = new HashSet<>();
    @Getter private static DataBaseMySql mySQLConnection;

    @Override
    public void enable() {
        register(mySQLConnection = new BanDataBase());
        for (DataBaseMySql db : dataBases) db.createTable();
    }

    @Override
    public void disable() {
        DataSection.getMySQLConnection().close();
    }

    @Override
    public void reloadConfig() {
        for (DataBaseMySql dataBaseMySql : dataBases) dataBaseMySql.reloadDatabase();
    }

    @Override
    public String getName() {
        return "Data";
    }
}
