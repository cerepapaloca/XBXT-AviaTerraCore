package net.atcore.data;

import net.atcore.AviaTerraCore;
import net.atcore.Reloadable;
import net.atcore.data.yml.ConfigFile;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;


import static net.atcore.messages.MessagesManager.logConsole;

import net.atcore.messages.TypeMessages;

public abstract class DataBaseMySql implements Reloadable {
    private static Connection connection;

    private static final String HOST = "192.168.1.55";//localhost
    private static final String PORT = "3306";
    private static final String DATABASE = "xbxt";
    public static final String USER;
    public static final String PASSWORD;

    static {
        USER = Objects.requireNonNullElseGet(DataSection.getConfigFile(),  () -> {
            ConfigFile configFile = new ConfigFile();
            DataSection.setConfigFile(configFile);
            return configFile;
        }).getFileYaml().getString("mysql.username");
        PASSWORD = DataSection.getConfigFile().getFileYaml().getString("mysql.password");
    }

    /**
     * No usar este method para tener la conexión con la base de datos
     * usen esta {@link #getConnection}
     */

    public DataBaseMySql() {
        DataSection.DATA_BASE.add(this);
    }

    private static void connect() {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
        try {
            connection = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            MessagesManager.sendErrorException("Error al conectar con la base de datos", e);
        }
    }

    /**
     * Crea una connexion con la base de datos si no está conectado e
     * intentar usar este method en un hilo aparte del servidor porque
     * bloquea el hilo principal de servidor un ejemplo de como
     * pueden crear un hilo aparte
     * <blockquote><pre>
     *     AviaTerraCore.enqueueTaskAsynchronously(() -> {
     *          connection = getConnection()
     *     });
     * </pre></blockquote>
     *
     * @return Te devuelve {@link #connection} que contiene todas las tablas
     * de la base de datos
     */

    protected static Connection getConnection() throws SQLException {
        if (Bukkit.isPrimaryThread() && !AviaTerraCore.isStarting() && !AviaTerraCore.isStopping()){
            throw new IllegalThreadStateException("No usar el hilo principal para la base de datos");
        }
        if (connection == null || connection.isClosed()) connect();
        return connection;
    }

    protected abstract void createTable();

}
