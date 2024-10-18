package net.atcore.Data;

import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.Ban.DataBan;
import net.atcore.Utils.GlobalUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class RegisterDataBase extends DataBaseMySql {
    @Override
    protected void reloadDatabase() {

    }

    @Override
    protected void createTable() {
        String checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = ? AND table_name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(checkTableSQL)) {//revisa si la tabla existe
            stmt.setString(1, "aviaterra");
            stmt.setString(2, "register");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        sendMessageConsole("DataBase Registro " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO);
                        reloadDatabase();
                        return;//si existe, se detiene
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String createTableSQL = "CREATE TABLE IF NOT EXISTS register (" +
                "name VARCHAR(36) NOT NULL, " +
                "uuidPremium VARCHAR(100), " +
                "uuidCracked VARCHAR(100) NOT NULL, " +
                "ipRegister VARCHAR(45), " +
                "ipLogin VARCHAR(45), " +
                "isPremium TINYINT NOT NULL, " +
                "password VARCHAR(100), " +
                "lastLoginDate BIGINT NOT NULL, " +
                "registerDate BIGINT NOT NULL, " +
                "PRIMARY KEY (name)," +
                "UNIQUE (name)" +
                ");";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            sendMessageConsole("DataBase Registro " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addRegister(String name, String uuidPremium,
                                   String uuidCracked, String ipRegister,
                                   String ipLogin, Boolean isPremium,
                                   String password, long lastLoginDate,
                                   long registerDate) {
        String sql = "INSERT INTO bans (name, uuidPremium, uuidCracked, ipRegister, ipLogin, isPremium, password, lastLoginDate, registerDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), uuidPremium = VALUES(uuidPremium), uuidCracked = VALUES(uuidCracked), ipRegister = VALUES(ipRegister), " +
                "ipLogin = VALUES(ipLogin), isPremium = VALUES(isPremium), password = VALUES(password), lastLoginDate = VALUES(lastLoginDate)," +
                "registerDate = VALUES(registerDate) ";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, uuidPremium);
            statement.setString(3, uuidCracked);
            statement.setString(4, ipRegister);
            statement.setString(5, ipLogin);
            statement.setInt(6, isPremium ? 1 : 0);
            statement.setString(7, password);
            statement.setLong(8, lastLoginDate);
            statement.setLong(9, registerDate);
            statement.executeUpdate();
            /*sendMessageConsole("el jugador <|" + name + "|> fue baneado de <|" + context + "|> durante <|" +
                    tiempoDeBaneo + "|> por el jugador <|" + author +
                    "|> y la razón es <|" + reason + "|> ", TypeMessages.SUCCESS, CategoryMessages.BAN);*/
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
