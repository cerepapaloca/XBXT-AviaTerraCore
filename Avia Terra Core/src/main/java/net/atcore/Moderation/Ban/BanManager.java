package net.atcore.Moderation.Ban;

import net.atcore.Data.BanDataBase;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.ModerationSection;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static net.atcore.AviaTerraCore.PLUGIN;
import static net.atcore.Messages.MessagesManager.*;

public class BanManager extends BanDataBase {

    //todo falta cositas para tener el sistema de baneo

    public static void unBanPlayer(String name, ContextBan context) {

        sendMessageConsole("Se desbaneo el jugador <|" + name, TypeMessages.INFO);
    }

    public static void banPlayer(Player player, String reason, long time, ContextBan contextBan, String nameAuthor) {
        banPlayer(player.getName(),
                player.getUniqueId().toString(),
                Objects.requireNonNull(player.getAddress()).getHostName(),
                reason, time, contextBan,
                nameAuthor);
    }

    public static void banPlayer(String name, String uuid, String ip, String reason, long time, ContextBan context, String nameAuthor) {
        kickBan(addBanPlayer(name,
                uuid,
                ip,
                reason,
                (time + System.currentTimeMillis()),
                System.currentTimeMillis(),
                context.name(),
                nameAuthor)
        );
    }

    /**
     * Version simplificada del {@link #checkBan(Player, InetAddress, ContextBan)}
     */

    public static String checkBan(@NotNull Player player, @Nullable ContextBan context) {
        return checkBan(player, Objects.requireNonNull(player.getAddress()).getAddress(), context);
    }

    /**
     * Comprueba si un jugador está baneado en un contexto determinado (Se puede ejecutar en un hilo separado)
     * @param player el jugador
     * @param ip la ip del jugador a veces se tiene que obtener por otros medios
     * @param context en que contexto lo quieres hacer el check
     * @return da true cuando esta banea y falso si no está baneado
     */

    public static String checkBan(@NotNull Player player, @Nullable InetAddress ip, @Nullable ContextBan context) {
        boolean checkName = false;
        boolean checkIp = false;
        List<DataBan> dataBans = null;

        if (listDataBanByNAME.containsKey(player.getName())) {
            dataBans = getDataBan(player.getName());
            checkName = true;//Se Busca por su UUID de usuario, si está baneado
        }

        if (ip != null && listDataBanByIP.containsKey(ip)) {
            dataBans = getDataBan(ip);
            checkIp = true;//Se Busca por su ip, si está baneado
        }

        if (checkName || checkIp) {//comprueba que esté baneado por algúna de las dos razónes

            if (dataBans == null) {//Esto es un "por si acaso"
                try {
                    if (checkName){//hace la búsqueda por ip o por UUID
                        dataBans = getDataBan(player, ip, SearchBanBy.NAME);
                    }else {
                        dataBans = getDataBan(player, ip, SearchBanBy.IP);
                    }
                } catch (SQLException e) {//por si explota
                    sendMessageConsole(e.getMessage(),TypeMessages.ERROR);
                }
            }

            if (context == null){//Otro "por si acaso"
                context = ContextBan.GLOBAL;
            }

            if (dataBans == null) return null;//Otro "por si acaso del por si acaso"
            //quiere decir que el DataBan dio nulo y que la base datos explotó
            for (DataBan ban : dataBans) {//mira cada DataBan del jugador para saber exactamente de que está baneado
                if (ban.getContext().equals(context)) {//saber si el contexto del check le corresponde al baneo
                    long unbanDate = ban.getUnbanDate();
                    long currentTime = System.currentTimeMillis();

                    if (currentTime < unbanDate) {//para saber si él baneo ya expiro
                        sendMessageConsole(player.getName() + " se echo por que estar baneado de: " + context.name() +
                                ". Se detecto por Nombre: <|" + checkName + "|> por ip: <|" + checkIp, TypeMessages.WARNING);
                        return kickBan(ban);
                    } else {//eliminar él baneó cuando expiro y realiza en un hilo aparte para que no pete el servidor
                        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, () -> ModerationSection.getBanManager().removeBanPlayer(player.getName(), ban.getContext(), "Expiro"));
                        return null;
                    }
                } else{
                    return "";
                }
            }
        } else {
            return null;
        }
        return null;
    }

    private static String kickBan(DataBan dataBan) {
        String contextName = dataBan.getContext().name().toLowerCase().replace("_", " ");
        if (Objects.equals(dataBan.getContext().name(), "GLOBAL")) contextName = "Avia Terra";

        String reasonFinal = ChatColor.translateAlternateColorCodes('&',
                colorInfo + "Estas baneado de &o" + contextName + "&r\n" +
                colorInfo + "Expira en: " + colorEspacial + GlobalUtils.TimeToString(dataBan.getUnbanDate() - System.currentTimeMillis(), 1) + "\n" +
                colorInfo + "Razón de baneo: " + colorEspacial + dataBan.getReason() + "\n" +
                colorInfo + "Apelación de ban: " + linkDiscord);
        Bukkit.getScheduler().runTask(PLUGIN, () -> {
            Player player = null;
            if (dataBan.getUuid() != null) player = Bukkit.getPlayer(dataBan.getUuid());
            if (player != null) player.kickPlayer(reasonFinal);
        });
        return reasonFinal;
    }
}
