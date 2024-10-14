package net.atcore.Data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import org.bukkit.Bukkit;

import java.util.HashSet;

import static net.atcore.Utils.RegisterManager.register;

public class DataSection implements Section {

    @Getter private final static HashSet<DataBaseMySql> dataBases = new HashSet<>();
    @Getter private static DataBaseMySql mySQLConnection;

    @Override
    public void enable() {
        register(mySQLConnection = new BanDataBase());
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            for (DataBaseMySql db : dataBases) db.createTable();
        });
    }

    @Override
    public void disable() {
        DataSection.getMySQLConnection().close();
        BanDataBase.listDataBanByNAME.clear();
        BanDataBase.listDataBanByIP.clear();
    }

    @Override
    public void reloadConfig() {
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            for (DataBaseMySql dataBaseMySql : dataBases) dataBaseMySql.reloadDatabase();
        });
    }

    @Override
    public String getName() {
        return "Data";
    }
}
