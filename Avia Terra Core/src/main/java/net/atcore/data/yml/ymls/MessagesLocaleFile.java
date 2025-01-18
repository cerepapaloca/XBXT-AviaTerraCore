package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.MessageFile;
import net.atcore.messages.MessagesManager;

import java.util.Locale;

public class MessagesLocaleFile extends FilesYams {
    public MessagesLocaleFile() {
        super("messages", MessageFile.class, true);
        for (Locale locale : MessagesManager.LOCALES_AVAILABLE){
            registerConfigFile(locale.getLanguage());
        }
    }
}
