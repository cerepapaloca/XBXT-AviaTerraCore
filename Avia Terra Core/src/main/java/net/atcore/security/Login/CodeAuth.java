package net.atcore.security.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CodeAuth {

    private final UUID code;
    private final long expires;
    private final UUID uuidPlayer;
    private final String media;
}
