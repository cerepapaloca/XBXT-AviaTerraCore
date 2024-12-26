package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.messages.Message;

public class MessageFile extends FileYaml {
    public MessageFile() {
        super("message", null, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        for (Message message : Message.values()) {
            String path = message.name().toLowerCase()
                    .replace("_", "-")
                    .replaceFirst(message.getParent(), "")
                    .replaceFirst("-", "");
            String finalPath = message.getParent() + "." + path;
            String s = fileYaml.getString(finalPath);
            if (s != null) {
                message.setMessage(s);
            }else {
                fileYaml.set(finalPath, message.getMessage());
            }
        }
        saveConfig();
    }

    @Override
    public void saveData() {

    }
}
