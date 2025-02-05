package net.atcore.command.commnads;

import net.atcore.armament.*;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static net.atcore.messages.MessagesManager.sendArgument;
import static net.atcore.messages.MessagesManager.sendMessage;

public class WeaponCommand extends BaseTabCommand {

    public WeaponCommand() {
        super("weapon",
                new ArgumentUse("weapon")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addArg("Tipo De Armamento")
                        .addArg("Nombre Del Armamento")
                        .addArgOptional()
                        .addArg("Cantidad"),
                "Crea añades un armamento",
                false);
        if (ArmamentUtils.ARMAMENTS.isEmpty()) ArmamentSection.initialize();
        for (BaseArmament armament : ArmamentUtils.ARMAMENTS){
            if (armament instanceof BaseWeaponUltraKill){
                WEAPONS_ULTRA_KILL_NAMES.add(armament.getName());
            }
            if (armament instanceof BaseWeaponTarkov){
                WEAPONS_TARVOK_NAMES.add(armament.getName());
            }
            if (armament instanceof BaseMagazine){
                MAGAZINES_NAMES.add(armament.getName());
            }
            if (armament instanceof BaseAmmo){
                AMMO_NAMES.add(armament.getName());
            }
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0, 1 -> sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            case 2 -> sendMessage(sender, Message.COMMAND_WEAPON_MISSING_ARGS_NAME);
            default -> {
                TypeArmament typeArmament;
                try {
                    typeArmament = TypeArmament.valueOf(args[1].toUpperCase());
                }catch (Exception e) {
                    sendMessage(sender, Message.COMMAND_WEAPON_MISSING_ARGS_TYPE);
                    return;
                }
                BaseArmament baseArmament;
                switch (typeArmament) {
                    case WEAPON_ULTRA_KILL, WEAPON_TARKOV -> baseArmament = ArmamentUtils.getWeapon(args[2]);
                    case MAGAZINE -> baseArmament = ArmamentUtils.getMagazine(args[2]);
                    case AMMO -> baseArmament = ArmamentUtils.getAmmo(args[2]);
                    default -> baseArmament = null;
                }
                if (baseArmament != null){
                    int cantidad;
                    try {
                        cantidad = Integer.parseInt(args[3]);
                    }catch (Exception e) {
                        cantidad = 1;
                    }
                    for (int i = 0; i < cantidad; i++) {
                        CommandUtils.executeForPlayer(sender, args[0], true, (name, player) ->
                                GlobalUtils.addItemPlayer(baseArmament.getItemArmament(), player,
                                        true, typeArmament != TypeArmament.AMMO, false));
                    }
                    sendMessage(sender, Message.COMMAND_WEAPON_SUCCESSFUL);
                }else{
                    sendMessage(sender, Message.COMMAND_WEAPON_NOT_FOUND_TYPE);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                return CommandUtils.tabForPlayer(args[0]);
            }
            case 2 -> {
                return CommandUtils.listTab(args[1] ,CommandUtils.enumsToStrings(TypeArmament.values()));
            }
            case 3 -> {
                TypeArmament typeArmament;
                try {
                    typeArmament = TypeArmament.valueOf(args[1].toUpperCase());
                }catch (Exception e) {
                    return List.of( TypeMessages.ERROR.getMainColor() + "En tipo de armamento no existe");
                }
                switch (typeArmament) {
                    case WEAPON_TARKOV -> {
                        return  CommandUtils.listTab(args[2], WEAPONS_TARVOK_NAMES);
                    }
                    case WEAPON_ULTRA_KILL -> {
                        return  CommandUtils.listTab(args[2], WEAPONS_ULTRA_KILL_NAMES);
                    }
                    case MAGAZINE -> {
                        return  CommandUtils.listTab(args[2], MAGAZINES_NAMES);
                    }
                    case AMMO -> {
                        return  CommandUtils.listTab(args[2], AMMO_NAMES);
                    }
                }
            }
            case 4 -> {
                return List.of("#");
            }
        }
        return null;
    }

    private static final List<String> WEAPONS_TARVOK_NAMES = new ArrayList<>();
    private static final List<String> WEAPONS_ULTRA_KILL_NAMES = new ArrayList<>();
    private static final List<String> MAGAZINES_NAMES = new ArrayList<>();
    private static final List<String> AMMO_NAMES = new ArrayList<>();

}
