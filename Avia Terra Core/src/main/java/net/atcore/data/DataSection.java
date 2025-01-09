package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.data.html.Email;
import net.atcore.data.md.Discord;
import net.atcore.data.sql.DataBaseBan;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.data.txt.BroadcastMessageFile;
import net.atcore.data.txt.MOTDFile;
import net.atcore.data.yml.*;
import net.atcore.data.yml.ymls.CacheLimboFlies;
import net.atcore.data.yml.ymls.PlayersData;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;

import java.util.HashSet;

public class DataSection implements Section {

    public final static HashSet<File> FILES = new HashSet<>();
    public final static HashSet<DataBaseMySql> DATA_BASE = new HashSet<>();
    @Getter private static CacheLimboFlies cacheLimboFlies;
    @Getter private static PlayersData playersData;
    @Getter private static ConfigFile configFile;
    @Getter private static CacheVoteFile cacheVoteFile;

    @Override
    public void enable() {
        MessageFile messageFile = new MessageFile();
        messageFile.reloadConfig();
        ConfigFile configFile = new ConfigFile();
        new DataBaseBan();
        new DataBaseRegister();
        new CommandsFile();
        new Discord();
        new Email();
        new MOTDFile();
        new BroadcastMessageFile();
        DataSection.configFile = configFile;
        playersData = new PlayersData();
        cacheVoteFile = new CacheVoteFile();
        cacheLimboFlies = new CacheLimboFlies();
        cacheLimboFlies.configure();
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
        AviaTerraCore.enqueueTaskAsynchronously(true, () -> {
            for (DataBaseMySql dataBaseMySql : DATA_BASE) dataBaseMySql.reload();
            for (File file : FILES) {
                if (file instanceof FileYaml yaml) {
                    yaml.reloadConfig();
                }else {
                    file.reloadFile();
                    file.loadData();
                }
                if (!AviaTerraCore.isStarting()) MessagesManager.sendMessageConsole(String.format("Archivo %s recargador exitosamente", file), MessagesType.SUCCESS);
            }
            cacheLimboFlies.reloadConfigs();
            MessagesManager.sendMessageConsole("La cache del limbo fue recarga exitosamente", MessagesType.SUCCESS);
        });
    }

    @Override
    public String getName() {
        return "Datos";
    }
}
