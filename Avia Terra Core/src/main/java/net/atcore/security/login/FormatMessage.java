package net.atcore.security.login;

import lombok.Getter;
import net.atcore.messages.Message;

@Getter
public enum FormatMessage {
    LINK(Message.LOGIN_TWO_FACTOR_LINK_TITLE, Message.LOGIN_TWO_FACTOR_LINK_SUBTITLE),
    CODE(Message.LOGIN_TWO_FACTOR_CODE_TITLE, Message.LOGIN_TWO_FACTOR_CODE_SUBTITLE);

    private final Message title;
    private final Message subtitle;
    FormatMessage(Message title, Message subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }
}
