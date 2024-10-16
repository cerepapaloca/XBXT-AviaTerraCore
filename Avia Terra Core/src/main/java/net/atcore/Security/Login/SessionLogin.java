package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@Setter
public class SessionLogin {

    public SessionLogin(String username, StateLogins state) {
        this.username = username;
        this.stateLogins = state;
        this.startTime = System.currentTimeMillis();
    }

    public SessionLogin(String username, UUID uuid, StateLogins state) {
        this.username = username;
        this.stateLogins = state;
        this.startTime = System.currentTimeMillis();
        this.uuid = uuid;
    }

    private final String username;
    private final StateLogins stateLogins;
    private final long startTime;
    private UUID uuid;
    private long endTime;
    private InetAddress ip;
    private String passwordShaded;
    private boolean loggedDiscord;

}
