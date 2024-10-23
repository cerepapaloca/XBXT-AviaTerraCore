package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Messages.TypeMessages;
import net.atcore.Utils.GlobalUtils;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;
import static net.atcore.Utils.GlobalUtils.addItemPlayer;

public class CommandAddRange extends BaseTabCommand {

    public CommandAddRange() {
        super("addRange",
                "/addRange <Rango> <Tiempo> <Jugador>",
                "aviaterra.command.addrangos",
                true,
                "das una tags donde se puede dar un rango durante un tiempo determinado"
        );
    }//

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            case 1 -> sendMessage(sender, "Te falta especificar el tiempo de duración", TypeMessages.ERROR);
            default -> {
                List<Group> range = AviaTerraCore.getLP().getGroupManager().getLoadedGroups().stream().toList();
                List<String> nameRage = new ArrayList<>();
                for (Group group : range) nameRage.add(group.getName());

                if (nameRage.contains(args[0])){
                    long time;
                    try {
                        time = CommandUtils.StringToMilliseconds(args[1], true);
                    }catch (RuntimeException e){
                        sendMessage(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                        return;
                    }
                    ItemStack item = new ItemStack(Material.NAME_TAG);
                    GlobalUtils.addPersistentDataItem(item, "duration", PersistentDataType.LONG, time);
                    GlobalUtils.addPersistentDataItem(item, "range", PersistentDataType.STRING, args[0]);
                    GlobalUtils.addPersistentDataItem(item, "dateCreation", PersistentDataType.LONG, System.currentTimeMillis());
                    GlobalUtils.addProtectionAntiDupe(item);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName("Rango " + args[0] + " por " + GlobalUtils.timeToString(time, 2));
                    item.setItemMeta(meta);
                    if(args.length == 2){
                        if (sender instanceof Player playerSender){
                            addItemPlayer(item, playerSender, false);
                        }else{
                            sendMessage(sender, "no eres un jugador para recibir el rango", TypeMessages.ERROR);
                        }
                    }else{
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null){
                            sendMessage(sender, "El jugador no existe o esta desconectado", TypeMessages.ERROR);

                        }else{
                            GlobalUtils.addItemPlayer(item, player, false);
                            sendMessage(sender, "El item se le dio exitosamente", TypeMessages.SUCCESS);
                        }
                    }
                }else{
                    sendMessage(sender, "El rango no existe", TypeMessages.ERROR);
                }
            }

        }

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                List<Group> range = AviaTerraCore.getLP().getGroupManager().getLoadedGroups().stream().toList();
                List<String> nameRage = new ArrayList<>();
                for (Group group : range) nameRage.add(group.getName());
                return CommandUtils.listTab(args[0], nameRage);
            }
            case 2 -> {
                return CommandUtils.listTabTime(args[1], true);
            }
        }
        return null;
    }
}
