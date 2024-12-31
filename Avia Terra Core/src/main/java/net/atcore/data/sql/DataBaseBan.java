package net.atcore.data.sql;

import net.atcore.AviaTerraCore;
import net.atcore.data.DataBaseMySql;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ban.DataBan;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;

import static net.atcore.messages.MessagesManager.sendMessageConsole;

public class DataBaseBan extends DataBaseMySql {

    public static final HashMap<String, HashMap<ContextBan, DataBan>> listDataBanByNAME = new HashMap<>();
    public static final HashMap<InetAddress, HashMap<ContextBan, DataBan>> listDataBanByIP = new HashMap<>();


    public static HashMap<ContextBan, DataBan> getDataBan(String name) {
        return listDataBanByNAME.get(name);
    }

    public static HashMap<ContextBan, DataBan> getDataBan(InetAddress ip) {
        return listDataBanByIP.get(ip);
    }

    @Override
    public void reload() {
        String sql = "SELECT uuid, name, ip, reason, unban_date, ban_date, context, author FROM bans";
        listDataBanByNAME.clear();
        listDataBanByIP.clear();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String uuids = resultSet.getString("uuid");
                String ip = resultSet.getString("ip");
                String reason = resultSet.getString("reason");
                long dateUnban = resultSet.getLong("unban_date");
                long dateBan = resultSet.getLong("ban_date");
                String context = resultSet.getString("context");
                String author = resultSet.getString("author");

                // Lógica para añadir los datos a tus listas
                addListDataBan(name, uuids, ip, reason, dateUnban, dateBan, context, author);
            }
        } catch (SQLException | UnknownHostException e) {
            MessagesManager.sendErrorException("Error al recargar la base de datos", e);
        }

        if (!AviaTerraCore.isStarting()) sendMessageConsole("Baneos Recargado", MessagesType.SUCCESS);
    }

    @Override
    protected void createTable() {
        try (Connection connection = getConnection()) {//revisa si la tabla existe
            DatabaseMetaData dbMetaData = connection.getMetaData();
            try (ResultSet resultSet = dbMetaData.getTables(null, null, "bans", null)) {
                if (resultSet.next()){
                    reload();
                    sendMessageConsole("DataBase Bans " + MessagesType.SUCCESS.getMainColor() + "Ok", MessagesType.INFO, false);
                    return;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String createTableSQL = "CREATE TABLE IF NOT EXISTS bans (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(36) NOT NULL, " +
                "uuid VARCHAR(100), " +
                "ip VARCHAR(45), " +
                "reason TEXT, " +
                "unban_date BIGINT NOT NULL, " +
                "ban_date BIGINT, " +
                "context VARCHAR(255) NOT NULL, " +
                "author VARCHAR(36)" +
                ");";
        String addUniqueKeySQL = "ALTER TABLE bans ADD UNIQUE KEY unique_name_context (name, context)";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            statement.executeUpdate(addUniqueKeySQL);
            sendMessageConsole("DataBase Bans " + MessagesType.SUCCESS.getMainColor() + "Creada", MessagesType.INFO, false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private static void addListDataBan(String name,
                                       @Nullable String uuids,
                                       @Nullable String stringIp,
                                       String reason,
                                       long dateUnban,
                                       long dateBan,
                                       String context,
                                       String author) throws UnknownHostException {
        UUID uuid = null;
        if (uuids != null){
            uuid = UUID.fromString(uuids);
        }
        InetAddress ipAddress = null;
        if (stringIp != null) {
            ipAddress = InetAddress.getByName(stringIp.split("/")[0]);
        }else {
            DataLogin dataLogin = LoginManager.getDataLogin(name);
            if (dataLogin != null) {
                ipAddress = dataLogin.getRegister().getLastAddress();
            }
        }
        DataBan dataBan = new DataBan(name, uuid, ipAddress, reason, dateUnban, dateBan, ContextBan.valueOf(context), author);

        HashMap<ContextBan, DataBan> listUUID = listDataBanByNAME.getOrDefault(name,  new HashMap<>());//busca si hay una lista de DataBan si no hay crea una
        listUUID.put(ContextBan.valueOf(context), dataBan);//Añade el DataBan a la lista
        listDataBanByNAME.put(name, listUUID);//Añade la lista de DataBan Remplazando el dato
        if (ipAddress == null) return;

        HashMap<ContextBan, DataBan> listIP = listDataBanByIP.getOrDefault(ipAddress,  new HashMap<>());
        listIP.put(ContextBan.valueOf(context), dataBan);
        listDataBanByIP.put(ipAddress, listIP);

    }
    /* No se esto se volver a usar en el futuro
    protected static @NotNull HashSet<DataBan> getDataBan(@NotNull Player player, InetAddress address, SearchBanBy searchBanBy) throws SQLException {

        PreparedStatement statement = null;
        Connection connection = getConnection();
        ResultSet resultSet;

        switch (searchBanBy) {
            case IP -> {
                statement = connection.prepareStatement("SELECT * FROM bans WHERE ip = ?");
                statement.setString(1, address.getHostAddress());
            }
            case NAME -> {
                statement = connection.prepareStatement("SELECT * FROM bans WHERE name = ?");
                statement.setString(1, player.getName());
            }
        }
        HashSet<DataBan> banList = new HashSet<>();
        try {

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                banList.add(new DataBan(
                        resultSet.getString("name"),
                        player.getUniqueId(),
                        InetAddress.getByName(resultSet.getString("ip")),
                        resultSet.getString("reason"),
                        resultSet.getLong("unban_date"),
                        resultSet.getLong("ban_date"),
                        ContextBan.valueOf(resultSet.getString("context")),
                        resultSet.getString("author")
                ));
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return banList;
    }*/

    public void addBanPlayer(DataBan dataBan) {
        String sql = "INSERT INTO bans (name, uuid, ip, reason, unban_date, ban_date, context, author) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), uuid = VALUES(uuid), ip = VALUES(ip), reason = VALUES(reason), " +
                "unban_date = VALUES(unban_date), ban_date = VALUES(ban_date), context = VALUES(context), author = VALUES(author)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dataBan.getName());
            statement.setString(2, dataBan.getUuid() != null ? dataBan.getUuid().toString() : null);
            statement.setString(3, dataBan.getAddress() != null ? dataBan.getAddress().getHostAddress().split("/")[0] : null);
            statement.setString(4, dataBan.getReason());
            statement.setLong(5, dataBan.getUnbanDate());
            statement.setLong(6, dataBan.getBanDate());
            statement.setString(7, dataBan.getContext().toString());
            statement.setString(8, dataBan.getAuthor());
            statement.executeUpdate();
            String tiempoDeBaneo;
            if (dataBan.getUnbanDate() == GlobalConstantes.NUMERO_PERMA){
                tiempoDeBaneo = "Permanente";
            }else {
                tiempoDeBaneo = GlobalUtils.timeToString(dataBan.getUnbanDate() - dataBan.getBanDate(), 2);
            }
            reload();
            sendMessageConsole(String.format(Message.DATA_BAN_ADD_OK.getMessage(), dataBan.getName(), dataBan.getContext(), tiempoDeBaneo, dataBan.getAuthor(), dataBan.getReason()), MessagesType.SUCCESS, CategoryMessages.BAN);

        } catch (SQLException e) {
            String tiempoDeBaneo;
            if (dataBan.getUnbanDate() == GlobalConstantes.NUMERO_PERMA){
                tiempoDeBaneo = "Permanente";
            }else {
                tiempoDeBaneo = GlobalUtils.timeToString(dataBan.getUnbanDate() - dataBan.getBanDate(), 2);
            }
            sendMessageConsole(String.format(Message.DATA_BAN_ADD_FAILED.getMessage(), dataBan.getName(), dataBan.getContext(), tiempoDeBaneo, dataBan.getAuthor(), dataBan.getReason()), MessagesType.SUCCESS, CategoryMessages.BAN);
            throw new RuntimeException(e);
        }
    }

    public void removeBanPlayer(String name, ContextBan context, String author) {
        String sql = "DELETE FROM bans WHERE name = ? AND context = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, context.name());
            statement.executeUpdate();
            reload();
            sendMessageConsole(String.format(Message.DATA_BAN_REMOVE_OK.getMessage(), name, context.name(), author), MessagesType.SUCCESS, CategoryMessages.BAN);
        } catch (SQLException e) {
            sendMessageConsole(String.format(Message.DATA_BAN_REMOVE_FAILED.getMessage(), name, context.name(), author), MessagesType.ERROR, CategoryMessages.BAN);
            throw new RuntimeException(e);
        }
    }

}