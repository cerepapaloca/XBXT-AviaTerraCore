package net.atcore.exception;

public class DiscordChannelNotFound extends RuntimeException {
    public DiscordChannelNotFound(String message) {
        super(message);
    }
}
