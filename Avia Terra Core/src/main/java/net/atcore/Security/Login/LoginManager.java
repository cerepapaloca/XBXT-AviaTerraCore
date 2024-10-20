package net.atcore.Security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Data.DataBaseRegister;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static net.atcore.Data.DataBaseRegister.*;

public class LoginManager {

    //la llave es el nombre de usuario
    @Getter private static final HashMap<String, DataSession> listSession = new HashMap<>();
    @Getter private static final HashMap<String, DataRegister> listRegister = new HashMap<>();

    @Getter private static final HashSet<UUID> listPlayerLoginIn = new HashSet<>();//esto existe por qué más optimizado que los hashMap

    public static StateLogins getState(InetAddress ip, String name){
        if (listRegister.containsKey(name)){
            return listRegister.get(name).getStateLogins();
        }else{//si no existe crea un registro
            DataRegister dataRegister = startRegister(ip, name);
            if (dataRegister != null){
                return dataRegister.getStateLogins();
            }else {
                return StateLogins.UNKNOWN;
            }
        }
    }

    private static @Nullable DataRegister startRegister(InetAddress ip , @NotNull String name){
        try {
            MojangResolver resolver = AviaTerraCore.getResolver();
            Optional<Profile> profile = resolver.findProfile(name);
            DataRegister dataRegister;
            if (profile.isPresent()){
                Profile profileObj = profile.get();
                dataRegister = new DataRegister(profileObj.getName(), GlobalUtils.getUUIDByName(name), profileObj.getId(), StateLogins.PREMIUM, false);
                dataRegister.setIp(ip);
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> DataBaseRegister.addRegister(dataRegister.getUsername(),
                        profileObj.getId().toString(), GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        true, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                ));

            }else {//es temporal el registro por qué no ha puesto la contraseña
                dataRegister = new DataRegister(name, GlobalUtils.getUUIDByName(name), StateLogins.CRACKED, true);
                dataRegister.setIp(ip);
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> DataBaseRegister.addRegister(dataRegister.getUsername(),
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

    public static String hashPassword(@NotNull String name, @NotNull String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String s = name + password;//combina el nombre de usuario y la contraseña
        PBEKeySpec spec = new PBEKeySpec(s.toCharArray(), password.getBytes(), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean isEqualPassword(@NotNull String name, @NotNull String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return hashPassword(name, password).equals(getListRegister().get(name).getPasswordShaded());
    }

    public static void newRegisterCracked(@NotNull String name, @NotNull InetAddress inetAddress , @NotNull String password){
        String s;
        try {
            s = hashPassword(name ,password);
            listRegister.get(name).setPasswordShaded(s);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        DataRegister data = listRegister.get(name);
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
        DataRegister dataRegister = listRegister.get(name);
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

    public static boolean checkLoginIn(Player player, boolean ignoreTime){
        if (listSession.containsKey(player.getName())){//mira si tiene una session
            DataSession dataSession = listSession.get(player.getName());
            if (dataSession.getStateLogins() == StateLogins.CRACKED && dataSession.getPasswordShaded() != null){//tiene contraseña para la cuenta cracked
                if (Objects.equals(dataSession.getIp().getHostName().split(":")[0], Objects.requireNonNull(player.getAddress()).getAddress().getHostName().split(":")[0])){
                    if (ignoreTime || dataSession.getEndTimeLogin() > System.currentTimeMillis()){//expiro? o no se tiene en cuenta
                        player.setGameMode(GameMode.SURVIVAL);
                        return true;
                    }
                }
            }
        }
        player.setGameMode(GameMode.SPECTATOR);
        listPlayerLoginIn.remove(player.getUniqueId());
        listSession.remove(player.getName());
        return false;
    }

    public static void startTimeOut(Player player, String reason){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (checkLoginIn(player, true)) return;
                GlobalUtils.kickPlayer(player, reason);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*20);
    }
}
