package net.atcore.command.commnads;

import net.atcore.armament.*;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.ModeTab;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class WeaponCommand extends BaseTabCommand {

    public WeaponCommand() {
        super("weapon",
                "/weapon <Jugador> <Tipo_de_armamento> <Nombre_del_armamento> <!Cantidad>",
                "desbanea a al jugador que le caes bien"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0, 1 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            case 2 -> sendMessage(sender, Message.COMMAND_WEAPON_MISSING_ARGS_NAME, TypeMessages.ERROR);
            default -> {
                TypeArmament typeArmament;
                try {
                    typeArmament = TypeArmament.valueOf(args[1].toUpperCase());
                }catch (Exception e) {
                    sendMessage(sender, Message.COMMAND_WEAPON_MISSING_ARGS_TYPE, TypeMessages.ERROR);
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
                        CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer ->
                                GlobalUtils.addItemPlayer(baseArmament.getItemArmament(), dataTemporalPlayer.player(),
                                        true, typeArmament != TypeArmament.AMMO));
                    }
                    sendMessage(sender, Message.COMMAND_WEAPON_SUCCESSFUL, TypeMessages.SUCCESS);
                }else{
                    sendMessage(sender, Message.COMMAND_WEAPON_NOT_FOUND_TYPE, TypeMessages.ERROR);
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
                    return List.of(ChatColor.RED + "En tipo de armamento no existe");
                }
                switch (typeArmament) {
                    case WEAPON_TARKOV -> {
                        return CommandUtils.listTab(args[2], CommandUtils.enumsToStrings(ListWeaponTarvok.values(), false), ModeTab.StartWith);
                    }
                    case WEAPON_ULTRA_KILL -> {
                        return CommandUtils.listTab(args[2] ,CommandUtils.enumsToStrings(ListWeaponUltraKill.values(), false), ModeTab.StartWith);
                    }
                    case MAGAZINE -> {
                        return CommandUtils.listTab(args[2] ,CommandUtils.enumsToStrings(ListMagazine.values(), false), ModeTab.StartWith);
                    }
                    case AMMO -> {
                        return CommandUtils.listTab(args[2] ,CommandUtils.enumsToStrings(ListAmmo.values(), false), ModeTab.StartWith);
                    }
                }
            }
            case 4 -> {
                return List.of("#");
            }
        }
        return null;
    }

}
