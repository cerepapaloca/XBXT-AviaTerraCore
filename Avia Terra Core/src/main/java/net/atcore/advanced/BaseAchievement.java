package net.atcore.advanced;

import com.google.gson.reflect.TypeToken;
import io.papermc.paper.adventure.PaperAdventure;
import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.*;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    protected final AdvancementHolder advancements;
    protected final String path;
    @NotNull
    protected final ResourceLocation locationId;
    @NotNull
    protected final ResourceLocation categoryId;
    @Nullable
    protected final ResourceLocation parentId;

    @SuppressWarnings("unchecked")
    public BaseAchievement(Material material, String title, String description, String path) {
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

        advancements = createAdvancement(material, title, description, null);
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

    private AdvancementHolder createAdvancement(Material material, String title, String description,@Nullable Player player) {
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(
                "minecraft", // Namespace (usar "minecraft" para texturas vanilla)
                "textures/gui/advancements/backgrounds/adventure.png" // Ruta de la textura
        );

        DisplayInfo display = new DisplayInfo(
                CraftItemStack.asNMSCopy(new ItemStack(material)),
                PaperAdventure.asVanilla(Component.text(title)),
                PaperAdventure.asVanilla(Component.text(description)),
                Optional.of(background),
                AdvancementType.TASK,
                player == null || !AviaTerraPlayer.getPlayer(player).getAchievementProgress(this).isDone(),
                true,
                false
        );
        var var = createRequirements();
        display.setLocation(getXCoordFor(path), getYCoordFor(path)); // Implementa esta lógica
        Bukkit.getLogger().severe("An achievement has been created " + var.size());
        return new AdvancementHolder(
                locationId,
                new Advancement(
                        parentId != null ? Optional.of(categoryId) : Optional.empty(),
                        Optional.of(display),
                        AdvancementRewards.EMPTY,
                        createCriteria(),
                        new AdvancementRequirements(var),
                        false
                )
        );
    }
    private int getXCoordFor(String path) {
        // Ejemplo: asigna coordenadas basadas en el "path"
        return path == null ? 0 : path.split("/").length;
    }

    private int getYCoordFor(String path) {
        return 0;
    }


    public abstract void onEvent(T event);

    public abstract void rewards(Player player);

    public abstract void onProgressAdvanced(Player player);

    public abstract List<List<String>> createRequirements();

    public abstract Map<String, Criterion<?>> createCriteria();

    protected void grantAdvanced(Player player) {
        AdvancementProgress progress = AviaTerraPlayer.getPlayer(player).getAchievementProgress(this);
        if (!progress.isDone()) {
            onProgressAdvanced(player);
        }else {
            return;
        }

        //Bukkit.getLogger().info(categoryId.toString() + parentId + locationId);
        rewards(player);
        if (!(this instanceof BaseAchievementProgressive<? extends Event>)) sendAchievement(player, progress);
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
            advancements.add(advancement.createAdvancement(displayInfo.getIcon().asBukkitCopy().getType(), displayInfo.getTitle().getString(), displayInfo.getDescription().getString(), player));
            advancementsProgress.put(advancement.locationId, AviaTerraPlayer.getPlayer(player).getAchievementProgress(advancement));
        }
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                advancements,
                Set.of(),
                advancementsProgress
        ));

    }
}