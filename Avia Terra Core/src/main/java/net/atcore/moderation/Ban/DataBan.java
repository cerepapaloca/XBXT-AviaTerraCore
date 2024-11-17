package net.atcore.moderation.Ban;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DataBan {

    @NotNull
    private final String name;
    private final UUID uuid;
    private final InetAddress address;
    private final String reason;
    private final long unbanDate;
    private final long banDate;
    @NotNull
    private final ContextBan context;
    @NotNull
    private final String author;

}
