package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.CacheLimboFile;

public class CacheLimboFlies extends FilesYams {
    public CacheLimboFlies() {
        super("cacheLimbo", CacheLimboFile.class, false);
    }
}
