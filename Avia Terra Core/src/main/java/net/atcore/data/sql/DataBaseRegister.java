package net.atcore.data.sql;

import net.atcore.AviaTerraCore;
import net.atcore.data.DataBaseMySql;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.model.RegisterData;
import net.atcore.security.Login.model.SessionData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.logConsole;

public class DataBaseRegister extends DataBaseMySql {
    @Override
    public void reload() {
        String sql = "SELECT name, uuidBedrock, uuidPremium, uuidCracked, ipRegister, ipLogin, stateAccount, password, lastLoginDate, registerDate, gmail, discord FROM register";
        HashMap<UUID, SessionData> sessions = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            for (LoginData loginData : LoginManager.getDataLogin()) sessions.put(loginData.getRegister().getUuidCracked(), loginData.getSession());
            LoginManager.clearDataLogin();//se limpia los datos
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String uuidPremium = resultSet.getString("uuidPremium");
                String uuidCracked = resultSet.getString("uuidCracked");
                String uuidBedrock = resultSet.getString("uuidBedrock");
                String ipRegister = resultSet.getString("ipRegister");
                String ipLogin = resultSet.getString("ipLogin");
                String stateAccount = resultSet.getString("stateAccount");
                String password = resultSet.getString("password");
                long lastLoginDate = resultSet.getLong("lastLoginDate");
                long registerDate = resultSet.getLong("registerDate");
                String gmail = resultSet.getString("gmail");
                String discord = resultSet.getString("discord");

                UUID uuid = UUID.fromString(uuidCracked);

                RegisterData registerData = new RegisterData(
                        name,
                        uuid,
                        uuidPremium != null ? UUID.fromString(uuidPremium) : null,
                        StateLogins.valueOf(stateAccount.toUpperCase()),
                        false
                );
                registerData.setUuidBedrock(uuidBedrock != null ? UUID.fromString(uuidBedrock) : null);
                registerData.setRegisterAddress(InetAddress.getByName(ipRegister));
                registerData.setLastAddress(InetAddress.getByName(ipLogin));
                registerData.setPasswordShaded(password);
                registerData.setLastLoginDate(lastLoginDate);
                registerData.setRegisterDate(registerDate);
                registerData.setMail(gmail);
                registerData.setDiscord(discord);

                LoginData loginData = LoginManager.addDataLogin(name, registerData);
                SessionData session = sessions.get(uuid);// Se obtiene las sesiones para que no se tenga que loguear de nuevo
                Player player = Bukkit.getPlayer(uuid);

                if (session == null || player == null) continue;

                // Se modifica los datos de la session
                session.setAddress(InetAddress.getByName(ipLogin));
                session.setStartTimeLogin(lastLoginDate);
                loginData.setSession(session);// añade la sesión rescatada
                if (!LoginManager.checkLogin(player)){// Revisa si son validas las sesiones
                    GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_SESSION_ERROR);
                }
            }
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        sessions.clear();

        if (!AviaTerraCore.isStarting()) MessagesManager.logConsole("Registros Recargado", TypeMessages.SUCCESS);
    }

    @Override
    protected void createTable() {

        try (Connection connection = getConnection()) {//revisa si la tabla existe
            DatabaseMetaData dbMetaData = connection.getMetaData();
            try (ResultSet resultSet = dbMetaData.getTables(null, null, "register", null)) {
                if (resultSet.next()){
                    reload();
                    MessagesManager.logConsole("DataBase Registro " + TypeMessages.SUCCESS.getMainColor() + "Ok", TypeMessages.INFO, false);
                    return;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String createTableSQL = "CREATE TABLE IF NOT EXISTS register (" +
                "name VARCHAR(36) NOT NULL, " +
                "uuidBedrock VARCHAR(100), " +
                "uuidPremium VARCHAR(100), " +
                "uuidCracked VARCHAR(100) NOT NULL, " +
                "ipRegister VARCHAR(45), " +
                "ipLogin VARCHAR(45), " +
                "stateAccount VARCHAR(45) NOT NULL, " +
                "password VARCHAR(100), " +
                "lastLoginDate BIGINT NOT NULL, " +
                "registerDate BIGINT NOT NULL, " +
                "gmail VARCHAR(100)," +
                "discord VARCHAR(100)," +
                "PRIMARY KEY (name)," +
                "UNIQUE (name)" +
                ");";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            reload();
            MessagesManager.logConsole("DataBase Registro " + TypeMessages.SUCCESS.getMainColor()  + "Creada", TypeMessages.INFO, false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addRegister(String name,
                                   String uuidCracked,
                                   String uuidPremium,
                                   String ipRegister,
                                   String ipLogin,
                                   StateLogins state,
                                   String password,
                                   long lastLoginDate,
                                   long registerDate
    ) {
        String sql = "INSERT INTO register (name, uuidBedrock, uuidPremium, uuidCracked, ipRegister, ipLogin, stateAccount, password, lastLoginDate, registerDate, gmail, discord) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), uuidBedrock = VALUES(uuidBedrock), uuidPremium = VALUES(uuidPremium), uuidCracked = VALUES(uuidCracked), ipRegister = VALUES(ipRegister), " +
                "ipLogin = VALUES(ipLogin), stateAccount = VALUES(stateAccount), password = VALUES(password), lastLoginDate = VALUES(lastLoginDate)," +
                "registerDate = VALUES(registerDate), gmail = VALUES(gmail), discord = VALUES(discord) ";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, null);
            statement.setString(3, uuidPremium);
            statement.setString(4, uuidCracked);
            statement.setString(5, ipRegister.replace("/",""));
            statement.setString(6, ipLogin.replace("/",""));
            statement.setString(7, state.name());
            statement.setString(8, password);
            statement.setLong(9, lastLoginDate);
            statement.setLong(10, registerDate);
            statement.setString(11, null);
            statement.setString(12, null);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isExistRegister(Player player) {
        String query = "SELECT EXISTS(SELECT 1 FROM register WHERE uuidCracked = ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, GlobalUtils.getRealUUID(player).toString());
            ResultSet rs = stmt.executeQuery();
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            logConsole(String.format("Error al conocer el nombre del jugador %s", player.getName()), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
        }
        return false;
    }

    public static boolean updateLoginDate(String name ,long time){
        String sql = "UPDATE register SET lastLoginDate = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, time);
            stmt.setString(2, name);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logConsole(String.format("No se pudo actualizar la <|fecha del ultimo login|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static boolean updateGmail(String name ,String gmail){
        String sql = "UPDATE register SET gmail = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, gmail);
            stmt.setString(2, name);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
                logConsole(String.format("No se pudo actualizar el <|gmail|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static boolean updateDiscord(String name ,String discord){
        String sql = "UPDATE register SET discord = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, discord);
            stmt.setString(2, name);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logConsole(String.format("No se pudo actualizar el <|discord|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static boolean updatePassword(String name, String password){
        String sql = "UPDATE register SET password = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, name);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logConsole(String.format("No se pudo actualizar la <|contraseña|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static boolean updateAddress(String name, String ip){
        String sql = "UPDATE register SET ipLogin = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setString(2, name);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logConsole(String.format("No se pudo actualizar la <|ip|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static boolean changeState(String name, StateLogins stateLogins){
        String sql = "UPDATE register SET stateAccount = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, stateLogins.name());
            stmt.setString(2, name);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logConsole(String.format("No se pudo cambiar el <|estado de la cuenta|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    /**
     * Borra el registro en la base de datos sql
     * @param name Nombre del usuario que quieres borrar
     * @param author Nombre del autor que borro el registro de la base de datos
     * @return true si salió bien de lo contrario false
     */

    public static boolean removeRegister(String name, String author){
        String sql = "DELETE FROM register WHERE name = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, name);

            stmt.executeUpdate();
            logConsole(String.format("Se borro el <|registro|> del jugador <|%1$s|> por <|%2$s|>", name, author), TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        }catch(SQLException e){
            logConsole(String.format("Se borro el <|registro|> del jugador <|%1$s|> por <|%2$s|>", name, author), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
            return false;
        }
    }

    public static void addUUIDBedrock(String name, UUID uuid){
        String sql = "UPDATE register SET uuidBedrock = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logConsole(String.format("Hubo un error al añadir <|UUID de bedrock|> del jugador <|%s|>", name), TypeMessages.ERROR, CategoryMessages.LOGIN);
            MessagesManager.sendErrorException(Message.DATA_MYSQL_EXCEPTION.getMessageLocatePrivate(), e);
        }
    }

    public static void checkRegister(Player player) {
        LoginData login = LoginManager.getDataLogin(player);
        RegisterData register = login.getRegister();
        boolean isExist = DataBaseRegister.isExistRegister(player);
        if (!isExist) {
            DataBaseRegister.addRegister(register.getUsername(),
                    register.getUuidCracked().toString(),
                    register.getUuidPremium() == null ? null : register.getUuidPremium().toString(),
                    register.getRegisterAddress().getHostAddress(),
                    login.getSession().getAddress().getHostAddress(),
                    register.getStateLogins(),
                    register.getPasswordShaded(),
                    login.getSession().getStartTimeLogin(),
                    login.getRegister().getRegisterDate()
            );
            MessagesManager.logConsole("Se añadió un registro de " + player.getName() + " que no existía", TypeMessages.WARNING, CategoryMessages.LOGIN);
        }
    }
}
