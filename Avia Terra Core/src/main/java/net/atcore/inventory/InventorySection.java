package net.atcore.inventory;

import net.atcore.Section;
import net.atcore.command.Commnads.seeInventoryCommand;

import java.io.Serializable;

import static net.atcore.utils.RegisterManager.register;

public class InventorySection implements Section {

    @Override
    public void enable() {
        register(new seeInventoryCommand());
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "";
    }
}
