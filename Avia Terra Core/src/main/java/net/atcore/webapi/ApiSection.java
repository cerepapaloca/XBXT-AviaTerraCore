package net.atcore.webapi;

import net.atcore.Section;
import net.atcore.webapi.response.Statistic;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ApiSection implements Section {


    @Override
    public void enable() {
        new ApiHandler();
        new Statistic();
    }

    @Override
    public void disable() {
        ApiHandler.server.stop();
    }

    @Override
    public String getName() {
        return "API website";
    }

    @Override
    public void reload() {
        ApiHandler.server.stop();
        new ApiHandler();
    }
}
