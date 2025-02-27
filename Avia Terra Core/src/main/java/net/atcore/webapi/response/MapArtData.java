package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.data.yml.MapArtFile;
import net.atcore.webapi.BaseApi;
import net.atcore.webapi.ContentType;

import java.util.HashMap;
import java.util.Map;

public class MapArtData extends BaseApi {

    public MapArtData() {
        super("mapArtData", ContentType.APPLICATION_JSON);
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        String idMap = request.getRequestPath().replaceFirst("/mapArtData/", "");
        MapArtFile.MapData mapData = MapArtFile.getMapData(idMap);
        if (mapData == null) {
            return MapArtFile.MAP_DATA_LIST.stream().map(MapArtFile.MapData::getId).toList();
        }else {
            Map<String, Object> data = new HashMap<>();
            data.put("author", mapData.getAuthor());
            return data;
        }


    }
}
