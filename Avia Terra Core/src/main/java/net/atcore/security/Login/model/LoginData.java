package net.atcore.security.Login.model;

import lombok.Getter;
import lombok.Setter;
import net.atcore.security.Login.StateLogins;

import java.util.UUID;

@Getter
@Setter
public class LoginData {

    public LoginData(RegisterData register) {
        this.register = register;
    }

    public LoginData(String username, UUID uuidCracked, UUID uuidPremium, StateLogins stateLogins, boolean isTemporary) {
        this.register = new RegisterData(username, uuidCracked, uuidPremium, stateLogins, isTemporary);
    }

    public LoginData(String username, UUID uuidCracked, StateLogins stateLogins, boolean isTemporary) {
        this.register = new RegisterData(username, uuidCracked, stateLogins, isTemporary);
    }

    final private RegisterData register;
    private SessionData session = null;
    private LimboData limbo = null;

    public boolean hasSession() {
        return session != null;
    }

    public boolean isLimboMode() {
        return limbo != null;
    }
}
