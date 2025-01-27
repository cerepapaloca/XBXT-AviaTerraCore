package net.atcore.security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.EncryptService;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.model.RegisterData;
import net.atcore.security.Login.model.SessionData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import static net.atcore.data.sql.DataBaseRegister.*;

@UtilityClass
public final class LoginManager {

    private final HashMap<UUID, LoginData> LIST_DATA_LOGIN = new HashMap<>();

    //la llave es el nombre de usuario
    public LoginData getDataLogin(String name) {
        return LIST_DATA_LOGIN.get(GlobalUtils.getUUIDByName(name));
    }

    /**
     * Retorna Login Data a través de la uuid real
     * @param uuid La uuid real del jugador para sacar la uuid real es con {@link GlobalUtils#getRealUUID(Player)}
     */

    public LoginData getDataLogin(UUID uuid) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(uuid)){
            FloodgatePlayer playerFP = FloodgateApi.getInstance().getPlayer(uuid);
            return LIST_DATA_LOGIN.get(GlobalUtils.getUUIDByName(playerFP.getUsername()));
        }else {
            return LIST_DATA_LOGIN.get(uuid);
        }

    }

    public LoginData getDataLogin(@NotNull Player player) {
        return LIST_DATA_LOGIN.get(GlobalUtils.getUUIDByName(GlobalUtils.getRealName(player)));
    }

    public @NotNull LoginData addDataLogin(String name , RegisterData registerData) {
        LoginData loginData = new LoginData(registerData);
        LIST_DATA_LOGIN.put(GlobalUtils.getUUIDByName(name) , loginData);
        return loginData;
    }

    public void clearDataLogin() {
        LIST_DATA_LOGIN.clear();
    }

    public void removeDataLogin(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                FloodgatePlayer playerFP = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                LIST_DATA_LOGIN.remove(GlobalUtils.getUUIDByName(playerFP.getUsername()));
            }
        }
        LIST_DATA_LOGIN.remove(GlobalUtils.getUUIDByName(name));
    }

    @Contract(" -> new")
    public @NotNull HashSet<LoginData> getDataLogin() {
        return new HashSet<>(LIST_DATA_LOGIN.values());
    }

    public boolean isLimboMode(Player player) {
        LoginData loginData = getDataLogin(player);
        if (loginData == null) return false;
        return loginData.isLimboMode();
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
            RegisterData registerData = startRegister(ip, name);
            if (registerData != null){
                addDataLogin(name, registerData);
                return registerData.getStateLogins();
            }else {
                return StateLogins.UNKNOWN;
            }
        }
    }

    private @Nullable RegisterData startRegister(InetAddress ip, @NotNull String name){
        try {
            MojangResolver resolver = AviaTerraCore.getResolver();
            Optional<Profile> profile = resolver.findProfile(name);
            RegisterData registerData;
            if (profile.isPresent()){
                Profile profileObj = profile.get();
                registerData = new RegisterData(profileObj.getName(), GlobalUtils.getUUIDByName(name), profileObj.getId(), StateLogins.SEMI_CRACKED, true);
                registerData.setLastAddress(ip);
                // Se guarda el registro en la base de datos
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        DataBaseRegister.addRegister(registerData.getUsername(),
                        profileObj.getId().toString(),
                                GlobalUtils.getUUIDByName(name).toString(),
                                ip.getHostAddress(),
                                ip.getHostAddress(),
                                StateLogins.SEMI_CRACKED,
                                null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));

            }else {// Es temporal el registro por qué no ha puesto la contraseña
                registerData = new RegisterData(name, GlobalUtils.getUUIDByName(name), StateLogins.CRACKED, true);
                registerData.setLastAddress(ip);
                //se guarda el registro en la base de datos
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                        DataBaseRegister.addRegister(registerData.getUsername(),
                                null,
                                GlobalUtils.getUUIDByName(name).toString(),
                                ip.getHostAddress(),
                                ip.getHostAddress(),
                                StateLogins.CRACKED,
                                null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));
            }
            return registerData;
        } catch (IOException | RateLimitException e) {
            MessagesManager.sendWaringException("Error al iniciar el registro del jugador", e);
            return null;
        }
    }

    @Contract(pure = true)
    public boolean isEqualPassword(@NotNull Player player, @NotNull String password)  {
        String name = GlobalUtils.getRealName(player);
        return EncryptService.hashPassword(name, password).equals(getDataLogin(name).getRegister().getPasswordShaded());
    }

    /**
     * El usuario pasa de modo limbo a modo play comenzado a jugar y creando
     * una sesión válida Cracked
     *
     * @param player El usuario que iniciar sessión
     */

    public static @NotNull LoginData startPlaySessionCracked(@NotNull Player player){
        LoginData loginData = getDataLogin(player);
        SessionData sessionData = new SessionData(player, StateLogins.CRACKED);//TODO: Cambiar si es un Cracked o un semi cracked
        sessionData.setEndTimeLogin(Config.getExpirationSession());
        loginData.setSession(sessionData);
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            loginData.getLimbo().restorePlayer(player);
            player.updateCommands();
        });
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.4f, 1);
        new BukkitRunnable() {
            public void run() {
                GlobalUtils.addRangeVote(player);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*3);
        MessagesManager.logConsole(String.format("Inicio de sesión cracked valida para <|%s|>", player.getName()) , TypeMessages.SUCCESS, CategoryMessages.LOGIN);
        return loginData;
    }

    /**
     * No crea un nuevo registro como tal solo añade la contraseña
     * a su registro asi completado el registro de los cracked
     *
     * @param player El usuario
     * @param password La contraseña sin cifrar
     */

    public void newRegisterCracked(@NotNull Player player, @NotNull String password){
        String name = GlobalUtils.getRealName(player);
        String s = EncryptService.hashPassword(name ,password);
        RegisterData data = getDataLogin(name).getRegister();
        data.setTemporary(false);
        data.setPasswordShaded(s);
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            if (!updatePassword(name, s)){
                GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_PASSWORD_ERROR);
            }
        });
        updateLoginDataBase(name, player.getAddress().getAddress());
    }

    public void updateLoginDataBase(String name, InetAddress inetAddress){
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            boolean b = updateAddress(name, inetAddress.getHostAddress()) && updateLoginDate(name, System.currentTimeMillis());
            if (!b) { // En caso de un error hace un kick al jugador para que vuelva a entrar
                Player player = Bukkit.getPlayer(name);
                if (player == null) return;
                GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_ADDRESS_ERROR);
            }else {
                MessagesManager.logConsole(String.format("Se creó exitosamente el registro de %s", name) , TypeMessages.SUCCESS, CategoryMessages.LOGIN);
            }
        });
        RegisterData registerData = getDataLogin(name).getRegister();
        registerData.setLastAddress(inetAddress);
        registerData.setLastLoginDate(System.currentTimeMillis());
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
     * @return verdadero cuando está logueado, falso cuando no lo está
     */

    public boolean checkLoginIn(@NotNull Player player, boolean ignoreTime, boolean limboMode){
        if (player.getAddress() == null) return false; // Esto por qué el jugador no terminado de entrar al servidor
        LoginData loginData = getDataLogin(player);
        if (loginData == null){
            GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_REGISTER_ERROR);
            return false;
        }
        if (loginData.hasSession()) {// Mira si tiene una session
            SessionData sessionData = loginData.getSession();
            switch (sessionData.getState()) {
                case CRACKED, SEMI_CRACKED -> {
                    if (loginData.getRegister().getPasswordShaded() != null) {// tiene una contraseña la cuenta?
                        if (!Config.getServerMode().equals(ServerMode.ONLINE_MODE)){// no puede ver cracked en modo online
                            if (GlobalUtils.equalIp(sessionData.getAddress(), player.getAddress().getAddress())) {// las ips tiene que ser iguales
                                if (ignoreTime || loginData.getSession().getEndTimeLogin() > System.currentTimeMillis()) {// expiro? o no se tiene en cuenta
                                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
                                        if (loginData.isLimboMode() && limboMode && player.isOnline()) {
                                            loginData.getLimbo().restorePlayer(player);
                                        }
                                    });
                                    return true;// sesión válida para los cracked
                                }
                            }
                            if (limboMode) LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_SESSION);
                        }else {
                            GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_ONLINE_MODE);
                        }
                    }else {
                        if (limboMode) LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_REGISTER);
                    }
                    loginData.setSession(null);
                    return false;
                }
                case PREMIUM -> {
                    if (!Config.getServerMode().equals(ServerMode.OFFLINE_MODE)) {// no puede haber sesiónes premium si esta offline
                        if (GlobalUtils.equalIp(sessionData.getAddress(), player.getAddress().getAddress())){// esto no tendría que dar falso
                            if (sessionData.getSharedSecret() == null) {// no tiene el secreto compartido lo cual tiene que ser imposible.
                                GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_KEY_ERROR);
                                loginData.setSession(null);
                            } else {
                                return true;// sesión válida para los premium
                            }
                        }
                    }else {
                        if (limboMode){
                            if (loginData.getRegister().getPasswordShaded() != null){// la cuenta premium tiene una contraseña?
                                LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_SESSION);
                            }else {
                                LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_REGISTER);
                            }
                        }
                    }
                    loginData.setSession(null);
                    return false;
                }
                default -> {
                    GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_UNKNOWN_STATE.getMessage(player));
                    return false;
                }
            }
        }else {// Esto solo sucedería para los no premium
            if (loginData.getRegister().getPasswordShaded() != null){
                LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_SESSION);
            }else {
                LimboManager.startAsynchronouslyLimboMode(player, ReasonLimbo.NO_REGISTER);
            }
            return false;
        }
    }

    /*NO QUITAR EL STATIC POR QUE SINO SE BUEGA EL LOMBOK*/
    public static void onEnteringServer(@NotNull Player player){
        LoginData loginData = getDataLogin(player);
        if (loginData != null) {
            RegisterData registerData = loginData.getRegister();
            if (registerData != null) {
                if (Config.getServerMode().equals(ServerMode.OFFLINE_MODE) || registerData.getStateLogins() != StateLogins.PREMIUM){
                    checkLoginIn(player, false, true);
                }
                AviaTerraCore.enqueueTaskAsynchronously(() -> {
                    if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()) && loginData.getRegister().getUuidBedrock() == null) {
                        DataBaseRegister.addUUIDBedrock(GlobalUtils.getRealName(player), player.getUniqueId());
                        loginData.getRegister().setUuidBedrock(player.getUniqueId());
                    }
                });
            }else{
                GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_NO_REGISTER.getMessage(player));
            }
        }else {
            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_NO_REGISTER.getMessage(player));
        }
    }
}
