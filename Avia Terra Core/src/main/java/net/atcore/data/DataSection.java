package net.atcore.data;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.data.html.Email;
import net.atcore.data.md.Discord;
import net.atcore.data.sql.DataBaseBan;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.data.txt.BroadcastMessageFile;
import net.atcore.data.txt.MOTDFile;
import net.atcore.data.yml.CacheVoteFile;
import net.atcore.data.yml.CommandsFile;
import net.atcore.data.yml.ConfigFile;
import net.atcore.data.yml.ymls.CacheLimboFlies;
import net.atcore.data.yml.ymls.MapArtsFiles;
import net.atcore.data.yml.ymls.MessagesLocaleFile;
import net.atcore.data.yml.ymls.PlayersDataFiles;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.BanManager;
import net.atcore.utils.AviaTerraScheduler;
import org.bukkit.Bukkit;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

import static net.atcore.messages.MessagesManager.*;

public class DataSection implements Section {

    public final static HashSet<File> FILES = new HashSet<>();
    public final static HashSet<DataBaseMySql> DATA_BASE = new HashSet<>();
    @Getter private static CacheLimboFlies cacheLimboFlies;
    @Getter private static PlayersDataFiles playersDataFiles;
    @Getter @Setter
    private static ConfigFile configFile;
    @Getter private static CacheVoteFile cacheVoteFile;
    @Getter private static MessagesLocaleFile messagesLocaleFile;
    @Getter private static MapArtsFiles mapArtsFiles;
    @Getter private static boolean isDataBaseActive = false;
    @Getter private static DataBaseBan databaseBan;

    @Override
    public void enable() {
        try (Connection connection = DataBaseMySql.getConnection()) {
            if (connection == null) throw new SQLException();
            databaseBan = new DataBaseBan();// TODO: Cambiar sus métodos a statics
            new DataBaseRegister();
            isDataBaseActive = true;
        }catch (Exception e) {
            isDataBaseActive = false;
            logConsole("Base de Datos" + TypeMessages.ERROR.getMainColor() + " Fail", TypeMessages.INFO, CategoryMessages.PRIVATE, false);
            MessagesManager.sendWaringException("Error al iniciar la base de datos", e);
        }
        new CommandsFile();
        new Discord();
        new Email();
        new MOTDFile();
        new BroadcastMessageFile();

        messagesLocaleFile = new MessagesLocaleFile();
        playersDataFiles = new PlayersDataFiles();
        cacheVoteFile = new CacheVoteFile();
        cacheLimboFlies = new CacheLimboFlies();
        mapArtsFiles = new MapArtsFiles();
        //for (File fileYaml : FILES) fileYaml.loadData();
    }

    @Override
    public void disable() {
        //DataSection.getMySQLConnection().close();
        FILES.clear();
        DATA_BASE.clear();
        BanManager.listDataBanByNAME.clear();
        BanManager.listDataBanByIP.clear();
    }

    @Override
    public void reload() {
        AviaTerraScheduler.enqueueTaskAsynchronously(true, () -> {
            for (DataBaseMySql dataBaseMySql : DATA_BASE) {
                try {
                    dataBaseMySql.reload();
                } catch (UnknownHostException | SQLException e) {
                    sendErrorException("Error al reloadar el sistema", e);
                }
            }
            for (File file : FILES) {
                if (file instanceof FileYaml yaml) {
                    yaml.reloadConfig();
                }else {
                    file.reloadFile();
                    file.loadData();
                }
                if (!AviaTerraCore.isStarting()) MessagesManager.logConsole(String.format("Archivo %s recargador exitosamente", file), TypeMessages.SUCCESS);
            }
            /*cacheLimboFlies.reloadConfigs();
            mapArtsFiles.reloadConfigs();
            messagesLocaleFile.reloadConfigs();
            playersDataFiles.reloadConfigs();*/
            MessagesManager.logConsole("Archivos recargador exitosamente", TypeMessages.SUCCESS);
        });
    }

    @Override
    public String getName() {
        return "Datos";
    }

    @Override
    public boolean isImportant() {
        return true;
    }
}
