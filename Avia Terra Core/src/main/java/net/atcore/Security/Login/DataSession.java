package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DataSession extends BaseDataLogin {

    public DataSession(String username, UUID uuidCracked, UUID uuidPremium, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, isTemporary, state);
        this.setUuidPremium(uuidPremium);
        LoginManager.getListSession().put(username, this);
    }

    public DataSession(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, isTemporary ,state);
        LoginManager.getListSession().put(username, this);
    }

    private long startTimeLogin;
    private long endTimeLogin;
    private boolean loggedDiscord;

}
