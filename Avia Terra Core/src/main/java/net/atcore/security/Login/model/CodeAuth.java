package net.atcore.security.Login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.atcore.security.Login.TwoFactorAuth;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CodeAuth {

    private final UUID code;
    private final long expires;
    private final UUID uuidPlayer;
    private final String media;
    private final TwoFactorAuth.MediaAuth mediaAuth;
}
