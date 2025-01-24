package net.atcore.utils.debug;

import lombok.Getter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.jetbrains.annotations.TestOnly;

@Getter
@TestOnly
public enum TypeTest {
    BAN(new BanDebug()),
    MESSAGE(new MessageDebug()),
    LOGIN_REMOVE(new LoginRemoveDebug()),
    PERSISTENT_DATA(new ItemPersistenDataDebug());

    TypeTest(RunTest runTest) {
        this.runTest = runTest;
    }

    private final RunTest runTest;

    public void runtTest(AviaTerraPlayer player) {
        runTest.runTest(player);
    }
}
