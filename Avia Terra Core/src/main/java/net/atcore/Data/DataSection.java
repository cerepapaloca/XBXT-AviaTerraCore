package net.atcore.Data;

import lombok.Getter;
import net.atcore.Section;

public class DataSection implements Section {
    @Getter
    private static MySQLConnection mySQLConnection;

    @Override
    public void enable() {
        mySQLConnection = new MySQLConnection("localhost", "AviaTerra", "root", "");//los datos para conectar se a la base de dato
    }

    @Override
    public void disable() {
        DataSection.getMySQLConnection().close();
    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "";
    }
}
