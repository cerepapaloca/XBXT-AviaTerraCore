package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.CacheLimboFile;

public class FliesCacheLimbo extends FilesYams {
    public FliesCacheLimbo() {
        super("cacheLimbo", CacheLimboFile.class);
    }
}
