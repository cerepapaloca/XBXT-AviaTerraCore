package net.atcore.Security.Login;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Data.RegisterDataBase;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class LoginManager {

    //la llave es el nombre de usuario
    @Getter private static final HashMap<String, DataSession> listSession = new HashMap<>();
    @Getter private static final HashMap<String, DataRegister> listRegister = new HashMap<>();

    @Getter private static final HashSet<UUID> listPlayerLoginIn = new HashSet<>();

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
                RegisterDataBase.addRegister(dataRegister.getUsername(),
                        profileObj.getId().toString(), GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        true, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
            }else {//es temporal el registro por qué no ha puesto la contraseña
                dataRegister = new DataRegister(name, GlobalUtils.getUUIDByName(name), StateLogins.CRACKED, true);
                RegisterDataBase.addRegister(dataRegister.getUsername(),
                        null, GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        false, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
            }
            return dataRegister;
        } catch (IOException | RateLimitException e) {
            return null;
        }
    }

    public static void addPassword(@NotNull String name, @NotNull String password){
        try {
            listRegister.get(name).setPasswordShaded(hashPassword(name ,password));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int ITERATIONS = 65536; // Número de iteraciones
    private static final int KEY_LENGTH = 256; // Longitud del hash (bits)
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

    private static void saveRegisterData(@NotNull DataRegister register){
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {

        });
    }

    public static boolean isLoginIn(Player player, boolean force){
        if (listSession.containsKey(player.getName())){
            DataSession dataSession = listSession.get(player.getName());
            if (dataSession.getIp() == Objects.requireNonNull(player.getAddress()).getAddress()){
                if (!force) return true;
                if (dataSession.getEndTimeLogin() > System.currentTimeMillis()){
                    return true;
                }
            }
        }
        listPlayerLoginIn.remove(player.getUniqueId());
        listSession.remove(player.getName());
        return false;
    }
}
