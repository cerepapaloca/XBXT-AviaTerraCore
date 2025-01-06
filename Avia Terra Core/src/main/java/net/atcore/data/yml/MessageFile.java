package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.listener.NuVotifierListener;
import net.atcore.messages.Message;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MessageFile extends FileYaml {
    public MessageFile() {
        super("message", null, false, true);
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
            List<String> messages = fileYaml.getStringList(finalPath);
            if (s != null) {
                message.setMessage(new String[]{s});
            }else if (!messages.isEmpty()) {
                message.setMessage(messages.toArray(new String[0]));
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
