package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;
@Getter
@Setter
public class RegisterData {

    public RegisterData(String username, UUID uuidCracked, UUID uuidPremium , StateLogins state, boolean isTemporary) {
        LoginManager.getListRegister().put(username, this);
        this.username = username;
        this.stateLogins = state;
        this.uuidCracked = uuidCracked;
        this.uuidPremium = uuidPremium;
        this.isTemporary = isTemporary;
    }

    public RegisterData(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        LoginManager.getListRegister().put(username, this);
        this.username = username;
        this.stateLogins = state;
        this.uuidCracked = uuidCracked;
        this.isTemporary = isTemporary;
    }

    private final String username;
    private final StateLogins stateLogins;
    private final boolean isTemporary;
    private UUID uuidPremium;
    private UUID uuidCracked;
    private InetAddress ip;
    private String passwordShaded;

    private long lastLoginDate;
    private long registerDate;

}
