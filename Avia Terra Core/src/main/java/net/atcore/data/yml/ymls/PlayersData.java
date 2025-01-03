package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.PlayerDataFile;

public class PlayersData extends FilesYams {
    public PlayersData() {
        super("playerData", PlayerDataFile.class);
    }
}
