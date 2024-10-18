package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;
@Getter
@Setter
public class DataRegister extends BaseDataLogin {

    public DataRegister(String username, UUID uuidCracked, UUID uuidPremium , StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, isTemporary, state);
        this.setUuidPremium(uuidPremium);
        LoginManager.getListRegister().put(username, this);
    }

    public DataRegister(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, isTemporary, state);
        LoginManager.getListRegister().put(username, this);
    }

    private long lastLoginDate;
    private long registerDate;

}
