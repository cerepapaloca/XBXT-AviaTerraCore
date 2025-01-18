package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.PlayerDataFile;

public class PlayersDataFiles extends FilesYams {
    public PlayersDataFiles() {
        super("playerData", PlayerDataFile.class, false);
    }
}
