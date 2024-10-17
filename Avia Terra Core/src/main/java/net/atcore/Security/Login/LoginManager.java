package net.atcore.Security.Login;

import com.github.games647.craftapi.resolver.RateLimitException;
import lombok.Getter;
import net.atcore.AviaTerraCore;

import java.io.IOException;
import java.util.HashMap;

public class LoginManager {

    @Getter private static final HashMap<String, SessionLogin> listSession = new HashMap<>();
    @Getter private static final HashMap<String, DataUUID> listUUIDs = new HashMap<>();

    public static StateLogins isPremium(String name){
        if (listSession.containsKey(name)){
            return listSession.get(name).getStateLogins();
        }else{
            try {
                return AviaTerraCore.getResolver().findProfile(name).isPresent() ? StateLogins.PREMIUM : StateLogins.CRACKED;
            } catch (IOException | RateLimitException e) {
                return StateLogins.UNKNOWN;
            }
        }
    }
}
