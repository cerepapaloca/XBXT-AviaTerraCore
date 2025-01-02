package net.atcore.utils.debug;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public final class MessageDebug implements RunTest {

    @Override
    public void runTest(AviaTerraPlayer player) {
        for (MessagesType type : MessagesType.values()) {
            for (CategoryMessages category : CategoryMessages.values()) {
                MessagesManager.sendMessage(player.getPlayer(), Message.TEST_MESSAGE.getMessage(), type, category);
            }
        }
    }
}
