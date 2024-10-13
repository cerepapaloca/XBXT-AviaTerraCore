package net.atcore.Exception;

public class DiscordChannelNotFound extends RuntimeException {
    public DiscordChannelNotFound(String message) {
        super(message);
    }
}
