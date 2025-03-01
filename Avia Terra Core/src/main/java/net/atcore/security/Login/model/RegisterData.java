package net.atcore.security.Login.model;

import lombok.Getter;
import lombok.Setter;
import net.atcore.security.Login.StateLogins;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@Setter
public class RegisterData {

    public RegisterData(@NotNull String username, @NotNull UUID uuidCracked, UUID uuidPremium , StateLogins state, boolean isTemporary) {
        this.username = username;
        this.uuidCracked = uuidCracked;
        this.stateLogins = state;
        this.setUuidPremium(uuidPremium);
        this.isTemporary = isTemporary;
    }

    public RegisterData(@NotNull String username, @NotNull UUID uuidCracked, StateLogins state, boolean isTemporary) {
        this.username = username;
        this.uuidCracked = uuidCracked;
        this.stateLogins = state;
        this.isTemporary = isTemporary;
    }
    @NotNull
    private final String username;
    private StateLogins stateLogins;
    private UUID uuidPremium;
    @NotNull
    private UUID uuidCracked;
    private UUID uuidBedrock;
    private String passwordShaded;
    private boolean isTemporary;
    private long lastLoginDate;
    private long registerDate;
    private InetAddress lastAddress;
    @NotNull
    private InetAddress registerAddress;
    @Nullable
    private String mail;
    @Nullable
    private String discord;
}
