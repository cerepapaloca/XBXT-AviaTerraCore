package net.atcore.data;

import org.jetbrains.annotations.NotNull;

public abstract class FileMarkdownDocumentation extends File {

    protected FileMarkdownDocumentation(@NotNull String fileName) {
        super(fileName, "md", "MD");
        copyDefaultConfig();
        loadData();
    }
}
