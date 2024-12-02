package net.atcore.security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static net.atcore.data.sql.DataBaseRegister.*;

@UtilityClass
public final class LoginManager {

    private final HashMap<UUID, DataLogin> listDataLogin = new HashMap<>();

    //la llave es el nombre de usuario
    public DataLogin getDataLogin(String name) {
        return listDataLogin.get(GlobalUtils.getUUIDByName(name));
    }

    public DataLogin getDataLogin(UUID uuid) {
        return listDataLogin.get(uuid);
    }

    public DataLogin getDataLogin(Player player) {
        return listDataLogin.get(player.getUniqueId());
    }

    public DataLogin addDataLogin(String name ,DataRegister dataRegister) {
        DataLogin dataLogin = new DataLogin(dataRegister);
        listDataLogin.put(GlobalUtils.getUUIDByName(name) ,dataLogin);
        return dataLogin;
    }

    public void clearDataLogin() {
        listDataLogin.clear();
    }

    public void removeDataLogin(String name) {
        listDataLogin.remove(GlobalUtils.getUUIDByName(name));
    }

    public HashSet<DataLogin> getDataLogin() {
        return new HashSet<>(listDataLogin.values());
    }

    public boolean isLimboMode(Player player) {
        return getDataLogin(player).isLimboMode();
    }

    /**
     * Se obtiene si es un premium o un cracked en caso de que no este registrado
     * inicia {@link #startRegister(InetAddress, String) startRegister} teniendo en
     * cuenta si es premium o cracked
     * @param ip La ip conque se va loguear o registrar
     * @param name El nombre real del usuario
     * @return Si el usuario es premium o cracked
     */

    public StateLogins getStateAndRegister(InetAddress ip, String name){
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

    private @Nullable DataRegister startRegister(InetAddress ip, @NotNull String name){
        try {
            MojangResolver resolver = AviaTerraCore.getResolver();
            Optional<Profile> profile = resolver.findProfile(name);
            DataRegister dataRegister;
            if (profile.isPresent()){
                Profile profileObj = profile.get();
                dataRegister = new DataRegister(profileObj.getName(), GlobalUtils.getUUIDByName(name), profileObj.getId(), StateLogins.PREMIUM, false);
                dataRegister.setLastAddress(ip);
                // Se guarda el registro en la base de datos
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        DataBaseRegister.addRegister(dataRegister.getUsername(),
                        profileObj.getId().toString(), GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        true, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));

            }else {// Es temporal el registro por qué no ha puesto la contraseña
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

    private final int ITERATIONS = 65536; //número de iteraciones
    private final int KEY_LENGTH = 256; //longitud del hash (bits)
    private final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Crea un hash seguro combina el nombre de usuario y la contraseña en la misma contraseña
     */

    @NotNull
    @Contract(pure = true)
    public String hashPassword(String name, String password) {
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

    @Contract(pure = true)
    public boolean isEqualPassword(@NotNull String name, @NotNull String password)  {
        return hashPassword(name, password).equals(getDataLogin(name).getRegister().getPasswordShaded());
    }

    /**
     * El usuario pasa de modo limbo a modo play comenzado a jugar y creando
     * una sesión válida Cracked
     *
     * @param player El usuario que iniciar sessión
     */

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

    /**
     * No crea un nuevo registro como tal solo añade la contraseña
     * a su registro asi completado el registro de los cracked
     *
     * @param name Nombre del usuario
     * @param inetAddress La ip con que va registrar
     * @param password La contraseña sin cifrar
     */

    public void newRegisterCracked(@NotNull String name, @NotNull InetAddress inetAddress , @NotNull String password){
        String s = hashPassword(name ,password);
        getDataLogin(name).getRegister().setPasswordShaded(s);
        DataRegister data = getDataLogin(name).getRegister();
        data.setTemporary(false);
        data.setLastAddress(inetAddress);
        data.setLastLoginDate(System.currentTimeMillis());
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            updateLoginDate(name, System.currentTimeMillis());
            updatePassword(name, s);
        });
    }

    public void updateLoginDataBase(String name, InetAddress inetAddress){
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            updateLoginDate(name, System.currentTimeMillis());
            updateAddress(name, inetAddress.getHostAddress().replace("/",""));
        });
        DataRegister dataRegister = getDataLogin(name).getRegister();
        dataRegister.setLastAddress(inetAddress);
        dataRegister.setLastLoginDate(System.currentTimeMillis());
    }

    public boolean checkLoginIn(@NotNull Player player){
        return checkLoginIn(player, true, true);
    }

    /**
     * Realiza todas comprobaciones de qué este logueado correctamente y que tenga una sesión
     * valída. Esto se puede usar afuera del hilo principal del servidor
     *
     * @param player al jugador que se va chequear
     * @param ignoreTime sí se tiene en cuenta el tiempo de expiración o se ignora
     * @param limboMode El jugador puede llegar a entrar a modo limo si no está logueado
     * @return verdadero cuando esta logueado, falso cuando no lo está
     */

    public boolean checkLoginIn(@NotNull Player player, boolean ignoreTime, boolean limboMode){
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
                            if (dataSession.getSharedSecret() == null) {// no tiene el secreto compartido lo cual tiene que ser imposible.
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

    public void onEnteringServer(@NotNull Player player){//TODO pasar esto al evento de Login
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
}
