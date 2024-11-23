package net.atcore.test;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public final class MessageTest implements RunTest {

    @Override
    public void runTest(AviaTerraPlayer player) {
        for (TypeMessages type : TypeMessages.values()) {
            for (CategoryMessages category : CategoryMessages.values()) {
                MessagesManager.sendMessage(player.getPlayer(), "Hola mundo!! esto es un <|Test|>", type, category);
            }
        }
    }
}
