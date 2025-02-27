package net.atcore.webapi;

import net.atcore.Section;
import net.atcore.data.yml.MapArtFile;
import net.atcore.webapi.response.*;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ApiSection implements Section {


    @Override
    public void enable() {
        new ApiHandler();
        new Statistic();
        new Commands();
        new PlayerStats();
        new MapArtData();
        new MapArtImg();
    }

    @Override
    public void disable() {
        if (ApiHandler.server != null) {
            ApiHandler.server.stop();
        }
    }

    @Override
    public String getName() {
        return "API website";
    }

    @Override
    public void reload() {
        if (ApiHandler.server != null) {
            ApiHandler.server.stop();
        }
        new ApiHandler();
    }
}
