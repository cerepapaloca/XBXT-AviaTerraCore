package net.atcore.Moderation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DataBan {

    private final UUID uuid;
    private final String name;
    private final InetAddress address;
    private final String reason;
    private final long unbanDate;
    private final long banDate;
    private final ContextBan context;

}
