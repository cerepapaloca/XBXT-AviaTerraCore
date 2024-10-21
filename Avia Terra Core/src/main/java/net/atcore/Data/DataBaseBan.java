package net.atcore.Data;

import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.Ban.DataBan;
import net.atcore.Moderation.Ban.SearchBanBy;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class DataBaseBan extends DataBaseMySql {

    protected static final HashMap<String, HashSet<DataBan>> listDataBanByNAME = new HashMap<>();
    protected static final HashMap<InetAddress, HashSet<DataBan>> listDataBanByIP = new HashMap<>();


    public static HashSet<DataBan> getDataBan(String name) {
        return listDataBanByNAME.get(name);
    }

    public static HashSet<DataBan> getDataBan(InetAddress ip) {
        return listDataBanByIP.get(ip);
    }

    @Override
    protected void reloadDatabase() {
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
            throw new RuntimeException(e);
        }

        sendMessageConsole("Baneos Recargado", TypeMessages.SUCCESS);
    }

    @Override
    protected void createTable() {
        String checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = ? AND table_name = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(checkTableSQL)) {//revisa si la tabla existe
            stmt.setString(1, "aviaterra");
            stmt.setString(2, "bans");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        sendMessageConsole("DataBase Bans " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO);
                        reloadDatabase();
                        return;//si existe, se detiene
                    }
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
            sendMessageConsole("DataBase Bans " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private static void addListDataBan(String name, @Nullable String uuids, @Nullable String ip, String reason, long dateUnban, long dateBan, String context, String author) throws UnknownHostException {
        UUID uuid = null;
        if (uuids != null){
            uuid = UUID.fromString(uuids);
        }
        InetAddress ipAddress = InetAddress.getByName(ip);
        DataBan dataBan = new DataBan(name, uuid, InetAddress.getByName(ip), reason, dateUnban, dateBan, ContextBan.valueOf(context), author);

        HashSet <DataBan> listUUID = listDataBanByNAME.getOrDefault(name,  new HashSet<>());//busca si hay una lista de DataBan si no hay crea una
        listUUID.add(dataBan);//Añade el DataBan a la lista
        listDataBanByNAME.put(name, listUUID);//Añade la lista de DataBan Remplazando el dato

        if (ip == null) return;

        HashSet <DataBan> listIP = listDataBanByIP.getOrDefault(ipAddress,  new HashSet<>());
        listIP.add(dataBan);
        listDataBanByIP.put(ipAddress, listIP);

    }

    protected static @NotNull HashSet<DataBan> getDataBan(@NotNull Player player, InetAddress address, SearchBanBy searchBanBy) throws SQLException {
        HashSet<DataBan> banList = new HashSet<>();
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
        } finally {
            if (statement != null) {
                statement.close(); // Asegúrate de cerrar el PreparedStatement después de usarlo
            }
            if (connection != null) {
                connection.close(); // Cierra la conexión también
            }
        }

        return banList;
    }

    public DataBan addBanPlayer(String name, String uuid, String ip, String reason, long unbanDate, long banDate, String context, String author) {
        String sql = "INSERT INTO bans (name, uuid, ip, reason, unban_date, ban_date, context, author) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), uuid = VALUES(uuid), ip = VALUES(ip), reason = VALUES(reason), " +
                "unban_date = VALUES(unban_date), ban_date = VALUES(ban_date), context = VALUES(context), author = VALUES(author)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.setString(3, ip);
            statement.setString(4, reason);
            statement.setLong(5, unbanDate);
            statement.setLong(6, banDate);
            statement.setString(7, context);
            statement.setString(8, author);
            statement.executeUpdate();
            String tiempoDeBaneo;
            if (unbanDate == 0){
                tiempoDeBaneo = "Permanente";
            }else {
                tiempoDeBaneo = GlobalUtils.TimeToString(unbanDate - banDate, 2);
            }
            reloadDatabase();
            sendMessageConsole("el jugador <|" + name + "|> fue baneado de <|" + context + "|> durante <|" +
                    tiempoDeBaneo + "|> por el jugador <|" + author +
                    "|> y la razón es <|" + reason + "|> ", TypeMessages.SUCCESS, CategoryMessages.BAN);
            UUID uuids;
            if (uuid == null){
                uuids = null;
            }else {
                uuids = UUID.fromString(uuid);
            }
            return new DataBan(name, uuids, InetAddress.getByName(ip), reason, unbanDate, banDate, ContextBan.valueOf(context), author);
        } catch (SQLException | UnknownHostException e) {
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
            reloadDatabase();
            sendMessageConsole("Se Desbano el jugador <|" + name + "|> en el contexto <|" + context.name() + "|> " +
                    "por <|" + author + "|>", TypeMessages.INFO, CategoryMessages.BAN);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}