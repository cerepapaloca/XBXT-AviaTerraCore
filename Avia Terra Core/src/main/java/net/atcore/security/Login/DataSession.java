package net.atcore.security.Login;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.net.InetAddress;

@Getter
@Setter
public class DataSession {

    public DataSession(Player player, StateLogins state) {
        this.player = player;
        this.address = player.getAddress().getAddress();
        this.setState(state);
        startTimeLogin = System.currentTimeMillis();
    }

    public DataSession(Player player, StateLogins state, InetAddress address) {
        this.player = player;
        this.address = address;
        this.setState(state);
        startTimeLogin = System.currentTimeMillis();
    }

    private long startTimeLogin;
    private long endTimeLogin;
    private StateLogins state;
    private InetAddress address;
    private final Player player;
    private byte[] sharedSecret;

}
