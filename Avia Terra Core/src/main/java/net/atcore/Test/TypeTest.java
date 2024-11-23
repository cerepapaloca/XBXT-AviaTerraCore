package net.atcore.test;

import lombok.Getter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.TypeMessages;
import org.jetbrains.annotations.TestOnly;

@Getter
@TestOnly
public enum TypeTest {
    BAN(new BanTest()),
    MESSAGE(new MessageTest()),
    PERSISTENT_DATA(new ItemPersistenDataTest());

    TypeTest(RunTest runTest) {
        this.runTest = runTest;
    }

    private final RunTest runTest;

    public void runtTest(AviaTerraPlayer player) {
        runTest.runTest(player);
        player.sendMessage("Test terminado", TypeMessages.SUCCESS);
    }
}
