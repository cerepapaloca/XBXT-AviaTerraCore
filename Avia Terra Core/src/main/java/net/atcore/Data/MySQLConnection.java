package net.atcore.Data;

import net.atcore.Messages.TypeMessages;

import java.sql.*;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class MySQLConnection {
    private static Connection connection;

    private static String host;
    private static String database;
    private static String user;
    private static String password;

    public MySQLConnection(String host, String database, String user, String password) {
        MySQLConnection.host = host;
        MySQLConnection.database = database;
        MySQLConnection.user = user;
        MySQLConnection.password = password;
    }

    /**
     * No usar este method para tener la conexión con la base de datos
     * usen esta {@link #getConnection}
     */

    private static void connect() {
        String url = "jdbc:mysql://" + host + "/" + database;
        try {
            connection = DriverManager.getConnection(url, user, password);
            sendMessageConsole("Conexión a MySQL establecida", TypeMessages.SUCCESS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Para cerrar la connection, pero no es muy util porque la connexion
     * se cierra automáticamente
     */

    public void close() {
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

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                sendMessageConsole("Conexión perdida Reconectando...", TypeMessages.WARNING);
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

}
