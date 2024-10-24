package net.atcore.Data;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.Ban.DataBan;
import net.atcore.Moderation.Ban.SearchBanBy;
import net.atcore.Utils.GlobalConstantes;
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

        if (!AviaTerraCore.isStarting()) sendMessageConsole("Baneos Recargado", TypeMessages.SUCCESS);
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
                        reloadDatabase();
                        sendMessageConsole("DataBase Bans " + MessagesManager.COLOR_SUCCESS + "Ok", TypeMessages.INFO, false);
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
        InetAddress ipAddress = null;
        if (ip != null) ipAddress = InetAddress.getByName(ip.split("/")[0]);
        DataBan dataBan = new DataBan(name, uuid, ipAddress, reason, dateUnban, dateBan, ContextBan.valueOf(context), author);

        HashSet <DataBan> listUUID = listDataBanByNAME.getOrDefault(name,  new HashSet<>());//busca si hay una lista de DataBan si no hay crea una
        listUUID.add(dataBan);//Añade el DataBan a la lista
        listDataBanByNAME.put(name, listUUID);//Añade la lista de DataBan Remplazando el dato

        if (ipAddress == null) return;

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
        }

        return banList;
    }

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
            statement.setString(3, dataBan.getAddress() != null ? dataBan.getAddress().toString().split("/")[0] : null);
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
            reloadDatabase();
            sendMessageConsole("el jugador <|" + dataBan.getName() + "|> fue baneado de <|" + dataBan.getContext() + "|> durante <|" +
                    tiempoDeBaneo + "|> por el jugador <|" + dataBan.getAuthor() +
                    "|> y la razón es <|" + dataBan.getReason() + "|> ", TypeMessages.SUCCESS, CategoryMessages.BAN);

        } catch (SQLException e) {
            String tiempoDeBaneo;
            if (dataBan.getUnbanDate() == GlobalConstantes.NUMERO_PERMA){
                tiempoDeBaneo = "Permanente";
            }else {
                tiempoDeBaneo = GlobalUtils.timeToString(dataBan.getUnbanDate() - dataBan.getBanDate(), 2);
            }
            sendMessageConsole("Error al banear al jugador <|" + dataBan.getName() + "|>, razón de baneo: <|" + dataBan.getReason() + "|>, Contexto: <|"
                            + dataBan.getContext().name() + "|> Autor: <|" + dataBan.getAuthor() + "|> Tiempo de baneo: <|" + tiempoDeBaneo + "|>"
                    , TypeMessages.ERROR, CategoryMessages.BAN);
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
            sendMessageConsole("Error al desbanear al jugador <|" + name + "|> del contexto: <|" + context.name() + "|> por <|" + author
                    , TypeMessages.ERROR, CategoryMessages.BAN);
            throw new RuntimeException(e);
        }
    }

}