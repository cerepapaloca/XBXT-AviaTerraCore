package net.atcore.security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static net.atcore.data.DataBaseRegister.*;
import static net.atcore.messages.MessagesManager.sendMessage;

public final class LoginManager {

    private static final HashMap<UUID, DataLogin> listDataLogin = new HashMap<>();

    //la llave es el nombre de usuario

    @Getter private static final HashSet<UUID> listPlayerLoginIn = new HashSet<>();//esto existe por qué más optimizado que los hashMap

    public static DataLogin getDataLogin(String name) {
        return listDataLogin.get(GlobalUtils.getUUIDByName(name));
    }

    public static DataLogin getDataLogin(UUID uuid) {
        return listDataLogin.get(uuid);
    }

    public static DataLogin getDataLogin(Player player) {
        return listDataLogin.get(player.getUniqueId());
    }

    public static DataLogin addDataLogin(String name ,DataRegister dataRegister) {
        DataLogin dataLogin = new DataLogin(dataRegister);
        listDataLogin.put(GlobalUtils.getUUIDByName(name) ,dataLogin);
        return dataLogin;
    }

    public static boolean isNewPlayer(String name) {
        return listPlayerLoginIn.contains(GlobalUtils.getUUIDByName(name));
    }

    public static void clearDataLogin() {
        listDataLogin.clear();
    }

    public static HashSet<DataLogin> getDataLogin() {
        return new HashSet<>(listDataLogin.values());
    }

    public static StateLogins getStateAndRegister(InetAddress ip, String name){
        if (getDataLogin(name) != null){//se pone nulo por qué es imposible no tener un registro sin tener un login data es decir si uno da nulo el otro también
            return getDataLogin(name).getRegister().getStateLogins();
        }else{//si no existe crea un registro
            DataRegister dataRegister = startRegister(ip, name);
            Bukkit.getLogger().warning("Register successful");
            if (dataRegister != null){
                addDataLogin(name, dataRegister);
                return dataRegister.getStateLogins();
            }else {
                return StateLogins.UNKNOWN;
            }
        }
    }

