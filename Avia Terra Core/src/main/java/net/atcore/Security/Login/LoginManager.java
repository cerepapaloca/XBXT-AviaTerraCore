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
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

public class LoginManager {

    //la llave es el nombre de usuario
    @Getter private static final HashMap<String, SessionLogin> listSession = new HashMap<>();
    @Getter private static final HashMap<String, RegisterData> listRegister = new HashMap<>();

    public static StateLogins getState(InetAddress ip, String name){
        if (listRegister.containsKey(name)){
            return listRegister.get(name).getStateLogins();
        }else{//si no existe crea un registro
            RegisterData registerData = startRegister(ip, name);
            if (registerData != null){
                return registerData.getStateLogins();
            }else {
                return StateLogins.UNKNOWN;
            }
        }
    }

    private static @Nullable RegisterData startRegister(InetAddress ip ,@NotNull String name){
        try {
            MojangResolver resolver = AviaTerraCore.getResolver();
            Optional<Profile> profile = resolver.findProfile(name);
            RegisterData registerData;
            if (profile.isPresent()){
                Profile profileObj = profile.get();
                registerData = new RegisterData(profileObj.getName(), GlobalUtils.getUUIDByName(name), profileObj.getId(), StateLogins.PREMIUM, false);
                RegisterDataBase.addRegister(registerData.getUsername(),
                        profileObj.getId().toString(), GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        true, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
                return registerData;
            }else {//es temporal el registro por qué no ha puesto la contraseña
                registerData = new RegisterData(name, GlobalUtils.getUUIDByName(name), StateLogins.CRACKED, true);
                RegisterDataBase.addRegister(registerData.getUsername(),
                        null, GlobalUtils.getUUIDByName(name).toString(),
                        ip.getHostName(), ip.getHostName(),
                        false, null,
                        System.currentTimeMillis(), System.currentTimeMillis()
                );
                return registerData;
            }
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

    private static void saveRegisterData(@NotNull RegisterData register){
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {

        });
    }
}
