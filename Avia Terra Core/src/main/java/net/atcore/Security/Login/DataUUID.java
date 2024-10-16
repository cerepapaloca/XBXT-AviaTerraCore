package net.atcore.Security.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DataUUID {

    public DataUUID(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    private final String name;
    private final UUID uuid;
    private UUID uuidPremium;

}
