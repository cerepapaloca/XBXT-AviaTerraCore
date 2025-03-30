package net.atcore.advanced;

import com.google.gson.reflect.TypeToken;
import io.papermc.paper.adventure.PaperAdventure;
import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class BaseAchievement<T extends Event> implements Listener {

    public static final List<BaseAchievement<? extends Event>> REGISTERED_ACHIEVEMENT = new ArrayList<>();

    protected final EventExecutor eventExecutor;
    private Class<T> eventClass;
    protected final String path;
    @NotNull
    public final AdvancementHolder advancements;
    @NotNull
    public final ResourceLocation locationId;
    @NotNull
    public final ResourceLocation categoryId;
    @Nullable
    public final ResourceLocation parentId;

    @SuppressWarnings("unchecked")
    public BaseAchievement(Material material, String title, String description, String path, AdvancementType advancementType) {
        this.path = path;
        categoryId = ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/root"
        );
        parentId = path != null ? ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/" + path.split("/")[path.split("/").length - 1]
        ) : null;
        locationId = path != null ? ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/root/" + path
        ) : categoryId;

        advancements = createAdvancement(material, title, description, null, advancementType);
        Type type;
        eventExecutor = (listener, event) -> {
            if (event instanceof PlayerEvent playerEvent) {
                if (playerEvent.getPlayer().isOp()) {
                    return;
                }
            }else if (event instanceof InventoryEvent inventoryEvent) {
                if (inventoryEvent.getView().getPlayer().isOp()) {
                    return;
                }
            }
            if (eventClass.isInstance(event)) {
                T t = eventClass.cast(event);
                onEvent(t);
            }
        };

        try {
            type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            eventClass = (Class<T>) TypeToken.get(type).getRawType();
            Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.LOWEST, eventExecutor, AviaTerraCore.getInstance());
        }catch (ClassCastException e) {
            eventClass = null;
        }
        REGISTERED_ACHIEVEMENT.add(this);
    }

    private AdvancementHolder createAdvancement(Material material, String title, String description, @Nullable Player player, AdvancementType type) {
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(
                "minecraft", // Namespace (usar "minecraft" para texturas vanilla)
                "textures/gui/advancements/backgrounds/adventure.png" // Ruta de la textura
        );

        DisplayInfo display = new DisplayInfo(
                CraftItemStack.asNMSCopy(new ItemStack(material)),
                PaperAdventure.asVanilla(Component.text(title)),
                PaperAdventure.asVanilla(Component.text(description)),
                Optional.of(background),
                type,
                player == null || !AviaTerraPlayer.getPlayer(player).getProgress(this).getProgress().isDone(),
                true,
                false
        );
        BaseProperties properties = createProperties();
        display.setLocation(getXCoordFor(path), getYCoordFor(path)); // Implementa esta lógica
        return new AdvancementHolder(
                locationId,
                new Advancement(
                        parentId != null ? Optional.of(categoryId) : Optional.empty(),
                        Optional.of(display),
                        AdvancementRewards.EMPTY,
                        properties.criteria(),
                        new AdvancementRequirements(properties.requirements()),
                        false
                )
        );
    }

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

    protected record BaseProperties(Map<String, Criterion<?>> criteria, List<List<String>> requirements) {}

    private int getXCoordFor(String path) {
        // Ejemplo: asigna coordenadas basadas en el "path"
        return path == null ? 0 : path.split("/").length;
    }

    private int getYCoordFor(String path) {
        return 0;
    }

    public abstract void onEvent(T event);

    public abstract void rewards(Player player);

    protected abstract int getMetaProgress();

    protected abstract void onProgressAdvanced(Player player, Object data);

    protected void grantAdvanced(Player player, Object data) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        if (progress.getProgress().isDone()) {
            return;
        } else {
            if ((this instanceof BaseAchievementContinuous<?> achievementContinuous) && (progress instanceof AviaTerraPlayer.DataProgressContinuos progressContinuos)) {
                if (achievementContinuous.getMetaValue() <= progressContinuos.getValue()) onProgressAdvanced(player, data);
            }else{
                onProgressAdvanced(player, data);
            }
            AviaTerraCore.enqueueTaskAsynchronously(() -> atp.getPlayerDataFile().saveData());
        }
        if (progress.getProgress().isDone()) {
            rewards(player);
        }
        sendAchievement(player, progress.getProgress());
    }

    public void grantAchievement(Player player, boolean b) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        if (!progress.getProgress().isDone()) {
            ArrayList<String> requirements = new ArrayList<>();
            progress.getProgress().getRemainingCriteria().forEach(requirements::add);
            requirements.forEach(s -> progress.getProgress().grantProgress(s));
        }
        AviaTerraCore.enqueueTaskAsynchronously(() -> atp.getPlayerDataFile().saveData());
        if (b) rewards(player);
        sendAchievement(player, progress.getProgress());
    }

    public void revokeAchievement(Player player, boolean b) {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        AviaTerraPlayer.DataProgress progress = atp.getProgress(this);
        if (progress.getProgress().isDone() || b) {
            ArrayList<String> requirements = new ArrayList<>();
            progress.getProgress().getCompletedCriteria().forEach(requirements::add);
            requirements.forEach(s -> progress.getProgress().revokeProgress(s));
        }
        AviaTerraCore.enqueueTaskAsynchronously(() -> atp.getPlayerDataFile().saveData());
        sendAchievement(player, progress.getProgress());
    }

    public void sendAchievement(Player player, AdvancementProgress progress) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(advancements),
                Set.of(),
                Map.of(locationId, progress)
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void sendAllAchievement(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        REGISTERED_ACHIEVEMENT.sort(Comparator.comparingInt((BaseAchievement<?> achievementSimple) ->
                achievementSimple.locationId.getPath().split("/").length));
        List<AdvancementHolder> advancements = new ArrayList<>();
        Map<ResourceLocation, AdvancementProgress> advancementsProgress = new HashMap<>();
        for (BaseAchievement<?> advancement : REGISTERED_ACHIEVEMENT) {
            DisplayInfo displayInfo = advancement.advancements.value().display().get();
            advancements.add(advancement.createAdvancement(displayInfo.getIcon().asBukkitCopy().getType(),
                    displayInfo.getTitle().getString(),
                    displayInfo.getDescription().getString(),
                    player,
                    displayInfo.getType()
                    )
            );
            advancementsProgress.put(advancement.locationId, AviaTerraPlayer.getPlayer(player).getProgress(advancement).getProgress());
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
        for (BaseAchievement<?> ach : REGISTERED_ACHIEVEMENT) {
            if (ach.locationId.getPath().equals(location.getPath()) && ach.locationId.getNamespace().equals(location.getNamespace())) {
                return ach;
            }
        }
        return null;
    }

    public static List<BaseAchievement<? extends Event>> getAllAchievement() {
        return REGISTERED_ACHIEVEMENT;
    }
}