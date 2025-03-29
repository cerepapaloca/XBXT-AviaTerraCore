package net.atcore.advanced;

import com.google.gson.reflect.TypeToken;
import io.papermc.paper.adventure.PaperAdventure;
import net.atcore.AviaTerraCore;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class BaseAchievementSimple<T extends Event> implements Listener {

    public static final List<BaseAchievementSimple<? extends Event>> REGISTERED_ACHIEVEMENT = new ArrayList<>();

    protected final EventExecutor eventExecutor;
    private Class<T> eventClass;
    protected final AdvancementHolder advancements;
    protected final HashMap<UUID, AdvancementProgress> playerProgress = new HashMap<>();

    protected final String path;
    protected final ResourceLocation categoryId;
    protected final ResourceLocation parentId;
    protected final ResourceLocation locationId;
    @SuppressWarnings("unchecked")
    public BaseAchievementSimple(Material material, String title, String description, String path) {
        this.path = path;

        categoryId = ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/root"
        );
        parentId = path != null ? ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/" + path.split("/")[path.split("/").length - 1]
        ) : null;
        if (path != null) Bukkit.getLogger().info(path.split("/")[path.split("/").length - 1]);
        locationId = path != null ? ResourceLocation.fromNamespaceAndPath(
                AviaTerraCore.getInstance().getName().toLowerCase(), "anarchy/" + path
        ) : null;

        advancements = createAdvancement(material, title, description);
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
            instance(event);
        };

        try {
            type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            eventClass = (Class<T>) TypeToken.get(type).getRawType();
            REGISTERED_ACHIEVEMENT.add(this);
            Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.LOWEST, eventExecutor, AviaTerraCore.getInstance());
        }catch (ClassCastException e) {
            eventClass = null;
        }
        REGISTERED_ACHIEVEMENT.add(this);
    }

    protected void instance(Event event) {
        if (eventClass.isInstance(event)) {
            T t = eventClass.cast(event);
            onEvent(t);
        }
    }

    private AdvancementHolder createAdvancement(Material material, String title, String description) {
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
                true,
                true,
                false
        );
        display.setLocation(getXCoordFor(path), getYCoordFor(path)); // Implementa esta lógica
        Map<String, Criterion<?>> criteria = new HashMap<>();
        criteria.put("impossible", new Criterion<>(
                new ImpossibleTrigger(),
                new ImpossibleTrigger.TriggerInstance()
        ));

        List<List<String>> requirements = new ArrayList<>();
        requirements.add(List.of("impossible"));

        return new AdvancementHolder(
                locationId == null ? categoryId : locationId,
                new Advancement(
                        parentId != null ? Optional.of(categoryId) : Optional.empty(),
                        Optional.of(display),
                        AdvancementRewards.EMPTY,
                        criteria,
                        new AdvancementRequirements(requirements),
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

    public abstract void onGrantAdvanced(Player player);

    protected void grantAdvanced(Player player) {
        AdvancementProgress progress = playerProgress.computeIfAbsent(player.getUniqueId(), k -> {
            AdvancementProgress p = new AdvancementProgress();
            p.update(advancements.value().requirements());
            return p;//TODO el segundo no funciona bien
        });
        progress.update(advancements.value().requirements());
        if (!progress.isDone()) {
            progress.grantProgress("impossible");
        }else {
            return;
        }
        onGrantAdvanced(player);
        sendAchievement(player);
    }

    public void sendAchievement(Player player) {
        AdvancementProgress progress = playerProgress.computeIfAbsent(player.getUniqueId(), k -> {
            AdvancementProgress p = new AdvancementProgress();
            p.update(advancements.value().requirements());
            return p;
        });
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(advancements),
                Set.of(),
                Map.of(locationId == null ? categoryId : locationId, progress)
        ));
    }

    public static void sendAllAchievement(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        List<AdvancementHolder> advancements = REGISTERED_ACHIEVEMENT.stream().map(base -> base.advancements).toList();
        Map<ResourceLocation, AdvancementProgress> advancementsProgress = new HashMap<>();
        for (BaseAchievementSimple<?> advancement : REGISTERED_ACHIEVEMENT) {
            advancementsProgress.put(advancement.parentId == null ? advancement.categoryId : advancement.parentId, advancement.playerProgress.computeIfAbsent(player.getUniqueId(),k ->  {
                AdvancementProgress progress = new AdvancementProgress();
                progress.update(advancement.advancements.value().requirements());
                return progress;
            }));
        }
        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                advancements,
                Set.of(),
                advancementsProgress
        ));

    }
}