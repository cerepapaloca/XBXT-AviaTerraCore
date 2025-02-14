package net.atcore.messages;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum LocaleAvailable {
    ES,
    EN;
    LocaleAvailable() {
        this.locale = Locale.of(name().toLowerCase());
    }
    private final Locale locale;


    public static LocaleAvailable getLocate(Locale locale) {
        String lang = locale.getLanguage().split("-")[0];
        try {
            return LocaleAvailable.valueOf(lang.toUpperCase());
        }catch (Exception e) {
            return MessagesManager.DEFAULT_LOCALE_USER;
        }
    }
}
