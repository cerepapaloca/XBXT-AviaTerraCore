package net.atcore.data.yml;

import net.atcore.achievement.BaseAchievement;
import net.atcore.data.FileYaml;
import net.atcore.messages.LocaleAvailable;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class MessageFile extends FileYaml {
    public MessageFile(String name, String folder) {
        super(name, folder, false, true);
    }

    public final HashMap<EntityType, List<String>> messagesEntity = new HashMap<>();
    public final HashMap<BaseAchievement<?, ?>, BaseAchievement.MessagesAchievement> messagesAchievement = new HashMap<>();

    @Override
    public void loadData() {
        loadConfig();
        messagesEntity.clear();
        messagesAchievement.clear();
        EnumSet<EntityType> allEntityTypes = EnumSet.allOf(EntityType.class);
        String tagLocale = fileName.replace(".yml", "");
        // Filtrar las entidades hostiles
        EnumSet<EntityType> hostileEntities = allEntityTypes.stream()
                .filter(type -> type.getEntityClass() != null && (
                        org.bukkit.entity.Monster.class.isAssignableFrom(type.getEntityClass()) ||
                        org.bukkit.entity.Golem.class.isAssignableFrom(type.getEntityClass())
                ))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(EntityType.class)));
        LocaleAvailable available = LocaleAvailable.valueOf(tagLocale.toUpperCase());
        for (EntityType entityType : hostileEntities) {
            String path = "death-cause.entity." + entityType.name().toLowerCase().replace("_", "-");
            List<String> messages = fileYaml.getStringList(path);
            String message = fileYaml.getString(path);
            if (!messages.isEmpty()) {
                messagesEntity.put(entityType, List.copyOf(messages));
            }else if (message != null) {
                messagesEntity.put(entityType, List.of(message));
            }else {
                if (available.equals(MessagesManager.DEFAULT_LOCALE_PRIVATE)) fileYaml.set(path, "<|%2$s|> mató a <|%1$s|>");
                messagesEntity.put(entityType, null);
            }
        }
        ConfigurationSection configSection = fileYaml.getConfigurationSection("achievement");
        Set<String> pathsAchievements = new HashSet<>(configSection == null ? Set.of("") : configSection.getKeys(true));
        for (BaseAchievement<?, ?> achievement : BaseAchievement.getAllAchievement()){
            String path = "achievement." + achievement.id.getPath().replace("/", ".");
            String title = fileYaml.getString(path + ".title");
            List<String> titles = fileYaml.getStringList(path + ".title");
            String description = fileYaml.getString(path + ".description");
            List<String> descriptions = fileYaml.getStringList(path + ".description");
            if (!titles.isEmpty() && !descriptions.isEmpty()) {
                messagesAchievement.put(achievement, new BaseAchievement.MessagesAchievement(List.copyOf(titles), List.copyOf(descriptions)));
            }else if (title != null && description != null) {
                messagesAchievement.put(achievement, new BaseAchievement.MessagesAchievement(List.of(title), List.of(description)));
            }else {
                if (available.equals(MessagesManager.DEFAULT_LOCALE_PRIVATE)){
                    fileYaml.set(path + ".title", "default title");
                    fileYaml.set(path + ".description", "default description");
                    messagesAchievement.put(achievement, new BaseAchievement.MessagesAchievement(List.of("default title"), List.of("default description")));
                }else {
                    messagesAchievement.put(achievement, null);
                }

            }
            pathsAchievements.add(path + ".title");
            pathsAchievements.add(path + ".description");
            pathsAchievements.add(path);
        }
        for (String s : fileYaml.getKeys(true)){
            if (fileYaml.isConfigurationSection(s)) continue;
            if (s.startsWith("death-cause.entity")) continue;
            try {
                if (pathsAchievements.contains(s)) continue;
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
            if (s != null) {
                message.getMapMessageLocale().put(available, new String[]{s});
            }else if (!messages.isEmpty()) {
                message.getMapMessageLocale().put(available, messages.toArray(new String[0]));
            }else if (available.equals(MessagesManager.DEFAULT_LOCALE_PRIVATE)){
                fileYaml.set(finalPath, message.getMessageLocatePrivate());
            }
        }
        saveConfig();
    }

    @Override
    public void saveData() {

    }
}