    private static @Nullable DataRegister startRegister(InetAddress ip, @NotNull String name){
        try {
            MojangResolver resolver = AviaTerraCore.getResolver();
            Optional<Profile> profile = resolver.findProfile(name);
            DataRegister dataRegister;
            if (profile.isPresent()){
                Profile profileObj = profile.get();
                dataRegister = new DataRegister(profileObj.getName(), GlobalUtils.getUUIDByName(name), profileObj.getId(), StateLogins.PREMIUM, false);
                dataRegister.setIp(ip);
                //se guarda el registro en la base de datos
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        DataBaseRegister.addRegister(dataRegister.getUsername(),
                        profileObj.getId().toString(), GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        true, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));

            }else {//es temporal el registro por qué no ha puesto la contraseña
                dataRegister = new DataRegister(name, GlobalUtils.getUUIDByName(name), StateLogins.CRACKED, true);
                dataRegister.setIp(ip);
                //se guarda el registro en la base de datos
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        DataBaseRegister.addRegister(dataRegister.getUsername(),
                        null, GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        false, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));
            }
            return dataRegister;
        } catch (IOException | RateLimitException e) {
            return null;
        }
    }

    private static final int ITERATIONS = 65536; //número de iteraciones
    private static final int KEY_LENGTH = 256; //longitud del hash (bits)
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Crea un hash seguro combina el nombre de usuario y la contraseña en la misma contraseña
     * y la "sal" que sería la como la semilla del mundo es la contraseña en byte
     */

    public static String hashPassword(@NotNull String name, @NotNull String password) {
        String s = name + password;// combina el nombre de usuario y la contraseña
        PBEKeySpec spec = new PBEKeySpec(s.toCharArray(), password.getBytes(), ITERATIONS, KEY_LENGTH);
        byte[] hash;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            hash = skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(hash);
    }

    @Pure
    public static boolean isEqualPassword(@NotNull String name, @NotNull String password)  {
        return hashPassword(name, password).equals(getDataLogin(name).getRegister().getPasswordShaded());
    }

    public static void newRegisterCracked(@NotNull String name, @NotNull InetAddress inetAddress , @NotNull String password){
        String s = hashPassword(name ,password);
        getDataLogin(name).getRegister().setPasswordShaded(s);
        DataRegister data = getDataLogin(name).getRegister();
        data.setTemporary(false);
        data.setIp(inetAddress);
        data.setLastLoginDate(System.currentTimeMillis());
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            updateLoginDate(name, System.currentTimeMillis());
            updatePassword(name, s);
        });
    }

    public static void updateLoginDataBase(String name, InetAddress inetAddress){
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            updateLoginDate(name, System.currentTimeMillis());
            updateAddress(name, inetAddress.getHostAddress().replace("/",""));
        });
        DataRegister dataRegister = getDataLogin(name).getRegister();
        dataRegister.setIp(inetAddress);
        dataRegister.setLastLoginDate(System.currentTimeMillis());
    }

    /**
     * Chequea si el jugador está logueado correctamente y lo cambia de modo de juego
     * si está logueado correctamente.
     * @param player al jugador que se va chequear
     * @param ignoreTime sí se tiene en cuenta el tiempo de expiración
     * @return verdadero cuando esta logueado, falso cuando no lo está
     */

    public static boolean checkLoginIn(Player player, boolean ignoreTime){//nota tengo que optimizar esto
        DataLogin dataLogin = getDataLogin(player);
        if (dataLogin == null){
            if (Bukkit.isPrimaryThread()){
                GlobalUtils.kickPlayer(player, "Hubo un problema con tu registro, vuelve a registrarte");
            }else{
                Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () ->
                        GlobalUtils.kickPlayer(player, "Hubo un problema con tu registro, vuelve a registrarte"));
            }
            return false;
        }

        if (dataLogin.hasSession()){//mira si tiene una session
            // revisa si tiene una contraseña la cuenta cracked o si es un usuario premium
            if ((dataLogin.getSession().getState() == StateLogins.CRACKED && dataLogin.getRegister().getPasswordShaded() != null) ||
                    dataLogin.getSession().getState() == StateLogins.PREMIUM){
                // mira si la ip son iguales
                if (Objects.equals(dataLogin.getSession().getAddress().getHostName().split(":")[0], Objects.requireNonNull(player.getAddress()).getAddress().getHostName().split(":")[0])){
                    if (ignoreTime || dataLogin.getSession().getEndTimeLogin() > System.currentTimeMillis()){//expiro? o no se tiene en cuenta
                        listPlayerLoginIn.add(player.getUniqueId());
                        // en caso de que sea op no se le cambia el modo de juego y se hace en el hilo principal por si acaso
                        if (!player.isOp()) {
                            if (Bukkit.isPrimaryThread()){
                                player.setGameMode(GameMode.SURVIVAL);
                            }else{
                                Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> player.setGameMode(GameMode.SURVIVAL));
                            }
                        }
                        return true;
                    }
                }
            }
        }
        // en caso que no sea valida
        if (Bukkit.isPrimaryThread()){
            player.setGameMode(GameMode.SPECTATOR);
        }else{
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> player.setGameMode(GameMode.SPECTATOR));
        }
        listPlayerLoginIn.remove(player.getUniqueId());
        dataLogin.setSession(null);// esto lo borra

        if (ServerMode.OFFLINE_MODE != Config.getServerMode() && LoginManager.getDataLogin(player).getRegister().getStateLogins().equals(StateLogins.PREMIUM)){
            GlobalUtils.kickPlayer(player, "Vuelve a entrar para obtener una sesión nueva");
        }

        return false;
    }

    public static void checkJoin(@NotNull Player player){
        DataLogin dataLogin = getDataLogin(player);
        if (dataLogin != null) {
            dataLogin.setLimbo(new DataLimbo(player));
            DataRegister dataRegister = dataLogin.getRegister();
            if (dataRegister != null) {
                if (Config.getServerMode().equals(ServerMode.OFFLINE_MODE) || dataRegister.getStateLogins() == StateLogins.CRACKED){
                    if (dataRegister.getPasswordShaded() != null) {//tiene contraseña o no
                        if (checkLoginIn(player, false)) {//si tiene una session valida o no
                            dataLogin.setLimbo(null);
                        }else {
                            player.getInventory().clear();
                            player.teleport(new Location(player.getWorld(),0,100,0));
                            startMessage(player, "login porfa. <|/login <Contraseña>|>");
                            startTimeOut(player, "Tardaste mucho en iniciar sesión");
                        }
                    }else{
                        startTimeOut(player, "Tardaste mucho en registrarte");
                        startMessage(player, "registrate porfa. <|/register <Contraseña> <Contraseña>|> &oNota de Ceres:" +
                                " esto algo que esta en desarrollo ponga cualquier contraseña como su nombre de usuario");
                    }
                }
            }else{
                GlobalUtils.kickPlayer(player, "no estas registrado, vuelve a entrar al servidor");
            }
        }else {
            GlobalUtils.kickPlayer(player, "no estas registrado, vuelve a entrar al servidor");
        }


        /*if (LoginManager.getListSession().get(player.getName()).getUuidPremium() != null) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz logueado!"),
                    ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "&oCuenta premium"), 20, 20*3, 40);
        }*/
    }

    public static DataLogin startPlaySessionCracked(@NotNull Player player){
        DataLogin dataLogin = getDataLogin(player);
        DataSession dataSession = new DataSession(player, StateLogins.CRACKED);
        dataSession.setEndTimeLogin(System.currentTimeMillis() + Config.getExpirationSession());
        dataLogin.setSession(dataSession);
        player.getInventory().setContents(dataLogin.getLimbo().getItems());
        player.teleport(dataLogin.getLimbo().getLocation());
        if (player.isOp()) {
            player.setGameMode(dataLogin.getLimbo().getGameMode());
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
        return dataLogin;
    }

    public static void startTimeOut(Player player, String reason){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (checkLoginIn(player, true)) return;
                GlobalUtils.kickPlayer(player, reason);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*30);
    }

    public static void startMessage(Player player, String message){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()){
                    cancel();
                    return;
                }
                if (checkLoginIn(player, true)){
                    cancel();
                }else{
                    sendMessage(player, message, TypeMessages.INFO);
                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 20*2, 20*2);
    }
}
