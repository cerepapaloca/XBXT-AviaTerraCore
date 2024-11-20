package net.atcore.data;

import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import static net.atcore.messages.MessagesManager.sendMessageConsole;

import net.atcore.messages.TypeMessages;

public abstract class DataBaseMySql {
    private static Connection connection;

    private static final String HOST = "147.185.221.20";
    private static final String PORT = "2149";
    private static final String DATABASE = "AviaTerra";
    private static final String USER = "azurex";
    private static final String PASSWORD = "AdeptusAzurex1313#waos";

    /**
     * No usar este method para tener la conexión con la base de datos
     * usen esta {@link #getConnection}
     */

    private static void connect() {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
        try {
            connection = DriverManager.getConnection(url, USER, PASSWORD);
            if (!AviaTerraCore.isStarting()) sendMessageConsole("Conexión a MySQL establecida", TypeMessages.SUCCESS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Para cerrar la connection, pero no es muy util porque la connexion
     * se cierra automáticamente
     */

    protected void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Crea una connexion con la base de datos si no está conectado y
     * intentar usar este method en un hilo aparte del servidor porque lo
     * puede bloquear el hilo principal de servidor un ejemplo de como
     * pueden crear un hilo aparte
     * <blockquote><pre>
     *     Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
     *          connection = getConnection()
     *     });
     * </pre></blockquote>
     * @return Te devuelve {@link #connection} que contiene todas las tablas
     * de la base de datos
     */

    protected static Connection getConnection() throws SQLException {
        if (Bukkit.isPrimaryThread() && !AviaTerraCore.isStarting()){
            throw new IllegalThreadStateException("No usar el hilo principal para la base de datos");
        }
        if (connection == null || connection.isClosed()) {
            if (!AviaTerraCore.isStarting()) sendMessageConsole("Conexión perdida Reconectando...", TypeMessages.WARNING);
            connect();
        }
        return connection;
    }

    public abstract void reloadDatabase();

    protected abstract void createTable();

}
