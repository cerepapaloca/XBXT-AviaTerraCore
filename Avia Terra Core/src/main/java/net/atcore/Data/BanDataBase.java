package net.atcore.Data;

import lombok.Getter;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.ContextBan;
import net.atcore.Moderation.DataBan;
import net.atcore.Moderation.SearchBanBy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashSet;
import java.util.UUID;

public class BanDataBase extends DataBaseMySql {

    @Getter
    private static final HashSet<DataBan> dataBans = new HashSet<>();

    public static DataBan getDataBan(UUID uuid) {
        for (DataBan dataBan : dataBans) if (dataBan.getUuid().equals(uuid)) return dataBan;
        return null;
    }

    @Override
    protected void reloadDatabase() {
        String sql = "SELECT uuid, name, ip, reason, unban_date, ban_date ,context, FROM bans";
        dataBans.clear();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String name = resultSet.getString("name");
                String ip = resultSet.getString("ip");
                String reason = resultSet.getString("reason");
                long dateUnban = resultSet.getLong("unban_date");
                long dateBan = resultSet.getLong("ban_date");
                String context = resultSet.getString("context");
                dataBans.add(new DataBan(UUID.fromString(uuid), name, InetAddress.getByName(ip), reason, dateUnban, dateBan, ContextBan.valueOf(context)));
            }
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        MessagesManager.sendMessageConsole("Baneos recargado con exitosamente", TypeMessages.SUCCESS);
    }

    @Override
    protected void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS bans (" +
                "uuid VARCHAR(100) NOT NULL, " +
                "name VARCHAR(36), " +
                "ip VARCHAR(45) NOT NULL, " +
                "reason TEXT, " +
                "ban_date BIGINT, " +
                "unban_date BIGINT, " +
                "context VARCHAR(255), " +
                "PRIMARY KEY (uuid), " +
                "UNIQUE (uuid)" +
                ");";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static @Nullable DataBan getDataBan(@NotNull Player player, InetAddress address , SearchBanBy searchBanBy) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = getConnection();
        ResultSet resultSet;
        switch (searchBanBy){
            case ip -> {
                statement = connection.prepareStatement("SELECT * FROM bans WHERE ip = ?");
                statement.setString(1, address.getHostAddress());
            }
            case uuid -> {
                statement = connection.prepareStatement("SELECT * FROM bans WHERE uuid = ?");
                statement.setString(1, player.getUniqueId().toString());
            }
        }
        try {
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new DataBan(player.getUniqueId(),
                        resultSet.getString("name"),
                        InetAddress.getByName(resultSet.getString("ip")),
                        resultSet.getString("reason"),
                        resultSet.getLong("unban_date"),
                        resultSet.getLong("ban_date"),
                        ContextBan.valueOf(resultSet.getString("context"))
                );
            } else {
                return null;
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected static DataBan addBanPlayer(String uuid, String name, String ip, String reason, long unbanDate, long banDate, String context) {
        String sql = "INSERT INTO bans (uuid, name, ip, reason, unban_date, ban_date, context) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE uuid = VALUES(uuid), name = VALUES(name), reason = VALUES(reason), " +
                "ban_date = VALUES(ban_date), unban_date = VALUES(unban_date), context = VALUES(context)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, ip);
            statement.setString(4, reason);
            statement.setLong(5, unbanDate);
            statement.setLong(6, banDate);
            statement.setString(7, context);
            statement.executeUpdate();
            DataBan dataBan = new DataBan(UUID.fromString(uuid), name, InetAddress.getByName(ip), reason, unbanDate, banDate, ContextBan.valueOf(context));
            dataBans.add(dataBan);
            return dataBan;
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void removeBanPlayer(Player player) {
        String sql = "DELETE FROM bans WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
            dataBans.remove(getDataBan(player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}