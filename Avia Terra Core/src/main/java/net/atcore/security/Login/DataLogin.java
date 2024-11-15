package net.atcore.security.Login;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class DataLogin {

    public DataLogin(DataRegister register) {
        this.register = register;
    }

    public DataLogin(String username, UUID uuidCracked, UUID uuidPremium, StateLogins stateLogins, boolean isTemporary) {
        this.register = new DataRegister(username, uuidCracked, uuidPremium, stateLogins, isTemporary);
    }

    public DataLogin(String username, UUID uuidCracked, StateLogins stateLogins, boolean isTemporary) {
        this.register = new DataRegister(username, uuidCracked, stateLogins, isTemporary);
    }

    final private DataRegister register;
    private DataSession session;
    private DataLimbo limbo;

    public boolean hasSession() {
        return session != null;
    }
}
