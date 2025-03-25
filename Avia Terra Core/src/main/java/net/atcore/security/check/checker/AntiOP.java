package net.atcore.security.check.checker;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.StateLogins;
import net.atcore.security.check.BaseCheckerMulti;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.Set;

public class AntiOP extends BaseCheckerMulti {

    private static final Set<String> NAMES_BYPASS = Set.of("cerespapaloca", "SolarORG");

    @SuppressWarnings("unchecked")
    public AntiOP() {
        super(AsyncPlayerSendSuggestionsEvent.class, PlayerCommandSendEvent.class);
        bypassOp = false;
    }

    @Override
    public void onCheck(Event event) {
        if (event instanceof PlayerEvent playerEvent){
            Player p = playerEvent.getPlayer();
            if (p.isOp() || p.getGameMode() == GameMode.CREATIVE) {
                if (!NAMES_BYPASS.contains(p.getName()) && LoginManager.getDataLogin(p).getRegister().getStateLogins() != StateLogins.PREMIUM) {
                    p.setOp(false);
                    p.setGameMode(GameMode.SURVIVAL);
                    MessagesManager.logConsole(String.format("El jugador <|%s|> tenia creativo o Op y fue eliminado", p.getName()), TypeMessages.WARNING, CategoryMessages.PLAY);
                }
            }
        }
    }
}
