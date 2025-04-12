package net.atcore.achievement;

import com.google.gson.reflect.TypeToken;
import io.papermc.paper.adventure.PaperAdventure;
import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.DataSection;
import net.atcore.data.yml.MessageFile;
import net.atcore.messages.*;
import net.atcore.security.login.LoginManager;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class BaseAchievement<T extends Event> implements Listener {

    private static final HashMap<ResourceLocation, BaseAchievement<? extends Event>> REGISTERED_ACHIEVEMENT = new HashMap<>();
    private static final ResourceLocation ROOT = ResourceLocation.fromNamespaceAndPath(AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/root");
    public static final HashMap<Class<? extends Event>, List<BaseAchievement<? extends Event>>> EVENTS_REGISTERED = new HashMap<>();

    protected final EventExecutor eventExecutor;
    private Class<T> eventClass;
    @NotNull
    public final AdvancementHolder advancements;
    @NotNull
    public final ResourceLocation categoryId;
    @Nullable
    public final ResourceLocation parentId;
    @NotNull
    public final ResourceLocation id;
    public final boolean isSyn;
    private AdvancementNode node = null;


    @SuppressWarnings("unchecked")
    public BaseAchievement(Material material, Class<? extends BaseAchievement<? extends Event>> parent, AdvancementType advancementType) {
        String normalizePath = parent == null ? null : parent.getSimpleName().replace("Achievement", "").toLowerCase();
        categoryId = ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/root"
        );
        parentId = normalizePath != null ? ResourceLocation.fromNamespaceAndPath(AviaTerraCore.getInstance().getName().toLowerCase(),"anarchy/" + normalizePath) : null;
        id = normalizePath != null ? ResourceLocation.fromNamespaceAndPath(AviaTerraCore.getInstance().getName().toLowerCase(),
                        "anarchy/" + this.getClass().getSimpleName().replace("Achievement", "").toLowerCase()
                ) : ROOT;
        advancements = createAdvancement(material, null, advancementType, true);
        Type type;
        isSyn = this instanceof SynchronouslyEvent;
        if (isSyn) {
            eventExecutor = (listener, event) -> {
                Player player = null;
                if (event instanceof PlayerEvent playerEvent) {
                    player = playerEvent.getPlayer();
                }else if (event instanceof InventoryEvent inventoryEvent) {
                    player = (Player) inventoryEvent.getView().getPlayer();
                }if (player != null){
                    if (LoginManager.getDataLogin(player) == null || !LoginManager.getDataLogin(player).hasSession()) return;
                    AviaTerraPlayer.DataProgress progress = AviaTerraPlayer.getPlayer(player).getAchievementProgress().get(id);
                    if (progress != null && progress.getProgress().isDone()) return;
                }
                if (eventClass.isInstance(event)) {
                    T t = eventClass.cast(event);
                    onEvent(t);
                }
            };
        }else {
            eventExecutor = (listener, event) -> {
                Player player;
                if (event instanceof PlayerEvent playerEvent) {
                    player = playerEvent.getPlayer();
                }else if (event instanceof InventoryEvent inventoryEvent) {
                    player = (Player) inventoryEvent.getView().getPlayer();
                } else {
                    player = null;
                }

                AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                    if (eventClass.isInstance(event)) {
                        T t = eventClass.cast(event);
                        for (BaseAchievement<? extends Event> achievement : EVENTS_REGISTERED.getOrDefault(t.getClass(), List.of())) {
                            if (player != null){
                                if (LoginManager.getDataLogin(player) == null || !LoginManager.getDataLogin(player).hasSession()) return;
                                AviaTerraPlayer.DataProgress progress = AviaTerraPlayer.getPlayer(player).getAchievementProgress().get(achievement.id);
                                if (progress != null && progress.getProgress().isDone()) continue;
                            }
                            try {
                                ((BaseAchievement<T>) achievement).onEvent(t);
                            }catch (ClassCastException e){
                                MessagesManager.sendWaringException("Error a hacer un cast, En los logros", e);
                            }
                        }
                    }
                });
            };
        }
        try {
            type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            eventClass = (Class<T>) TypeToken.get(type).getRawType();
            if (EVENTS_REGISTERED.containsKey(eventClass)) {
                EVENTS_REGISTERED.get(eventClass).add(this);
            }else {
                Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, eventExecutor, AviaTerraCore.getInstance());
                if (!isSyn) EVENTS_REGISTERED.put(eventClass, new ArrayList<>(List.of(this)));
            }
        } catch (ClassCastException e) {
            eventClass = null;
        }
        REGISTERED_ACHIEVEMENT.put(id, this);
    }

    public abstract void onEvent(T event);

    private static AdvancementNode setParent(AdvancementNode advancement, AdvancementNode parent) {
        List<AdvancementNode> children = new ArrayList<>();
        for (AdvancementNode child : advancement.children())
            children.add(child);

        try {
            AdvancementHolder newAdvancementHolder = deconstructAdvancement(advancement.advancement()).parent(parent.holder()).build(advancement.holder().id());
            AdvancementNode newAdvancement = new AdvancementNode(newAdvancementHolder, parent);

            for (AdvancementNode child : children) newAdvancement.addChild(child);

            parent.addChild(newAdvancement);
            return newAdvancement;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set parent of advancement.", e);
        }
    }

    private static Advancement.Builder deconstructAdvancement(Advancement advancement) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        if (advancement.display().isPresent())
            builder.display(advancement.display().get());
        for (Map.Entry<String, Criterion<?>> entry : advancement.criteria().entrySet()) builder.addCriterion(entry.getKey(), entry.getValue());
        builder.requirements(advancement.requirements());
        return builder;
    }

    public static void createNode(){
        //A todos le crea un nodo sin pariente
        for (BaseAchievement<?> achievement : REGISTERED_ACHIEVEMENT.values()){
            achievement.node = new AdvancementNode(achievement.advancements, null);
        }
        // A todos se le añade un pariente ya qué todos tiene un nodo
        for (BaseAchievement<?> achievement : REGISTERED_ACHIEVEMENT.values()){
            if (achievement.parentId == null) continue;
            achievement.node = setParent(achievement.node, REGISTERED_ACHIEVEMENT.get(achievement.parentId).node);
        }
        TreeNodePosition.run(AdvancementNode.getRoot(REGISTERED_ACHIEVEMENT.get(ROOT).node));
    }

    private AdvancementHolder createAdvancement(Material material, @Nullable Player player, AdvancementType type, boolean toas) {
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(
                "minecraft", // Namespace (usar "minecraft" para texturas vanilla)
                "textures/block/obsidian.png" // Ruta de la textura
        );
        Random random = new Random();
        MessagesAchievement message = getMessage(player);
        DisplayInfo display = new DisplayInfo(
                CraftItemStack.asNMSCopy(new ItemStack(material)),
                PaperAdventure.asVanilla(GlobalUtils.chatColorLegacyToComponent(message.title().get(random.nextInt(message.title().size())))),
                PaperAdventure.asVanilla(GlobalUtils.chatColorLegacyToComponent(message.description().get(random.nextInt(message.title().size())))),
                Optional.of(background),
                type,
                toas,
                true,
                false
        );
        if (node != null)
            display.setLocation(node.advancement().display().orElseThrow().getX(), node.advancement().display().orElseThrow().getY());
        BaseProperties properties = createProperties();
        return new AdvancementHolder(
                id,
                new Advancement(
                        parentId != null ? Optional.of(parentId) : Optional.empty(),
                        Optional.of(display),
                        AdvancementRewards.EMPTY,
                        properties.criteria(),
                        new AdvancementRequirements(properties.requirements()),
                        false
                )
        );
    }

    private @NotNull MessagesAchievement getMessage(@Nullable CommandSender sender) {
        List<String> titles;
        List<String> description;

        if (!AviaTerraCore.isStarting()) {
            MessageFile messageFile;
            if (sender instanceof Player player) {
                messageFile = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(LocaleAvailable.getLocate(player.locale()).name().toLowerCase(), false);
            } else {
                messageFile = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(MessagesManager.DEFAULT_LOCALE_PRIVATE.name().toLowerCase(), false);
            }
            titles = Objects.requireNonNullElseGet(messageFile.messagesAchievement.get(this), () -> {
                MessageFile mf = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(MessagesManager.DEFAULT_LOCALE_PRIVATE.name().toLowerCase(), false);
                return mf.messagesAchievement.get(this);
            }).title;
            description = Objects.requireNonNullElseGet(messageFile.messagesAchievement.get(this), () -> {
                MessageFile mf = (MessageFile) DataSection.getMessagesLocaleFile().getConfigFile(MessagesManager.DEFAULT_LOCALE_PRIVATE.name().toLowerCase(), false);
                return mf.messagesAchievement.get(this);
            }).description;
        } else {
            titles = List.of("default");
            description = List.of("default");
        }
        return new MessagesAchievement(titles, description);
    }

    @Contract(" -> new")
    protected @NotNull BaseAchievement.BaseProperties createProperties() {
        Map<String, Criterion<?>> criteria = new HashMap<>();
        for (int i = 1; i <= getMetaProgress(); i++) {
            criteria.put(String.valueOf(i), new Criterion<>(
                    new ImpossibleTrigger(),
                    new ImpossibleTrigger.TriggerInstance()
            ));
        }

        List<List<String>> requirements = new ArrayList<>();
        for (int i = 1; i <= getMetaProgress(); i++) {
            requirements.add(List.of(String.valueOf(i)));
        }
        return new BaseProperties(criteria, requirements);
    }

    protected record BaseProperties(Map<String, Criterion<?>> criteria, List<List<String>> requirements) {
    }



    public abstract void rewards(Player player);

    protected abstract int getMetaProgress();

    protected abstract void onProgressAdvanced(AdvancementProgress progress, Object data);
    
    /**
     * Das un avance en el logro en caso de cumplir con todos los requisitos se completaría
     * @param player el jugador que cometió el avance
     * @param data esto si se requiere datos especiales para dar el avance en la mayor de casos solo es poner null
     */

    protected void grantAdvanced(Player player, Object data) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        // Si el logro está completo no se hace nada
        if (progress.getProgress().isDone()) {
            return;
        } else {
            // Se le añade el progreso
            if ((this instanceof BaseAchievementContinuous<?> achievementContinuous) && (progress instanceof AviaTerraPlayer.DataProgressContinuos progressContinuos)) {
                if (achievementContinuous.getMetaValue() <= progressContinuos.getValue())  onProgressAdvanced(progress.getProgress(), data);
            } else {
                onProgressAdvanced(progress.getProgress(), data);
            }
            saveData(atp);
        }
        // Si el logro se completó por esta acción se le envía él toas
        if (progress.getProgress().isDone()) {
            sendAchievement(player, progress.getProgress(), true);
            rewards(player);
        }else {
            // Estos tipos de logros, solo se requiere un seguimiento a tiempo real
            if ((this instanceof BaseAchievementStep<?>) || (this instanceof BaseAchievementProgressive<?>)) sendAchievement(player, progress.getProgress(), false);
        }
    }

    private final HashSet<AviaTerraPlayer> saveTask = new HashSet<>();

    public void saveData(AviaTerraPlayer player) {
        if (!saveTask.contains(player)) {
            saveTask.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    AviaTerraScheduler.runTask(() -> {
                        player.getPlayerDataFile().saveData();
                        saveTask.remove(player);
                    });
                }
            }.runTaskLater(AviaTerraCore.getInstance(), 40);
        }
    }

    /**
     * Completa un logro a un jugador sin importar lo que le falte
     * @param player el jugador afectado
     * @param b si se tiene que ver él toas y dar recompensa
     */

    public void grantAchievement(Player player, boolean b) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        if (!progress.getProgress().isDone()) {
            ArrayList<String> requirements = new ArrayList<>();
            progress.getProgress().getRemainingCriteria().forEach(requirements::add);
            requirements.forEach(s -> progress.getProgress().grantProgress(s));
        }
        saveData(atp);
        if (b) rewards(player);
        sendAchievement(player, progress.getProgress(), b);
    }

    public void revokeAchievement(Player player, boolean b) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        if (progress.getProgress().isDone() || b) {
            ArrayList<String> requirements = new ArrayList<>();
            progress.getProgress().getCompletedCriteria().forEach(requirements::add);
            requirements.forEach(s -> progress.getProgress().revokeProgress(s));
            if (progress instanceof AviaTerraPlayer.DataProgressContinuos progressContinuos) {
                progressContinuos.setValue(0);
            }
        }
        saveData(atp);
        sendAchievement(player, progress.getProgress(), true);
    }


    public void sendAchievement(Player player, AdvancementProgress progress, boolean toas) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        DisplayInfo displayInfo = advancements.value().display().orElseThrow();
        if (toas && progress.isDone()) {
            String color;
            Message message;
            switch (displayInfo.getType()){
                case CHALLENGE ->{
                    color = "light_purple";
                    message = Message.EVENT_CHAT_ADVANCEMENT_CHALLENGE;
                }
                case GOAL -> {
                    color = "aqua";
                    message = Message.EVENT_CHAT_ADVANCEMENT_GOAL;
                }
                default -> {
                    color = "green";
                    message = Message.EVENT_CHAT_ADVANCEMENT_TASK;
                }
            }
            Random random = new Random();
            List<CommandSender> senders = new ArrayList<>(Bukkit.getOnlinePlayers());
            senders.add(Bukkit.getConsoleSender());
            for (CommandSender sender : senders){
                MessagesAchievement messagesAchievement = getMessage(sender);
                Component component = MessagesManager.applyFinalProprieties(String.format("<dark_gray>[</dark_gray><gold>★</gold><dark_gray>]</dark_gray> " + message.getMessage(sender),
                        "<click:SUGGEST_COMMAND:/w " + player.getName() + ">" + player.getName() + "</click>",
                        "<" + color + ">[<hover:show_text:'<" + color + ">" + messagesAchievement.description().get(random.nextInt(messagesAchievement.description().size())) + "</" + color + ">'>" + messagesAchievement.title().get(random.nextInt(messagesAchievement.title().size())) + "</hover>]</" + color + ">"
                ), TypeMessages.INFO, CategoryMessages.PRIVATE, false);
                sender.sendMessage(component);
            }
        }
        AdvancementHolder advancementHolder = createAdvancement(displayInfo.getIcon().asBukkitCopy().getType(),
                player,
                displayInfo.getType(),
                toas
        );
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(advancementHolder),
                Set.of(),
                Map.of(id, progress)
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void sendAllAchievement(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        List<AdvancementHolder> advancements = new ArrayList<>();
        Map<ResourceLocation, AdvancementProgress> advancementsProgress = new HashMap<>();
        for (BaseAchievement<?> advancement : REGISTERED_ACHIEVEMENT.values()) {
            DisplayInfo displayInfo = advancement.advancements.value().display().get();
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            AviaTerraPlayer.DataProgress progress = atp.getProgress(advancement);
            if (advancement.id.equals(ROOT)) {
                if (!progress.getProgress().isDone()) {
                    List<String> requirements = new ArrayList<>();
                    progress.getProgress().getRemainingCriteria().forEach(requirements::add);
                    requirements.forEach(s -> progress.getProgress().grantProgress(s));
                }
                AviaTerraScheduler.enqueueTaskAsynchronously(() -> atp.getPlayerDataFile().saveData());
            }
            advancements.add(advancement.createAdvancement(displayInfo.getIcon().asBukkitCopy().getType(),
                         player,
                         displayInfo.getType(),
                    false)
            );
            advancementsProgress.put(advancement.id, AviaTerraPlayer.getPlayer(player).getProgress(advancement).getProgress());
        }
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                advancements,
                Set.of(),
                advancementsProgress
        ));
    }

    @Nullable
    public static BaseAchievement<? extends Event> getAchievement(ResourceLocation location) {
        return REGISTERED_ACHIEVEMENT.get(location);
    }

    public static List<BaseAchievement<? extends Event>> getAllAchievement() {
        return REGISTERED_ACHIEVEMENT.values().stream().toList();
    }

    public record MessagesAchievement(List<String> title, List<String> description){}
}
