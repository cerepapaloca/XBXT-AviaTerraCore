package net.atcore.Security.Login;

import com.comphenix.protocol.PacketType;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;

public class LoginManager {

    @Getter private static final HashMap<String, SessionLogin> listSession = new HashMap<>();
    @Getter private static final HashMap<String, DataUUID> listUUIDs = new HashMap<>();

    public static StateLogins getStateLogin(String name){

    }
}
