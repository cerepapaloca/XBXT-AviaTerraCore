package net.atcore.security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.DataBaseRegister;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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

public final class LoginManager {

    private static final HashMap<UUID, DataLogin> listDataLogin = new HashMap<>();

    //la llave es el nombre de usuario
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

    public static void clearDataLogin() {
        listDataLogin.clear();
    }

    public static void removeDataLogin(String name) {
        listDataLogin.remove(GlobalUtils.getUUIDByName(name));
    }

    public static HashSet<DataLogin> getDataLogin() {
        return new HashSet<>(listDataLogin.values());
    }

    public static StateLogins getStateAndRegister(InetAddress ip, String name){
        if (getDataLogin(name) != null){//se pone nulo por qué es imposible no tener un registro sin tener un login data es decir si uno da nulo el otro también
            return getDataLogin(name).getRegister().getStateLogins();
        }else{//si no existe crea un registro
            DataRegister dataRegister = startRegister(ip, name);
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
                dataRegister.setLastAddress(ip);
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
                dataRegister.setLastAddress(ip);
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
        data.setLastAddress(inetAddress);
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
        dataRegister.setLastAddress(inetAddress);
        dataRegister.setLastLoginDate(System.currentTimeMillis());
    }

    public static boolean checkLoginIn(@NotNull Player player){
        return checkLoginIn(player, true, true);
    }

    /**
     * Realiza todas comprobaciones de qué este logueado correctamente y tenga una sesión
     * valída
     *
     * @param player al jugador que se va chequear
     * @param ignoreTime sí se tiene en cuenta el tiempo de expiración o se ignora
     * @param limboMode El jugador puede llegar a entrar a modo limo si no está logueado
     * @return verdadero cuando esta logueado, falso cuando no lo está
     */

    public static boolean checkLoginIn(@NotNull Player player, boolean ignoreTime, boolean limboMode){
        if (player.getAddress() == null) return false; // Esto por qué el jugador no terminado de entrar al servidor
        DataLogin dataLogin = getDataLogin(player);
        if (dataLogin == null){
            GlobalUtils.synchronizeKickPlayer(player, "Hubo un problema con tu registro, vuelve a registrarte");
            return false;
        }

        if (dataLogin.hasSession()) {//mira si tiene una session
            DataSession dataSession = dataLogin.getSession();
            switch (dataSession.getState()) {
                case CRACKED -> {
                    if (dataLogin.getRegister().getPasswordShaded() != null) {// tiene una contraseña la cuenta?
                        if (!Config.getServerMode().equals(ServerMode.ONLINE_MODE)){// no puede ver cracked en modo online
                            if (GlobalUtils.equalIp(dataSession.getAddress(), player.getAddress().getAddress())) {// las ips tiene que ser iguales
                                if (ignoreTime || dataLogin.getSession().getEndTimeLogin() > System.currentTimeMillis()) {// expiro? o no se tiene en cuenta
                                    if (dataLogin.isLimboMode() && limboMode && player.isOnline()) {
                                        dataLogin.getLimbo().restorePlayer(player);
                                        dataLogin.setLimbo(null);
                                    }
                                    return true;// sesión válida para los cracked
                                }
                            }
                            if (limboMode) LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_SESSION);
                        }else {
                            GlobalUtils.synchronizeKickPlayer(player, "El servidor esta online mode");
                        }
                    }else {
                        if (limboMode) LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_REGISTER);
                    }
                    dataLogin.setSession(null);
                    return false;
                }
                case PREMIUM -> {
                    if (!Config.getServerMode().equals(ServerMode.OFFLINE_MODE)) {// no puede haber sesiónes premium si esta offline
                        if (GlobalUtils.equalIp(dataSession.getAddress(), player.getAddress().getAddress())){// esto no tendría que dar falso
                            if (dataSession.getSharedSecret() == null) {//no tiene el secreto compartido lo cual tiene que ser imposible
                                GlobalUtils.synchronizeKickPlayer(player, "Hubo problema con tu llave secreta vuelve a entrar al servidor. Si el problema persiste reinicie su launcher");
                                dataLogin.setSession(null);
                            } else {
                                return true;// sesión válida para los premium
                            }
                        }
                    }else {
                        if (limboMode){
                            if (dataLogin.getRegister().getPasswordShaded() != null){// la cuenta premium tiene una contraseña?
                                LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_SESSION);
                            }else {
                                LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_REGISTER);
                            }
                        }
                    }
                    dataLogin.setSession(null);
                    return false;
                }
                default -> {
                    GlobalUtils.kickPlayer(player, "Vuelve a entrar, Hubo un problema con tu cuenta");
                    return false;
                }
            }
        }else {//esto solo sucedería para los no premium
            if (dataLogin.getRegister().getPasswordShaded() != null){// la cuenta premium tiene una contraseña?
                LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_SESSION);
            }else {
                LimboManager.startSynchronizeLimboMode(player, ReasonLimbo.NO_REGISTER);
            }
            return false;
        }
    }

    public static boolean isLimboMode(@NotNull Player player){
        return getDataLogin(player).isLimboMode();
    }

    public static void onEnteringServer(@NotNull Player player){//TODO pasar esto al evento de Login
        DataLogin dataLogin = getDataLogin(player);
        if (dataLogin != null) {
            DataRegister dataRegister = dataLogin.getRegister();
            if (dataRegister != null) {
                if (Config.getServerMode().equals(ServerMode.OFFLINE_MODE) || dataRegister.getStateLogins() == StateLogins.CRACKED){
                    checkLoginIn(player, false, true);
                }
            }else{
                GlobalUtils.kickPlayer(player, "no estas registrado, vuelve a entrar al servidor");
            }
        }else {
            GlobalUtils.kickPlayer(player, "no estas registrado, vuelve a entrar al servidor");
        }
    }

    public static DataLogin startPlaySessionCracked(@NotNull Player player){
        DataLogin dataLogin = getDataLogin(player);
        DataSession dataSession = new DataSession(player, StateLogins.CRACKED);
        dataSession.setEndTimeLogin(System.currentTimeMillis() + Config.getExpirationSession());
        dataLogin.setSession(dataSession);
        dataLogin.getLimbo().restorePlayer(player);
        dataLogin.setLimbo(null);
        player.updateCommands();
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.4f, 1);
        return dataLogin;
    }

}
