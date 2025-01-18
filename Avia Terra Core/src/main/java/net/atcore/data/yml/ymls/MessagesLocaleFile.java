package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.MessageFile;
import net.atcore.messages.LocaleAvailable;

public class MessagesLocaleFile extends FilesYams {
    public MessagesLocaleFile() {
        super("messages", MessageFile.class, true);
        for (LocaleAvailable locale : LocaleAvailable.values()){
            registerConfigFile(locale.name().toLowerCase());
        }
    }
}
