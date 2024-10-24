package net.atcore.Data;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.*;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class DataBaseRegister extends DataBaseMySql {
    @Override
    protected void reloadDatabase() {
        String sql = "SELECT name, uuidPremium, uuidCracked, ipRegister, ipLogin, isPremium, password, lastLoginDate, registerDate FROM register";
        HashMap<UUID, DataSession> sessions = new HashMap<>();
        LoginManager.getDataLogin().forEach(dataLogin -> sessions.put(dataLogin.getRegister().getUuidCracked(),dataLogin.getSession()));//rescata las sesiones
        LoginManager.clearDataLogin();//se limpia los datos

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String uuidPremium = resultSet.getString("uuidPremium");
                String uuidCracked = resultSet.getString("uuidCracked");
                String ipRegister = resultSet.getString("ipRegister");
                String ipLogin = resultSet.getString("ipLogin");
                int isPremium = resultSet.getInt("isPremium");
                String password = resultSet.getString("password");
                long lastLoginDate = resultSet.getLong("lastLoginDate");
                long registerDate = resultSet.getLong("registerDate");

                UUID uuid = UUID.fromString(uuidCracked);

                DataRegister dataRegister = new DataRegister(name, uuid,
                        uuidPremium != null ? UUID.fromString(uuidPremium) : null, isPremium == 1 ? StateLogins.PREMIUM : StateLogins.CRACKED,
                        true);
                dataRegister.setAddressRegister(InetAddress.getByName(ipRegister));
                dataRegister.setIp(InetAddress.getByName(ipLogin));
                dataRegister.setPasswordShaded(password);
                dataRegister.setLastLoginDate(lastLoginDate);
                dataRegister.setRegisterDate(registerDate);

                DataLogin dataLogin = LoginManager.addDataLogin(name, dataRegister);
                DataSession session = sessions.get(uuid);//Se obtiene las sesiones rescatas
                Player player = Bukkit.getPlayer(uuid);

                if (session == null || player == null) continue;

                //se modifica los datos de la session
                session.setAddress(InetAddress.getByName(ipLogin));
                session.setStartTimeLogin(lastLoginDate);
                dataLogin.setSession(session);//añade la sesión rescatada

                if (!LoginManager.checkLoginIn(player, true)){//revisa si son validas las sesiones
                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> GlobalUtils.kickPlayer(player, "Hay una discrepancia es tu session, vuelve a iniciar sessión"));
                }
            }
            sessions.clear();
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (!AviaTerraCore.isStarting()) sendMessageConsole("Registros Recargado", TypeMessages.SUCCESS);
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
                        reloadDatabase();
                        sendMessageConsole("DataBase Registro " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO, false);
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
            reloadDatabase();
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
        String sql = "INSERT INTO register (name, uuidPremium, uuidCracked, ipRegister, ipLogin, isPremium, password, lastLoginDate, registerDate) " +
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
            statement.setString(4, ipRegister.replace("/",""));
            statement.setString(5, ipLogin.replace("/",""));
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

    public static void updateLoginDate(String name ,long time){
        String sql = "UPDATE register SET lastLoginDate = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, time);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePassword(String name ,String password){
        String sql = "UPDATE register SET password = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAddress(String name ,String ip){
        String sql = "UPDATE register SET ipLogin = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
