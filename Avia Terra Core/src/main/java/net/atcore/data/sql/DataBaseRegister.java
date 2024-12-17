package net.atcore.data.sql;

import net.atcore.AviaTerraCore;
import net.atcore.data.DataBaseMySql;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import static net.atcore.command.commnads.RemoveRegisterCommand.names;
import static net.atcore.messages.MessagesManager.sendMessageConsole;

public class DataBaseRegister extends DataBaseMySql {
    @Override
    public void reloadDatabase() {
        String sql = "SELECT name, uuidPremium, uuidCracked, ipRegister, ipLogin, isPremium, password, lastLoginDate, registerDate, gmail, discord FROM register";
        HashMap<UUID, DataSession> sessions = new HashMap<>();


        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            LoginManager.getDataLogin().forEach(dataLogin -> sessions.put(dataLogin.getRegister().getUuidCracked(),dataLogin.getSession()));//rescata las sesiones
            LoginManager.clearDataLogin();//se limpia los datos
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
                String gmail = resultSet.getString("gmail");
                String discord = resultSet.getString("discord");

                UUID uuid = UUID.fromString(uuidCracked);

                DataRegister dataRegister = new DataRegister(name, uuid,
                        uuidPremium != null ? UUID.fromString(uuidPremium) : null, isPremium == 1 ? StateLogins.PREMIUM : StateLogins.CRACKED,
                        false);
                dataRegister.setRegisterAddress(InetAddress.getByName(ipRegister));
                dataRegister.setLastAddress(InetAddress.getByName(ipLogin));
                dataRegister.setPasswordShaded(password);
                dataRegister.setLastLoginDate(lastLoginDate);
                dataRegister.setRegisterDate(registerDate);
                dataRegister.setGmail(gmail);
                dataRegister.setDiscord(discord);

                DataLogin dataLogin = LoginManager.addDataLogin(name, dataRegister);
                DataSession session = sessions.get(uuid);//Se obtiene las sesiones rescatas
                Player player = Bukkit.getPlayer(uuid);

                if (session == null || player == null) continue;

                //se modifica los datos de la session
                session.setAddress(InetAddress.getByName(ipLogin));
                session.setStartTimeLogin(lastLoginDate);
                dataLogin.setSession(session);//añade la sesión rescatada

                if (!LoginManager.checkLoginIn(player)){//revisa si son validas las sesiones
                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> GlobalUtils.kickPlayer(player, "Hay una discrepancia es tu session, vuelve a iniciar sessión"));
                }
            }
            sessions.clear();
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        LoginManager.getDataLogin().forEach(login -> names.add(login.getRegister().getUsername()));

        if (!AviaTerraCore.isStarting()) sendMessageConsole("Registros Recargado", TypeMessages.SUCCESS);
    }

    @Override
    protected void createTable() {

        try (Connection connection = getConnection()) {//revisa si la tabla existe
            DatabaseMetaData dbMetaData = connection.getMetaData();
            try (ResultSet resultSet = dbMetaData.getTables(null, null, "register", null)) {
                if (resultSet.next()){
                    reloadDatabase();
                    sendMessageConsole("DataBase Registro " + TypeMessages.SUCCESS.getMainColor() + "Ok", TypeMessages.INFO, false);
                    return;
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
                "gmail VARCHAR(100)," +
                "discord VARCHAR(100)," +
                "PRIMARY KEY (name)," +
                "UNIQUE (name)" +
                ");";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            reloadDatabase();
            sendMessageConsole("DataBase Registro " + TypeMessages.SUCCESS.getMainColor()  + "Creada", TypeMessages.INFO, false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addRegister(String name, String uuidPremium,
                                   String uuidCracked, String ipRegister,
                                   String ipLogin, Boolean isPremium,
                                   String password, long lastLoginDate,
                                   long registerDate) {
        String sql = "INSERT INTO register (name, uuidPremium, uuidCracked, ipRegister, ipLogin, isPremium, password, lastLoginDate, registerDate, gmail, discord) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), uuidPremium = VALUES(uuidPremium), uuidCracked = VALUES(uuidCracked), ipRegister = VALUES(ipRegister), " +
                "ipLogin = VALUES(ipLogin), isPremium = VALUES(isPremium), password = VALUES(password), lastLoginDate = VALUES(lastLoginDate)," +
                "registerDate = VALUES(registerDate), gmail = VALUES(gmail), discord = VALUES(discord) ";
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
            statement.setString(10, null);
            statement.setString(11, null);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateLoginDate(String name ,long time){
        String sql = "UPDATE register SET lastLoginDate = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, time);
            stmt.setString(2, name);

            stmt.executeUpdate();
            sendMessageConsole("Se actualizó la <|fecha del ultimo login|> del jugador <|" + name + "|> exitosamente", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        } catch (SQLException e) {
            sendMessageConsole("No se pudo actualizar la <|fecha del ultimo login|> del jugador <|" + name+ "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean updateGmail(String name ,String gmail){
        String sql = "UPDATE register SET gmail = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, gmail);
            stmt.setString(2, name);

            stmt.executeUpdate();
            sendMessageConsole("Se actualizó el <|gmail|> del jugador <|" + name + "|> exitosamente", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        } catch (SQLException e) {
            sendMessageConsole("No se pudo actualizar <|gmail|> del jugador <|" + name+ "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean updateDiscord(String name ,String discord){
        String sql = "UPDATE register SET discord = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, discord);
            stmt.setString(2, name);

            stmt.executeUpdate();
            sendMessageConsole("Se actualizó el <|discord|> del jugador <|" + name + "|> exitosamente", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        } catch (SQLException e) {
            sendMessageConsole("No se pudo actualizar <|discord|> del jugador <|" + name+ "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean updatePassword(String name, String password){
        String sql = "UPDATE register SET password = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, name);

            stmt.executeUpdate();
            sendMessageConsole("Se actualizó la <|contraseña|> del jugador <|" + name + "|> exitosamente", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        } catch (SQLException e) {
            sendMessageConsole("No se pudo actualizar la <|contraseña|> del jugador <|" + name + "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static boolean updateAddress(String name, String ip){
        String sql = "UPDATE register SET ipLogin = ? WHERE name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setString(2, name);

            stmt.executeUpdate();
            sendMessageConsole("Se actualizó la <|ip|> del jugador <|" + name + "|> exitosamente", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        } catch (SQLException e) {
            sendMessageConsole("No se pudo actualizar la <|ip|> del jugador <|" + name + "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
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
            sendMessageConsole("Se borro el <|registro|> del jugador <|" + name + "|> por <|" + author + "|>", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            return true;
        }catch(SQLException e){
            sendMessageConsole("Hubo un error al borrar el <|registro|> del jugador <|" + name + "|> por <|" + author + "|>", TypeMessages.ERROR, CategoryMessages.LOGIN);
            AviaTerraCore.getInstance().getLogger().warning(e.getMessage());
            return false;
        }
    }
}
