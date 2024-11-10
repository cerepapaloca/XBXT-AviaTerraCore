package net.atcore.Test;

import lombok.Getter;
import org.jetbrains.annotations.TestOnly;

@Getter
@TestOnly
public enum TypeTest {
    BAN(new BanTest()),
    MESSAGE(new MessageTest());

    TypeTest(RunTest runTest) {
        this.runTest = runTest;
    }

    private final RunTest runTest;
}
