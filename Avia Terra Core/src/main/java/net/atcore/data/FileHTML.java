package net.atcore.data;

import org.jetbrains.annotations.NotNull;

public abstract class FileHTML extends File {

    protected FileHTML(@NotNull String fileName) {
        super(fileName, "html", "HTML");
        copyDefaultConfig();
        loadData();
    }
}
