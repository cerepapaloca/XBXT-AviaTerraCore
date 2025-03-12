package net.atcore.security.login.model;

import lombok.Getter;
import lombok.Setter;
import net.atcore.security.login.StateLogins;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class LoginData {

    public LoginData(@NotNull RegisterData register) {
        this.register = register;
    }

    public LoginData(String username, UUID uuidCracked, UUID uuidPremium, StateLogins stateLogins, boolean isTemporary) {
        this.register = new RegisterData(username, uuidCracked, uuidPremium, stateLogins, isTemporary);
    }

    public LoginData(String username, UUID uuidCracked, StateLogins stateLogins, boolean isTemporary) {
        this.register = new RegisterData(username, uuidCracked, stateLogins, isTemporary);
    }

    @NotNull
    final private RegisterData register;
    private SessionData session = null;
    private LimboData limbo = null;

    public boolean hasSession() {
        return session != null;
    }

    public boolean isLimboMode() {
        return limbo != null;
    }

    public boolean isBedrockPlayer(){
        return register.getUuidBedrock() != null;
    }
}
