package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DataSession extends BaseDataLogin {

    public DataSession(String username, UUID uuidCracked, UUID uuidPremium, StateLogins state) {
        super(username, uuidCracked, state);
        this.setUuidPremium(uuidPremium);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);

    }

    public DataSession(String username, UUID uuidCracked, StateLogins state) {
        super(username, uuidCracked ,state);
        LoginManager.getListSession().put(username, this);
        LoginManager.getListPlayerLoginIn().add(uuidCracked);
    }

    private long startTimeLogin;
    private long endTimeLogin;
    private boolean loggedDiscord;

}
