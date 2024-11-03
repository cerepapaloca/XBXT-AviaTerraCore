package net.atcore.command.Commnads;

import net.atcore.armament.*;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.ModeTab;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class WeaponCommand extends BaseTabCommand {

    public WeaponCommand() {
        super("weapon",
                "/weapon <Jugador> <Tipo de armamento> <Nombre del armamento> <Cantidad>",
                true,
                "desbanea a al jugador que le caes bien"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0, 1 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            case 2 -> sendMessage(sender, "Falta el nombre del armamento", TypeMessages.ERROR);
            default -> {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {

                    BaseArmament BaseArmament = null;
                    TypeArmament typeArmament;
                    try {
                        typeArmament = TypeArmament.valueOf(args[1].toUpperCase());
                    }catch (Exception e) {
                        sendMessage(sender, "El tipo de armamento no existe", TypeMessages.ERROR);
                        return;
                    }
                    switch (typeArmament) {
                        case WEAPON_ULTRA_KILL, WEAPON_TARKOV -> BaseArmament = ArmamentUtils.getWeapon(args[2]);
                        case CHARGER -> BaseArmament = ArmamentUtils.getCharger(args[2]);
                        case AMMO -> BaseArmament = ArmamentUtils.getAmmon(args[2]);
                    }
                    if (BaseArmament != null){
                        int cantidad;
                        try {
                             cantidad = Integer.parseInt(args[3]);
                        }catch (Exception e) {
                            sendMessage(sender, "Numero no valido", TypeMessages.ERROR);
                            return;
                        }
                        for (int i = 0; i < cantidad; i++) {
                            GlobalUtils.addItemPlayer(BaseArmament.getItemArmament(), player, true, typeArmament != TypeArmament.AMMO);
                        }
                        sendMessage(sender, "Se dio el armamento de manera exitosa", TypeMessages.SUCCESS);
                    }else{
                        sendMessage(sender, "El tipo de armamento no existe 11", TypeMessages.ERROR);
                    }
                }else {
                    sendMessage(sender, "El jugador no existe o no esta conectado", TypeMessages.ERROR);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                return null;
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
                    case CHARGER -> {
                        return CommandUtils.listTab(args[2] ,CommandUtils.enumsToStrings(ListCharger.values(), false), ModeTab.StartWith);
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
