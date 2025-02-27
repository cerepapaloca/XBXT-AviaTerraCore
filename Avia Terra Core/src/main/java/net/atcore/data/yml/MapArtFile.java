package net.atcore.data.yml;

import lombok.Getter;
import net.atcore.data.FileYaml;
import org.bukkit.Bukkit;
import org.w3c.dom.ls.LSInput;

import java.util.*;

public class MapArtFile extends FileYaml {
    public MapArtFile(String fileName, String folderName) {
        super(fileName, folderName, false, true);
    }

    public final static Set<MapData> MAP_DATA_LIST = new HashSet<>();

    @Override
    public void loadData() {
        loadConfig();
        String author = fileYaml.getString("author");
        int cols = fileYaml.getInt("cols");
        int rows = fileYaml.getInt("rows");
        List<?> raws = fileYaml.getList("maps-ids");
        if (raws == null) return;
        List<Integer> mapsIds = raws.stream().filter(raw -> raw instanceof Integer).map(raw -> (Integer) raw).toList();
        MapArtFile.MAP_DATA_LIST.add(new MapData(author, mapsIds, cols, rows));
    }

    @Override
    public void saveData() {
        String id = file.getName().replace(".yml", "");
        MapData mapData = getMapData(id);
        if (mapData == null) return;
        fileYaml.set("cols", mapData.cols);
        fileYaml.set("rows", mapData.rows);
        fileYaml.set("author", mapData.author);
        fileYaml.set("maps-ids", mapData.mapIds);
        saveConfig();
    }

    public static MapData getMapData(String ids) {
        for (MapData data : MAP_DATA_LIST) {
            if (ids.equals(data.getId())) {
                return data;
            }
        }
        return null;
    }

    @Getter
    public static class MapData {

        public MapData(String author, List<Integer> ids, int cols, int rows) {
            this.author = author;
            this.cols = cols;
            this.rows = rows;
            mapIds.addAll(ids);
        }

        private final int cols, rows;
        private final String author;
        private final List<Integer> mapIds = new ArrayList<>();

        public String getId(){
            return author + mapIds.toString().replace(" ", "");
        }
    }

    @Override
    protected void addFile(){

    }
}
