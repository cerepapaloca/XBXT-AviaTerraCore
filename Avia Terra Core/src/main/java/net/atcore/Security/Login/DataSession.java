package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@Setter
public class DataSession extends BaseDataLogin {

    public DataSession(String username, UUID uuidCracked, UUID uuidPremium, StateLogins state, InetAddress inetAddress) {
        super(username, uuidCracked, state);
        this.setUuidPremium(uuidPremium);
        this.setIp(inetAddress);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);
        LoginManager.updateLoginDataBase(username, inetAddress);
        startTimeLogin = System.currentTimeMillis();

    }

    public DataSession(String username, UUID uuidCracked, StateLogins state, InetAddress inetAddress) {
        super(username, uuidCracked ,state);
        this.setIp(inetAddress);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);
        LoginManager.updateLoginDataBase(username, inetAddress);
        startTimeLogin = System.currentTimeMillis();
    }

    private long startTimeLogin;
    private long endTimeLogin;
    private boolean loggedDiscord;

}
