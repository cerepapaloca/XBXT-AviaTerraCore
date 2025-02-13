package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.utils.GlobalUtils;
import net.atcore.webapi.BaseApi;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;

public class PlayerStats extends BaseApi {

    public PlayerStats() {
        super("playerStats");
        isJson = true;
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        String name = request.getRequestPath().replaceFirst("/playerStats/", "");
        UUID uuid = GlobalUtils.getUUIDByName(name);
        File statsFolder = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats");
        return new File(statsFolder, uuid + ".json");
    }
}
