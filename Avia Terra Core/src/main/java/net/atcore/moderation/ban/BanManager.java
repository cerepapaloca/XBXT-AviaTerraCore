package net.atcore.moderation.ban;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.messages.Message;
import net.atcore.security.login.model.LoginData;
import net.atcore.security.login.LoginManager;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalConstantes;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.*;

@UtilityClass
public class BanManager {

    public static final HashMap<String, HashMap<ContextBan, DataBan>> listDataBanByNAME = new HashMap<>();
    public static final HashMap<InetAddress, HashMap<ContextBan, DataBan>> listDataBanByIP = new HashMap<>();

    public static HashMap<ContextBan, DataBan> getDataBan(String name) {
        return listDataBanByNAME.get(name);
    }

    public static HashMap<ContextBan, DataBan> getDataBan(InetAddress ip) {
        return listDataBanByIP.get(ip);
    }

    public static void addListDataBan(DataBan dataBan) {
        if (dataBan.getAddress() != null){
            listDataBanByIP.computeIfAbsent(dataBan.getAddress(),k -> new HashMap<>()).put(dataBan.getContext(), dataBan);
        }
        listDataBanByNAME.computeIfAbsent(dataBan.getName(),k -> new HashMap<>()).put(dataBan.getContext(), dataBan);
    }

    public static void removeBan(ContextBan contextBan, String name) {
        // Obtiene todos los bans por ip
        for (HashMap<ContextBan, DataBan> map : listDataBanByNAME.values()) {
            // Obtiene los bans por contexto
            for (DataBan dataBan : map.values()) {
                // Elimina el ban con el nombre igual
                if (dataBan.getName().equals(name)) map.remove(contextBan);
            }
        }
        listDataBanByNAME.computeIfAbsent(name,k -> new HashMap<>()).remove(contextBan);
    }

    public void unban(ContextBan contextBan, String name, String author) {
        AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
            removeBan(contextBan, name);
            DataSection.getDatabaseBan().removeBanPlayer(name, contextBan, author);
        });
    }

    public void banPlayer(Player player, String reason, long time, ContextBan contextBan, String nameAuthor) {
        banPlayer(player.getName(),
                player.getUniqueId(),
                player.getAddress() == null ? null : player.getAddress().getAddress(),
                reason, time, contextBan,
                nameAuthor);
    }

    public void banPlayer(@NotNull String name,
                          @NotNull UUID uuid,
                          @Nullable InetAddress ip,
                          @NotNull String reason,
                          long time,
                          @NotNull ContextBan context,
                          @NotNull String nameAuthor
    ) {
        long finalTime = time == GlobalConstantes.NUMERO_PERMA ? GlobalConstantes.NUMERO_PERMA : time == Long.MAX_VALUE ? Long.MAX_VALUE : time + System.currentTimeMillis();

        LoginData loginData = LoginManager.getDataLogin(name);
        if (loginData != null) {
            ip = loginData.getRegister().getLastAddress();
        }
        DataBan dataBan = new DataBan(name, uuid, ip, reason, finalTime, System.currentTimeMillis(), context, nameAuthor);
        Player player = Bukkit.getPlayer(name);
        if (player != null) dataBan.getContext().onBan(player, dataBan);

        AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
            DataSection.getDatabaseBan().addBanPlayer(dataBan);
            addListDataBan(dataBan);
        });
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
        if (BanManager.listDataBanByNAME.containsKey(player.getName())) {
            dataBans = BanManager.getDataBan(player.getName()).values();
            checkName = true;//Se Busca por su UUID de usuario, si está baneado
        }

        if (ip != null && BanManager.listDataBanByIP.containsKey(ip)) {
            dataBans = BanManager.getDataBan(ip).values();
            checkIp = true;//Se Busca por su ip, si está baneado
        }

        if (checkName || checkIp) {//comprueba que esté baneado por algúna de las dos razónes

            if (dataBans.isEmpty()) {//En caso qué llega a estar en la lista de baneados, pero no tiene un ban vinculado
                return IsBan.UNKNOWN;
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
                        logConsole(player.getName() + " se \"echo\" por que estar baneado de: <|" + context.name() +
                                "|>. Se detecto por Nombre: <|" + checkName + "|> por ip: <|" + checkIp + "|>. tiempo restante " +
                                "<|" +  time + "|>", TypeMessages.INFO, CategoryMessages.BAN);
                        return IsBan.YES;
                    } else {// eliminar él baneó cuando expiro y realiza en un hilo aparte para que no pete el servidor
                        unban(ban.getContext(), player.getName(), Message.BAN_AUTHOR_AUTO_BAN.getMessageLocatePrivate() + "(Expiro)");
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
        if (Objects.equals(dataBan.getContext().name(), "GLOBAL")) contextName = "XBXT";
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
