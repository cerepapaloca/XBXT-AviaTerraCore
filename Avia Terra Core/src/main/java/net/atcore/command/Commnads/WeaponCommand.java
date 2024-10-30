package net.atcore.command.Commnads;

import net.atcore.armament.BaseArmament;
import net.atcore.armament.TypeArmament;
import net.atcore.command.BaseTabCommand;
import net.atcore.armament.GunsSection;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class WeaponCommand extends BaseTabCommand {

    public WeaponCommand() {
        super("weapon",
                "/weapon <Jugador> <Tipo de armamento> <Nombre del armamento> <Cantidad> <Munición \"solo para los cargadores\">",
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
                        typeArmament = TypeArmament.valueOf(args[1]);
                    }catch (Exception e) {
                        sendMessage(sender, "El tipo de armamento no existe", TypeMessages.ERROR);
                        return;
                    }
                    switch (typeArmament) {
                        case WEAPON -> BaseArmament = GunsSection.getWeapon(args[2]);
                        case CHARGER -> BaseArmament = GunsSection.getCharger(args[2]);
                        case AMMO -> BaseArmament = GunsSection.getAmmon(args[2]);
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
                    }
                }else {
                    sendMessage(sender, "El jugador no existe o no esta conectado", TypeMessages.ERROR);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }

}
