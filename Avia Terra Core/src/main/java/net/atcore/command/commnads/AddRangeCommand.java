package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RangeType;
import net.luckperms.api.model.group.Group;
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
                "das una tags donde se puede dar un rango durante un tiempo determinado"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            case 1 -> sendMessage(sender, Message.COMMAND_ADD_RANGE_MISSING_ARGUMENT_TIME, TypeMessages.ERROR);
            default -> {
                List<Group> groups = AviaTerraCore.getLp().getGroupManager().getLoadedGroups().stream().toList();
                List<String> nameRage = new ArrayList<>();
                for (Group group : groups) nameRage.add(group.getName());

                if (nameRage.contains(args[0])){
                    long time;
                    try {
                        time = CommandUtils.StringToMilliseconds(args[1], true);
                    }catch (RuntimeException e){
                        sendMessage(sender, Message.COMMAND_GENERIC_FORMAT_DATE_ERROR, TypeMessages.ERROR);
                        return;
                    }
                    RangeType range = RangeType.valueOf(args[0].toUpperCase());
                    ItemStack item = new ItemStack(Material.NAME_TAG);
                    GlobalUtils.setPersistentData(item, "durationRange", PersistentDataType.LONG, time);
                    GlobalUtils.setPersistentData(item, "rangeName", PersistentDataType.STRING, range.name());
                    GlobalUtils.setPersistentData(item, "dateCreationRange", PersistentDataType.LONG, System.currentTimeMillis());
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    //meta.setDisplayName(GlobalUtils.applyGradient("<#f0f0f0>asdas<#404040>"));
                    String displayName = GlobalUtils.applyGradient(
                                    "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(range.getColor()), 0f, 0.35f, -0.f) + ">" +
                                            String.format(Message.COMMAND_ADD_RANGE_NAME.getMessage(), range.getDisplayName(), GlobalUtils.timeToString(time, 2) ) +
                                            "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(range.getColor()), 0f, -0.2f, 0.1f) + ">");
                    meta.setDisplayName(displayName);
                    item.setItemMeta(meta);
                    if(args.length == 2){
                        if (sender instanceof Player playerSender){
                            addItemPlayer(item, playerSender, false, true);
                        }else{
                            sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, TypeMessages.ERROR);
                        }
                    }else{
                        CommandUtils.executeForPlayer(sender, args[2], true, fakePlayer -> GlobalUtils.addItemPlayer(item, fakePlayer.player(), false, true));
                        sendMessage(sender, Message.COMMAND_ADD_RANGE_SUCCESSFUL, TypeMessages.SUCCESS);
                    }
                }else{
                    sendMessage(sender, Message.COMMAND_ADD_RANGE_MISSING_ARGUMENT_TIME, TypeMessages.ERROR);
                }
            }

        }

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                List<Group> range = AviaTerraCore.getLp().getGroupManager().getLoadedGroups().stream().toList();
                List<String> nameRage = new ArrayList<>();
                for (Group group : range) nameRage.add(group.getName());
                return CommandUtils.listTab(args[0], nameRage);
            }
            case 2 -> {
                return CommandUtils.listTabTime(args[1], true);
            }
            case 3 -> {
                return CommandUtils.tabForPlayer(args[2]);
            }
        }
        return null;
    }
}
