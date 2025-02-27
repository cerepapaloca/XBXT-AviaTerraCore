package net.atcore.data.yml.ymls;

import net.atcore.data.FilesYams;
import net.atcore.data.yml.MapArtFile;

public class MapArtsFiles extends FilesYams {
    public MapArtsFiles() {
        super("mapArt", MapArtFile.class, true);
    }

    @Override
    public void reloadConfigs(){
        MapArtFile.MAP_DATA_LIST.clear();
        super.reloadConfigs();
    }
}
