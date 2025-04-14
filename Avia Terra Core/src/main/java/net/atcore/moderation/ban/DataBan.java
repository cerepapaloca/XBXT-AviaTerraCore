package net.atcore.moderation.ban;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DataBan {

    @NotNull
    private final String name;
    @NotNull
    private final UUID uuid;
    @Nullable
    private final InetAddress address;
    @NotNull
    private final String reason;
    private final long unbanDate;
    private final long banDate;
    @NotNull
    private final ContextBan context;
    @NotNull
    private final String author;

}
