package net.atcore.moderation.ban;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.sql.DataBaseBan;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.*;

public class BanManager extends DataBaseBan {

    public void banPlayer(Player player, String reason, long time, ContextBan contextBan, String nameAuthor) {
        banPlayer(player.getName(),
                player.getUniqueId(),
                player.getAddress().getAddress(),
                reason, time, contextBan,
                nameAuthor);
    }

    public void banPlayer(String name, UUID uuid, InetAddress ip, String reason, long time, ContextBan context, String nameAuthor) {
        long finalTime = time == GlobalConstantes.NUMERO_PERMA ? GlobalConstantes.NUMERO_PERMA : time == Long.MAX_VALUE ? Long.MAX_VALUE : time + System.currentTimeMillis();

        DataLogin dataLogin = LoginManager.getDataLogin(name);
        if (dataLogin != null) {
            ip = dataLogin.getRegister().getLastAddress();
        }
        DataBan dataBan = new DataBan(name, uuid, ip, reason, finalTime, System.currentTimeMillis(), context, nameAuthor);
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            dataBan.getContext().onBan(player, dataBan);
        }
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> addBanPlayer(dataBan));
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
        Collection<DataBan> dataBans = null;

        // Se busca él databan del jugador
        if (listDataBanByNAME.containsKey(player.getName())) {
            dataBans = getDataBan(player.getName()).values();
            checkName = true;//Se Busca por su UUID de usuario, si está baneado
        }

        if (ip != null && listDataBanByIP.containsKey(ip) && Config.isCheckBanByIp()) {
            dataBans = getDataBan(ip).values();
            checkIp = true;//Se Busca por su ip, si está baneado
        }

        if (checkName || checkIp) {//comprueba que esté baneado por algúna de las dos razónes

            if (dataBans.isEmpty()) {//En caso qué llega a estar en la lista de baneados, pero no tiene un ban vinculado
                return IsBan.UNKNOWN;
                /*No se usa por que se tiene que ejecutar en un hilo aparte
                try {
                    if (checkName){//hace la búsqueda por ip o por UUID
                        dataBans = getDataBan(player, ip, SearchBanBy.NAME);
                    }else {
                        dataBans = getDataBan(player, ip, SearchBanBy.IP);
                    }
                } catch (SQLException e) {//por si explota
                    Bukkit.getLogger().severe(e.getMessage());
                }
                */
            }

            if (context == null){// Otro "por si acaso"
                context = ContextBan.GLOBAL;
            }
            for (DataBan ban : dataBans) {// mira cada DataBan del jugador para saber exactamente de que está baneado
                if (ban.getContext().equals(context)) {// saber si el contexto del check le corresponde al baneo
                    long unbanDate = ban.getUnbanDate();
                    long currentTime = System.currentTimeMillis();
                    String time;
                    time = GlobalUtils.timeToString(unbanDate,1, true);
                    if (unbanDate == GlobalConstantes.NUMERO_PERMA || currentTime < unbanDate) {// para saber si él baneó ya expiro o es permanente
                        sendMessageConsole(player.getName() + " se \"echo\" por que estar baneado de: <|" + context.name() +
                                "|>. Se detecto por Nombre: <|" + checkName + "|> por ip: <|" + checkIp + "|>. tiempo restante " +
                                "<|" +  time + "|>", TypeMessages.INFO, CategoryMessages.BAN);
                        return IsBan.YES;
                    } else {// eliminar él baneó cuando expiro y realiza en un hilo aparte para que no pete el servidor
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

    @Contract(pure = true)
    public static String formadMessageBan(DataBan dataBan) {
        String contextName = dataBan.getContext().name().toLowerCase().replace("_", " ");
        if (Objects.equals(dataBan.getContext().name(), "GLOBAL")) contextName = "Avia Terra";
        return String.format("""
                Estas Baneado De <|%s|>
                Expira en: <|%s|>
                Razón de Baneo: <|%s|>
                Apelación de ban: <|%s|>
                """,contextName,
                GlobalUtils.timeToString(dataBan.getUnbanDate(), 1, true),
                dataBan.getReason(),
                LINK_DISCORD);
    }
}
