package net.atcore.data.yml;

import net.atcore.Config;
import net.atcore.data.FileYaml;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.ConsoleDiscord;
import net.atcore.security.Login.ServerMode;

public class ConfigFile extends FileYaml {

    public ConfigFile() {
        super("config", null, true, true);
    }

    @Override
    public void loadData() {
        loadConfig();
        for (CategoryMessages messages : CategoryMessages.values()) {
            String message = fileYaml.getString("canales-de-discord." + messages.name().toLowerCase());
            messages.setIdChannel(message);
        }
        ConsoleDiscord.consoleId = fileYaml.getString("canales-de-discord.console");
        Config.setAntiBot(fileYaml.getBoolean("anti-bot", Config.isAntiBot()));
        Config.setExpirationSession(fileYaml.getLong("expiration-session", Config.getExpirationSession()));
        Config.setLevelModerationChat(fileYaml.getDouble("level-moderation-chat", Config.getLevelModerationChat()));
        Config.setCheckAntiOp(fileYaml.getBoolean("check-anti-op", Config.isCheckAntiOp()));
        Config.setPurgeTagRange(fileYaml.getLong("purge-tag-range", Config.getPurgeTagRange()));
        Config.setCheckAntiIllegalItems(fileYaml.getBoolean("check-anti-illegal-items", Config.isCheckAntiIllegalItems()));
        Config.setCheckBanByIp(fileYaml.getBoolean("check-ban-by-ip", Config.isCheckBanByIp()));
        Config.setServerMode(ServerMode.valueOf(fileYaml.getString("server-mode", Config.getServerMode().name().toLowerCase()).toUpperCase()));
        saveData(); // Se guarda por si hay una liena faltante en la configuración
    }

    @Override
    public void saveData() {
        for (CategoryMessages messages : CategoryMessages.values()) {
            fileYaml.set("canales-de-discord." + messages.name().toLowerCase(), messages.getIdChannel());
        }
        fileYaml.set("anti-bot", Config.isAntiBot());
        fileYaml.set("expiration-session", Config.getExpirationSession());
        fileYaml.set("level-moderation-chat", Config.getLevelModerationChat());
        fileYaml.set("check-anti-op", Config.isCheckAntiOp());
        fileYaml.set("purge-tag-range", Config.getPurgeTagRange());
        fileYaml.set("check-anti-illegal-items", Config.isCheckAntiIllegalItems());
        fileYaml.set("check-ban-by-ip", Config.isCheckBanByIp());
        fileYaml.set("server-mode", Config.getServerMode().name().toLowerCase());
        saveConfig();
    }
}
