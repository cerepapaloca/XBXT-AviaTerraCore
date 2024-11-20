package net.atcore.Test;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.TypeMessages;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public class MessageTest implements RunTest {

    @Override
    public void runTest(AviaTerraPlayer player) {
        player.sendMessage("Mensaje de información y <|esto|> un dato importante", TypeMessages.INFO);
        player.sendMessage("Mensaje Exitoso y <|esto|> un dato importante", TypeMessages.SUCCESS);
        player.sendMessage("Mensaje de Advertencia y <|esto|> un dato importante", TypeMessages.WARNING);
        player.sendMessage("Mensaje de Error y <|esto|> un dato importante", TypeMessages.ERROR);
        player.sendMessage("Mensaje sin categoría y <|esto|> un dato importante", null);
    }
}
