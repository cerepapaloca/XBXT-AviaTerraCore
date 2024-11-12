package net.atcore.security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@Setter
public class DataRegister {

    public DataRegister(String username, UUID uuidCracked, UUID uuidPremium , StateLogins state, boolean isTemporary) {
        this.username = username;
        this.uuidCracked = uuidCracked;
        this.stateLogins = state;
        this.setUuidPremium(uuidPremium);
        this.isTemporary = isTemporary;
    }

    public DataRegister(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        this.username = username;
        this.uuidCracked = uuidCracked;
        this.stateLogins = state;
        this.isTemporary = isTemporary;
    }

    private final String username;
    private final StateLogins stateLogins;
    private UUID uuidPremium;
    private UUID uuidCracked;
    private InetAddress ip;
    private String passwordShaded;
    private String gmail;
    private String discord;

    private boolean isTemporary;
    private long lastLoginDate;
    private long registerDate;
    private InetAddress addressRegister;

}
