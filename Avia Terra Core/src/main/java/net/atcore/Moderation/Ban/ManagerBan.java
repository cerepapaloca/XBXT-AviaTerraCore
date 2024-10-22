package net.atcore.Moderation.Ban;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.Data.DataBaseBan;
import net.atcore.Messages.CategoryMessages;
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
import java.util.HashSet;
import java.util.Objects;

import static net.atcore.Messages.MessagesManager.*;

public class ManagerBan extends DataBaseBan {

    public void banPlayer(Player player, String reason, long time, ContextBan contextBan, String nameAuthor) {
        banPlayer(player.getName(),
                player.getUniqueId().toString(),
                Objects.requireNonNull(player.getAddress()).getHostName(),
                reason, time, contextBan,
                nameAuthor);
    }

    public void banPlayer(String name, String uuid, String ip, String reason, long time, ContextBan context, String nameAuthor) {
        long finalTime = time == 0 ? 0 : time + System.currentTimeMillis();
        kickBan(addBanPlayer(name,
                uuid,
                ip,
                reason,
                (finalTime),
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
        HashSet<DataBan> dataBans = null;

        if (listDataBanByNAME.containsKey(player.getName())) {
            dataBans = getDataBan(player.getName());
            checkName = true;//Se Busca por su UUID de usuario, si está baneado
        }

        if (ip != null && listDataBanByIP.containsKey(ip) && Config.isCheckBanByIp()) {
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
                    String time;
                    if (unbanDate == 0){
                        time = "Permanente";
                    }else {
                        time = GlobalUtils.TimeToString(unbanDate - currentTime,1);
                    }
                    if (unbanDate == 0 || currentTime < unbanDate) {//para saber si él baneo ya expiro
                        sendMessageConsole(player.getName() + " se echo por que estar baneado de: " + context.name() +
                                ". Se detecto por Nombre: <|" + checkName + "|> por ip: <|" + checkIp + "|>. tiempo restante " +
                                "<|" +  time + "|>", TypeMessages.INFO, CategoryMessages.BAN);
                        return kickBan(ban);
                    } else {//eliminar él baneó cuando expiro y realiza en un hilo aparte para que no pete el servidor
                        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> ModerationSection.getBanManager().removeBanPlayer(player.getName(), ban.getContext(), "Servidor (Expiro)"));
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
        String time;
        if (dataBan.getUnbanDate() == 0){
            time = "&lPermanente";
        }else {
            time =GlobalUtils.TimeToString(dataBan.getUnbanDate() - System.currentTimeMillis(), 1) ;
        }

        String reasonFinal = ChatColor.translateAlternateColorCodes('&',
                        "&c&m &r &c&m       &r  &4&lAviaBans&c  &m        &r &c&m \n\n&r"+
                        "&c" + "Estas baneado de &4&o" + contextName + "&r\n" +
                        "&c" + "Expira en: " + "&4" + time + "\n" +
                        "&c" + "Razón de baneo: " + "&4" + dataBan.getReason() + "\n" +
                        "&c" + "Apelación de ban: " +  "&4" + LINK_DISCORD + "\n\n" +
                        "&c" + "&m &r &c&m                                 &r &c&m "
                );
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            Player player = null;
            if (dataBan.getUuid() != null) player = Bukkit.getPlayer(dataBan.getUuid());
            if (player != null) player.kickPlayer(reasonFinal);
        });
        return reasonFinal;
    }
}
