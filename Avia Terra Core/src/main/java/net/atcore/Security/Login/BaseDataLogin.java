package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;
@Getter
@Setter
public abstract class BaseDataLogin {

    public BaseDataLogin(String username, UUID uuidCracked, StateLogins stateLogins) {
        this.username = username;
        this.uuidCracked = uuidCracked;
        this.stateLogins = stateLogins;
    }

    private final String username;
    private final StateLogins stateLogins;
    private UUID uuidPremium;
    private UUID uuidCracked;
    private InetAddress ip;
    private String passwordShaded;
}
