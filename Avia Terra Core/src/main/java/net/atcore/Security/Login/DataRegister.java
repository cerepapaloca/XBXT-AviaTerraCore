package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;
@Getter
@Setter
public class DataRegister extends BaseDataLogin {

    public DataRegister(String username, UUID uuidCracked, UUID uuidPremium , StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, state);
        this.setUuidPremium(uuidPremium);
        LoginManager.getListRegister().put(username, this);
        this.isTemporary = isTemporary;
    }

    public DataRegister(String username, UUID uuidCracked, StateLogins state, boolean isTemporary) {
        super(username, uuidCracked, state);
        LoginManager.getListRegister().put(username, this);
        this.isTemporary = isTemporary;
    }

    private boolean isTemporary;
    private long lastLoginDate;
    private long registerDate;

}
