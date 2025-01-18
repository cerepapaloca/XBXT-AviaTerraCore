package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.messages.LocaleAvailable;
import net.atcore.messages.Message;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class MessageFile extends FileYaml {
    public MessageFile(String name, String folder) {
        super(name, folder, false, true);
    }

    public static final HashMap<EntityType, List<String>> MESSAGES_ENTITY = new HashMap<>();

    @Override
    public void loadData() {
        loadConfig();
        EnumSet<EntityType> allEntityTypes = EnumSet.allOf(EntityType.class);
        String tagLocale = fileName.replace(".yml", "");
        // Filtrar las entidades hostiles
        EnumSet<EntityType> hostileEntities = allEntityTypes.stream()
                .filter(type -> type.getEntityClass() != null && (
                        org.bukkit.entity.Monster.class.isAssignableFrom(type.getEntityClass()) ||
                        org.bukkit.entity.Golem.class.isAssignableFrom(type.getEntityClass())
                ))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(EntityType.class)));
        for (EntityType entityType : hostileEntities) {
            String path = "death-cause.entity." + entityType.name().toLowerCase().replace("_", "-");
            List<String> messages = fileYaml.getStringList(path);
            String message = fileYaml.getString(path);
            if (!messages.isEmpty()) {
                MESSAGES_ENTITY.put(entityType, List.copyOf(messages));
            }else if (message != null) {
                MESSAGES_ENTITY.put(entityType, List.of(message));
            }else {
                fileYaml.set(path, "<|%2$s|> mató a <|%1$s|>");
                MESSAGES_ENTITY.put(entityType, List.of("<|%2$s|> mató a <|%1$s|>"));
            }
        }
        for (String s : fileYaml.getKeys(true)){
            if (fileYaml.isConfigurationSection(s)) continue;
            if (s.startsWith("death-cause.entity")) continue;
            try {
                Message.valueOf(s.toUpperCase()
                        .replace("-", "_")
                        .replace(".", "_")
                );
            }catch (Exception e){
                fileYaml.setComments(s, List.of("Este mensaje no se esta usando"));
            }
        }
        for (Message message : Message.values()) {
            String path = message.name().toLowerCase()
                    .replace("_", "-")
                    .replaceFirst(message.getParent(), "")
                    .replaceFirst("-", "");
            String finalPath = message.getParent() + "." + path;
            String s = fileYaml.getString(finalPath);
            List<String> messages = fileYaml.getStringList(finalPath);
            LocaleAvailable available = LocaleAvailable.valueOf(tagLocale.toUpperCase());
            if (s != null) {
                message.getMapMessageLocale().put(available, new String[]{s});
            }else if (!messages.isEmpty()) {
                message.getMapMessageLocale().put(available, messages.toArray(new String[0]));
            }else {
                fileYaml.set(finalPath, message.getMessageLocatePrivate());
            }
        }
        saveConfig();
    }

    @Override
    public void saveData() {

    }
}
