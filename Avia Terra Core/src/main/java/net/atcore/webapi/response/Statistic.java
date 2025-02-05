package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
import net.atcore.webapi.BaseApi;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Statistic extends BaseApi {

    public Statistic() {
        super("statistic");
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        Map<String, Object> data = new HashMap<>();
        data.put("uniqueUsers", LoginManager.getDataLogin().size());
        data.put("activeTime", AviaTerraCore.getActiveTime());
        data.put("onlinePlayer", Bukkit.getOnlinePlayers().size());
        data.put("maxPlayers", Bukkit.getMaxPlayers());
        data.put("frameDupe", Config.getChaceDupeFrame());

        long size = 0;
        for (World world : Bukkit.getWorlds()) {
            size += GlobalUtils.getFolderSize(world.getWorldFolder());
        }
        data.put("sizeWorlds", size);

        String[] jugadores = Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new);
        data.put("namesPlayers", jugadores);
        return data;
    }
}
