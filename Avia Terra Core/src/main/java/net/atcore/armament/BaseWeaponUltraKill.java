package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Getter
@Setter
public abstract class BaseWeaponUltraKill extends BaseWeapon {

    public BaseWeaponUltraKill(ListWeaponUltraKill weaponType, String displayName, int maxDistance, Color color, double damage, int manaCost, double precision, float penetration) {
        super(displayName, new ItemStack(Material.GOLDEN_HORSE_ARMOR), maxDistance, weaponType.name(), precision);
        this.color = color;
        this.damage = damage;
        this.manaCost = manaCost;
        this.weaponType = weaponType;
        this.penetration = penetration;
        ItemMeta meta = itemArmament.getItemMeta();
        assert meta != null;
        meta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(String.format("""
                Daño: <|%s|>
                Coste: <|%s|>
                Presión: <|%s|>
                Rango máximo: <|%s|>m
                """,
                damage,
                manaCost,
                (100 - precision) + "%",
                maxDistance
        ), null, CategoryMessages.PRIVATE), true));
        meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setDisplayName(displayName);
        itemArmament.setItemMeta(meta);
    }

    private final float penetration;
    private final Color color;
    private final double damage;
    private final int manaCost;
    private final ListWeaponUltraKill weaponType;

    @Override
    public void shoot(Player player) {
        AviaTerraPlayer atp = AviaTerraCore.getPlayer(player);
        double manaPlayer = atp.getMana();
        if (manaCost < manaPlayer){
            atp.setMana(manaPlayer - manaCost);
            Vector direction = player.getLocation().getDirection();
            Location location = player.getEyeLocation();
            Vector directionRandom = direction.add(new Vector((Math.random() - 0.5)*precision*0.002, (Math.random() - 0.5)*precision*0.002, (Math.random() - 0.5)*precision*0.002));//añade la imprecision del arma

            RayTraceResult result = player.getWorld().rayTraceEntities(//se crea un rayTrace
                    location,
                    directionRandom,
                    maxDistance,
                    0.5,//el margen para detectar a las entidades
                    entity -> entity != player//se descarta el protio jugador
            );
            boolean b = false;
            double distance = maxDistance;
            Block lastBlock = null;
            if (result != null) {
                Entity entity = result.getHitEntity();
                distance = player.getEyeLocation().distance(result.getHitPosition().toLocation(player.getWorld()));
                if (entity != null) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof LivingEntity livingEntity) {//se tiene que hacer instance por qué no la variable entity no sirve en este caso

                            BlockIterator blockIterator = new BlockIterator(
                                    player.getWorld(),
                                    player.getEyeLocation().toVector(),
                                    direction,
                                    0,
                                    (int) distance
                            );
                            DataShoot dataShoot = new DataShoot(livingEntity, player, this, null, null, distance, damage);//se crea los datos del disparo
                            float f = 0;
                            onShoot(dataShoot);

                            while (blockIterator.hasNext()) {
                                Block block = blockIterator.next();
                                f += block.getType().getHardness();
                                Bukkit.getLogger().warning(block.getType().name() + " | " + block.getType().getHardness());
                                if (f < penetration){
                                    lastBlock = block;
                                }
                            }
                            if (f < penetration){
                                livingEntity.damage(dataShoot.getDamage());//se aplica el daño
                                b = true;
                            }
                        }
                    }
                }
            }
            Location finalLocation = ArmamentUtils.getPlayerLookLocation(directionRandom, player, distance, 0.25);

            if (lastBlock != null){
                finalLocation = lastBlock.getLocation();
            }
            ArmamentUtils.drawParticleLine(player.getEyeLocation(), finalLocation,
                    color, b, 1);
        }else{
            MessagesManager.sendMessage(player, "No tiene mana suficiente", TypeMessages.ERROR);
        }
    }

    public abstract void onShoot(DataShoot dataShoot);
}
