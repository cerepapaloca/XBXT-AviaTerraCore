package net.atcore.moderation.Ban;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.utils.GlobalConstantes;
import net.atcore.data.DataBaseBan;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.*;

public class ManagerBan extends DataBaseBan {

    public void banPlayer(Player player, String reason, long time, ContextBan contextBan, String nameAuthor) {
        banPlayer(player.getName(),
                player.getUniqueId(),
                player.getAddress().getAddress(),
                reason, time, contextBan,
                nameAuthor);
    }

    public void banPlayer(String name, UUID uuid, InetAddress ip, String reason, long time, ContextBan context, String nameAuthor) {
        long finalTime = time == GlobalConstantes.NUMERO_PERMA ? GlobalConstantes.NUMERO_PERMA : time == Long.MAX_VALUE ? Long.MAX_VALUE : time + System.currentTimeMillis();
        DataBan dataBan = new DataBan(name, uuid, ip, reason, finalTime, System.currentTimeMillis(), context, nameAuthor);
        kickBan(dataBan);
        AviaTerraCore.getInstance().enqueueTaskDataBase(() -> addBanPlayer(dataBan));
    }

    /**
     * Version simplificada del {@link #checkBan(Player, InetAddress, ContextBan)}
     */

    public static IsBan checkBan(@NotNull Player player, @Nullable ContextBan context) {
        return checkBan(player, Objects.requireNonNull(player.getAddress()).getAddress(), context);
    }

    /**
     * Comprueba si un jugador está baneado en un contexto determinado (Se puede ejecutar en un hilo separado)
     * @param player el jugador
     * @param ip la ip del jugador a veces se tiene que obtener por otros medios
     * @param context en que contexto lo quieres hacer el check
     * @return da true cuando esta banea y falso si no está baneado
     */

    public static IsBan checkBan(@NotNull Player player, @Nullable InetAddress ip, @Nullable ContextBan context) {
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

            if (dataBans == null) return IsBan.UNKNOWN;//Otro "por si acaso del por si acaso"
            //quiere decir que el DataBan dio nulo y que la base datos explotó
            for (DataBan ban : dataBans) {//mira cada DataBan del jugador para saber exactamente de que está baneado
                if (ban.getContext().equals(context)) {//saber si el contexto del check le corresponde al baneo
                    long unbanDate = ban.getUnbanDate();
                    long currentTime = System.currentTimeMillis();
                    String time;
                    time = GlobalUtils.timeToString(unbanDate,1, true);
                    if (unbanDate == GlobalConstantes.NUMERO_PERMA || currentTime < unbanDate) {//para saber si él baneo ya expiro
                        sendMessageConsole(player.getName() + " se echo por que estar baneado de: " + context.name() +
                                ". Se detecto por Nombre: <|" + checkName + "|> por ip: <|" + checkIp + "|>. tiempo restante " +
                                "<|" +  time + "|>", TypeMessages.INFO, CategoryMessages.BAN);
                        kickBan(ban);
                        return IsBan.YES;
                    } else {//eliminar él baneó cuando expiro y realiza en un hilo aparte para que no pete el servidor
                        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> ModerationSection.getBanManager().removeBanPlayer(player.getName(), ban.getContext(), "Servidor (Expiro)"));
                        return IsBan.NOT;
                    }
                } else{
                    return IsBan.NOT_THIS_CONTEXT;
                }
            }
        } else {
            return IsBan.NOT;
        }
        return IsBan.NOT;
    }
    @Getter
    private static String reasonBan;

    private static void kickBan(DataBan dataBan) {
        String contextName = dataBan.getContext().name().toLowerCase().replace("_", " ");
        if (Objects.equals(dataBan.getContext().name(), "GLOBAL")) contextName = "Avia Terra";
        String time;
        time =GlobalUtils.timeToString(dataBan.getUnbanDate(), 1, true);

        String reasonFinal = ChatColor.translateAlternateColorCodes('&',
                        "&c&m &r &c&m       &r  &4&lAviaBans&c  &m        &r &c&m \n\n&r"+
                        "&c" + "Estas baneado de &4&o" + contextName + "&r\n" +
                        "&c" + "Expira en: " + "&4" + time + "\n" +
                        "&c" + "Razón de baneo: " + "&4" + dataBan.getReason() + "\n" +
                        "&c" + "Apelación de ban: " +  "&4" + LINK_DISCORD + "\n\n" +
                        "&c" + "&m &r &c&m                                 &r &c&m "
                );
        reasonBan = reasonFinal;
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            Player player = null;
            if (dataBan.getUuid() != null) player = Bukkit.getPlayer(dataBan.getUuid());
            if (player != null) player.kickPlayer(reasonFinal);
        });
    }
}
