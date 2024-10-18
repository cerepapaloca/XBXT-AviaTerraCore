package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SessionLogin extends RegisterData {

    private long startTimeLogin;
    private long endTimeLogin;
    private boolean loggedDiscord;

    public SessionLogin(String username, UUID uuidCracked, UUID uuidPremium, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, uuidPremium, state, isTemporary);
        LoginManager.getListRegister().remove(username);
        LoginManager.getListSession().put(username, this);
    }

    public SessionLogin(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, state, isTemporary);
        LoginManager.getListRegister().remove(username);
        LoginManager.getListSession().put(username, this);
    }
}
