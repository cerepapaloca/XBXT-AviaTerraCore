package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.Range;
import net.atcore.utils.RangeList;
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

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.utils.GlobalUtils.addItemPlayer;

public class AddRangeCommand extends BaseTabCommand {

    public AddRangeCommand() {
        super("addRange",
                "/addRange <Rango> <Tiempo> <Jugador>",
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
                List<Group> groups = AviaTerraCore.getLP().getGroupManager().getLoadedGroups().stream().toList();
                List<String> nameRage = new ArrayList<>();
                for (Group group : groups) nameRage.add(group.getName());

                if (nameRage.contains(args[0])){
                    long time;
                    try {
                        time = CommandUtils.StringToMilliseconds(args[1], true);
                    }catch (RuntimeException e){
                        sendMessage(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                        return;
                    }
                    ItemStack item = new ItemStack(Material.NAME_TAG);
                    GlobalUtils.setPersistentDataItem(item, "durationRange", PersistentDataType.LONG, time);
                    GlobalUtils.setPersistentDataItem(item, "rangeName", PersistentDataType.STRING, args[0]);
                    GlobalUtils.setPersistentDataItem(item, "dateCreationRange", PersistentDataType.LONG, System.currentTimeMillis());
                    GlobalUtils.addProtectionAntiDupe(item);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    Range range = GlobalConstantes.RANGOS_COLORS.get(RangeList.valueOf(args[0].toUpperCase()));
                    //meta.setDisplayName(GlobalUtils.applyGradient("<#f0f0f0>asdas<#404040>"));
                    meta.setDisplayName(range.getIcon() + GlobalUtils.applyGradient( "<" + GlobalUtils.colorToStringHex(range.getColor()) + ">"
                             + " Duración: " + GlobalUtils.timeToString(time, 2) + "<#696969>"));
                    item.setItemMeta(meta);
                    if(args.length == 2){
                        if (sender instanceof Player playerSender){
                            addItemPlayer(item, playerSender, false, false);
                        }else{
                            sendMessage(sender, "no eres un jugador para recibir el rango", TypeMessages.ERROR);
                        }
                    }else{
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null){
                            sendMessage(sender, "El jugador no existe o esta desconectado", TypeMessages.ERROR);

                        }else{
                            GlobalUtils.addItemPlayer(item, player, false, false);
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
