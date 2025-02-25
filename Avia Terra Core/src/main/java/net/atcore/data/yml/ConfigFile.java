package net.atcore.data.yml;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.FileYaml;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.DiscordBot;
import net.atcore.security.Login.ServerMode;
import net.atcore.security.check.BaseChecker;

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

        DiscordBot.consoleId = fileYaml.getString("canales-de-discord.console");
        DiscordBot.chatId = fileYaml.getString("canales-de-discord.chat-bridge", DiscordBot.chatId);
        DiscordBot.JoinAndLeave = fileYaml.getString("canales-de-discord.join-and-leave", DiscordBot.JoinAndLeave);

        for (BaseChecker<?> check : BaseChecker.REGISTERED_CHECKS) {
            check.enabled = fileYaml.getBoolean("checker." + check.getClass().getSimpleName(), check.enabled);
        }

        Config.setExpirationSession(fileYaml.getLong("expiration-session", Config.getExpirationSession()));
        Config.setLevelModerationChat(fileYaml.getDouble("level-moderation-chat", Config.getLevelModerationChat()));
        Config.setPurgeTagRange(fileYaml.getLong("purge-tag-range", Config.getPurgeTagRange()));
        Config.setCheckBanByIp(fileYaml.getBoolean("check-ban-by-ip", Config.isCheckBanByIp()));
        Config.setServerMode(ServerMode.valueOf(fileYaml.getString("server-mode", Config.getServerMode().name().toLowerCase()).toUpperCase()));
        Config.setChaceDupeFrame(fileYaml.getDouble("chace-dupe-frame", Config.getChaceDupeFrame()));
        Config.setPasswordSSL(fileYaml.getString("password-ssl", Config.getPasswordSSL()));
        AviaTerraCore.setActiveTime(fileYaml.getLong("active-time", 0));
        saveData(); // Se guarda por si hay una liena faltante en la configuración
    }

    @Override
    public void saveData() {
        for (CategoryMessages messages : CategoryMessages.values()) {
            fileYaml.set("canales-de-discord." + messages.name().toLowerCase(), messages.getIdChannel());
        }
        fileYaml.set("canales-de-discord.chat-bridge", DiscordBot.chatId);
        fileYaml.set("canales-de-discord.join-and-leave", DiscordBot.JoinAndLeave);

        for (BaseChecker<?> check : BaseChecker.REGISTERED_CHECKS) {
            fileYaml.set("checker." + check.getClass().getSimpleName(), check.enabled);
        }

        fileYaml.set("expiration-session", Config.getExpirationSession());
        fileYaml.set("level-moderation-chat", Config.getLevelModerationChat());
        fileYaml.set("purge-tag-range", Config.getPurgeTagRange());
        fileYaml.set("check-ban-by-ip", Config.isCheckBanByIp());
        fileYaml.set("server-mode", Config.getServerMode().name().toLowerCase());
        fileYaml.set("chace-dupe-frame", Config.getChaceDupeFrame());
        fileYaml.set("password-ssl", Config.getPasswordSSL());

        saveConfig();
    }

    public void saveActiveTime(){
        fileYaml.set("active-time", AviaTerraCore.getActiveTime());
        saveConfig();
    }
}
