package net.atcore.webapi;

import net.atcore.Section;
import net.atcore.webapi.response.Commands;
import net.atcore.webapi.response.Statistic;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ApiSection implements Section {


    @Override
    public void enable() {
        new ApiHandler();
        new Statistic();
        new Commands();
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
