package net.atcore.Security.Login;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;

@Getter
@Setter

public class SessionLogin {

    public SessionLogin(String username, StateLogins state) {
        this.username = username;
        this.stateLogins = state;
        this.startTime = System.currentTimeMillis();
    }

    private final String username;
    private final StateLogins stateLogins;
    private final long startTime;
    private long endTime;
    private InetAddress ip;
    private String passwordShaded;
    private boolean loggedDiscord;

}
