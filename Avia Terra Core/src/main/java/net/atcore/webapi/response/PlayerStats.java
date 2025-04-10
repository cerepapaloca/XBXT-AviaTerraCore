package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LoginData;
import net.atcore.webapi.BaseApi;
import net.atcore.webapi.ContentType;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;

public class PlayerStats extends BaseApi {

    public PlayerStats() {
        super("playerStats", ContentType.APPLICATION_JSON);
        toJson = false;
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        String name = request.getRequestPath().replaceFirst("/playerStats/", "");
        LoginData data = LoginManager.getDataLogin(name);
        UUID uuid;
        if (data == null) return null;
        if (data.isBedrockPlayer()) {
            uuid = data.getRegister().getUuidBedrock();
        }else {
            uuid = data.getRegister().getUuidCracked();
        }
        File statsFolder = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats");
        return new File(statsFolder, uuid + ".json");
    }
}
