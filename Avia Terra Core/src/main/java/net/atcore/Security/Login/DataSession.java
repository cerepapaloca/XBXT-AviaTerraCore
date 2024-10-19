package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.util.UUID;

@Getter
@Setter
public class DataSession extends BaseDataLogin {

    public DataSession(String username, UUID uuidCracked, UUID uuidPremium, StateLogins state) {
        super(username, uuidCracked, state);
        this.setUuidPremium(uuidPremium);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);
        startTimeLogin = System.currentTimeMillis();

    }

    public DataSession(String username, UUID uuidCracked, StateLogins state) {
        super(username, uuidCracked ,state);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);
        startTimeLogin = System.currentTimeMillis();
    }

    private long startTimeLogin;
    private long endTimeLogin;
    private boolean loggedDiscord;

}
