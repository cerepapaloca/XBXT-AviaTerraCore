package net.atcore.placeholder;

import net.atcore.AviaTerraCore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceHolderLocalHandler {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([\\w-]+)%");

    public static String processPlaceholders(@Nullable Player player, String input) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            //if (!placeholder.startsWith(AviaTerraCore.getInstance().getName() + "_")) continue;
            String replacement = onPlaceholderRequest(player, placeholder.replaceFirst(AviaTerraCore.getInstance().getName() + "_", ""));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    @NotNull
    public static String onPlaceholderRequest(@Nullable Player player, @NotNull String identifier){
        return PlaceHolderSection.HOLDERS.stream().filter(placeHolder -> placeHolder.getIdentifier().equals(identifier)).findFirst().map(placeHolder -> placeHolder.onPlaceholderRequest(player)).orElse("null");
    }
}
