package net.atcore.command.commnads;

import lombok.Getter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// Gracias a 8b8t por el comando
public class NameColorCommand extends BaseTabCommand {


    private final List<String> colorOptions;
    private final List<String> styleOptions;
    private static final Set<Gradient> GRADIENTS = new HashSet<>();

    @SuppressWarnings("deprecation")
    public NameColorCommand() {
        super("nameColor",
                new ArgumentUse("nameColor").addArg(ChatColor.values()),
                CommandVisibility.PUBLIC,
                "Cambias de color tu nombre"
        );
        GRADIENTS.clear();
        new Gradient("fire", "#F3904F", "#E7654A");
        new Gradient("sky", "#1488CC", "#0B14C3");
        new Gradient("pink", "#F4C4F3", "#EC00E9");
        new Gradient("green", "#59ED33", "#165818");
        new Gradient("silver", "#C9C9C9", "#2F2F2F");
        new Gradient("red", "#BF0C1F", "#DE4E4E");
        new Gradient("aqua", "#4FF3EA", "#00828D");
        new Gradient("retro", "#F34F4F", "#65008D");
        new Gradient("mango", "#2BBFA7","#FFC600");
        new Gradient("orange", "#FF9800", "#BC4664");
        new Gradient("red_dark", "#212121", "#f4143f");
        new Gradient("rick", "#98f8dd", "#F6FF58");
        new Gradient("purple", "#d481d2", "#703b94");
        new Gradient("meat", "meat");
        colorOptions = getColorOptions();
        styleOptions = getStyleOptions();
        addAlias("nc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
            return;
        }

        if (args.length < 1) {
            MessagesManager.sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            return;
        }

        String colorName = args[0].toLowerCase();
        if (!colorOptions.contains(colorName)) {
            MessagesManager.sendMessage(sender, Message.COMMAND_NAME_COLOR_NOT_FOUND);
            return;
        }else if (colorName.equalsIgnoreCase("reset")){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            atp.setNameColor(player.getName());
            atp.getPlayerDataFile().saveData();
            player.displayName(Component.text(player.getName()));
        }

        String colorCode = getColorCode(colorName);
        StringBuilder nameBuilder = new StringBuilder(colorCode);

        for (int i = 1; i < args.length; i++) {
            String styleCode = getStyleCode(args[i]);
            if (styleCode != null) {
                nameBuilder.append(styleCode);
            }
        }

        nameBuilder.append(player.getName());
        String displayNameString = nameBuilder.toString();
        Component component = GlobalUtils.chatColorLegacyToComponent(displayNameString);
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        atp.setNameColor(displayNameString);
        atp.getPlayerDataFile().saveData();
        player.displayName(component);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args){
        if (args.length == 1) {
            return CommandUtils.listTab(args[0], colorOptions);
        } else if (args.length > 1) {
            return CommandUtils.listTab(args[args.length - 1], styleOptions);
        }
        return List.of();
    }

    public List<String> getColorOptions() {
        List<String> colorOptions = new ArrayList<>(List.of("black",
                "dark_blue",
                "dark_green",
                "dark_aqua",
                "dark_red",
                "dark_purple",
                "gold",
                "gray",
                "dark_gray",
                "blue",
                "green",
                "aqua",
                "red",
                "light_purple",
                "yellow",
                "white",
                "reset")
        );
        colorOptions.addAll(GRADIENTS.stream().map(Gradient::getName).toList());
        return colorOptions;
    }

    public List<String> getStyleOptions() {
        return List.of("bold", "italic", "underline", "strikethrough", "reset");
    }

    private String getColorCode(String colorName) {
        return switch (colorName) {
            case "black" -> "&0";
            case "dark_blue" -> "&1";
            case "dark_green" -> "&2";
            case "dark_aqua" -> "&3";
            case "dark_red" -> "&4";
            case "dark_purple" -> "&5";
            case "gold" -> "&6";
            case "gray" -> "&7";
            case "dark_gray" -> "&8";
            case "blue" -> "&9";
            case "green" -> "&a";
            case "aqua" -> "&b";
            case "red" -> "&c";
            case "light_purple" -> "&d";
            case "yellow" -> "&e";
            case "white" -> "&f";
            default -> getGradient(colorName);
        };
    }

    private String getGradient(String colorName) {
        for (Gradient gradient : GRADIENTS) {
            if (gradient.getName().equals(colorName)) {
                return gradient.tag;
            }
        }
        return "";
    }

    private String getStyleCode(String styleName) {
        return switch (styleName.toLowerCase()) {
            case "bold" -> "&l";
            case "italic" -> "&o";
            case "underline" -> "&n";
            case "strikethrough" -> "&m";
            default -> null;
        };
    }
    @Getter
    private static class Gradient{
        public Gradient(String name, String... tag) {
            this.name = "gradient_" + name;
            this.tag =  "<gradient:" + String.join(":", tag) + ">";
            GRADIENTS.add(this);
        }

        private final String name, tag;
    }
}

