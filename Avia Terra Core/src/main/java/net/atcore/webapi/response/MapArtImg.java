package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;
import net.atcore.data.yml.MapArtFile;
import net.atcore.messages.MessagesManager;
import net.atcore.webapi.BaseApi;
import net.atcore.webapi.ContentType;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class MapArtImg extends BaseApi {

    public MapArtImg() {
        super("mapArtImg", ContentType.IMAGE_PNG);
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        String idMap = request.getRequestPath().replaceFirst("/mapArtImg/", "");
        MapArtFile.MapData mapData = MapArtFile.getMapData(idMap);
        if (mapData == null) {
            return MapArtFile.MAP_DATA_LIST.stream().map(MapArtFile.MapData::getId).toList();
        }
        List<Integer> mapIds = mapData.getMapIds();
        int mapSize = 128; // Cada mapa tiene 128x128 píxeles
        int cols = mapData.getCols();
        int rows = mapData.getRows();
        int width = cols * mapSize;
        int height = rows * mapSize;

        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (index >= mapIds.size()) break;

                Integer id = mapIds.get(index++);
                Path mapPath = Paths.get(Bukkit.getWorlds().getFirst().getWorldFolder().getPath() + "/data/map_" + id + ".dat");

                try {
                    // Leer el archivo .dat
                    CompoundTag nbt = read(mapPath.toFile()).unpack().get("data").asCompound();
                    byte[] data = nbt.get("colors").byteArray();
                    if (data == null) continue;

                    BufferedImage img = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < mapSize; y++) {
                        for (int x = 0; x < mapSize; x++) {
                            byte input = data[x + y * mapSize];
                            int colId = (input >>> 2) & 0b111111;
                            byte shader = (byte) (input & 0b11);

                            BasicColor color = BasicColor.colors.getOrDefault(colId, BasicColor.TRANSPARENT);
                            img.setRGB(x, y, color.shaded(shader));
                        }
                    }

                    // Dibujar en la imagen final
                    g.drawImage(img, col * mapSize, row * mapSize, null);
                } catch (IOException ignored) {
                }
            }
        }
        g.dispose();

        // Guardar la imagen final como PNG
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(finalImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            MessagesManager.sendWaringException("Error al crear el mapArt", e);
        }
        return null;
    }

    public static Tag read(File f) throws IOException {
        InputStream input = new FileInputStream(f);
        byte[] fileContent = IOUtils.toByteArray(input);
        return NamedTag.read(
                new DataInputStream(new ByteArrayInputStream(gzipDecompress(fileContent)))
        );
    }

    // Source: https://stackoverflow.com/a/44922240
    public static byte[] gzipDecompress(byte[] compressedData) {
        byte[] result = new byte[]{};
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzipIS = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            result = bos.toByteArray();
        } catch (IOException e) {
            MessagesManager.sendWaringException("error al descomprimir el .gz", e);
        }
        return result;
    }


    private static BasicColor toCol(int r, int g, int b) {
        return new BasicColor(r, g, b);
    }



    @RequiredArgsConstructor
    public static class BasicColor {

        final int r, g, b;

        final static BasicColor TRANSPARENT = new BasicColor(0, 0, 0) {
            @Override
            public int shaded(byte b) {
                return 0;
            }
        };

        public int shaded(byte shaderB) {
            double shader = 0;
            switch (shaderB) {
                case 0:
                    shader = 0.71;
                    break;
                case 1:
                    shader = 0.85;
                    break;
                case 2:
                    shader = 1.0;
                    break;
                case 3:
                    shader = 0.53;
                    break;
            }

            return 255 << 24 | toInt(r, shader) << 16 | toInt(g, shader) << 8 | toInt(b, shader);
        }

        private int toInt(int c, double shader) {
            return (int) Math.round(c * shader);
        }

        static Map<Integer, BasicColor> colors;
        static {
            colors = new HashMap<>();
            colors.put(0, BasicColor.TRANSPARENT);
            colors.put(1, toCol(127, 178, 56));
            colors.put(2, toCol(247, 233, 163));
            colors.put(3, toCol(199, 199, 199));
            colors.put(4, toCol(255, 0, 0));
            colors.put(5, toCol(160, 160, 255));
            colors.put(6, toCol(167, 167, 167));
            colors.put(7, toCol(0, 124, 0));
            colors.put(8, toCol(255, 255, 255));
            colors.put(9, toCol(164, 168, 184));
            colors.put(10, toCol(151, 109, 77));
            colors.put(11, toCol(112, 112, 112));
            colors.put(12, toCol(64, 64, 255));
            colors.put(13, toCol(143, 119, 72));
            colors.put(14, toCol(255, 252, 245));
            colors.put(15, toCol(216, 127, 51));
            colors.put(16, toCol(178, 76, 216));
            colors.put(17, toCol(102, 153, 216));
            colors.put(18, toCol(229, 229, 51));
            colors.put(19, toCol(127, 204, 25));
            colors.put(20, toCol(242, 127, 165));
            colors.put(21, toCol(76, 76, 76));
            colors.put(22, toCol(153, 153, 153));
            colors.put(23, toCol(76, 127, 153));
            colors.put(24, toCol(127, 63, 178));
            colors.put(25, toCol(51, 76, 178));
            colors.put(26, toCol(102, 76, 51));
            colors.put(27, toCol(102, 127, 51));
            colors.put(28, toCol(153, 51, 51));
            colors.put(29, toCol(25, 25, 25));
            colors.put(30, toCol(250, 238, 77));
            colors.put(31, toCol(92, 219, 213));
            colors.put(32, toCol(74, 128, 255));
            colors.put(33, toCol(0, 217, 58));
            colors.put(34, toCol(129, 86, 49));
            colors.put(35, toCol(112, 2, 0));
            colors.put(36, toCol(209, 177, 161));
            colors.put(37, toCol(159, 82, 36));
            colors.put(38, toCol(149, 87, 108));
            colors.put(39, toCol(112, 108, 138));
            colors.put(40, toCol(186, 133, 36));
            colors.put(41, toCol(103, 117, 53));
            colors.put(42, toCol(160, 77, 78));
            colors.put(43, toCol(57, 41, 35));
            colors.put(44, toCol(135, 107, 98));
            colors.put(45, toCol(87, 92, 92));
            colors.put(46, toCol(122, 73, 88));
            colors.put(47, toCol(76, 62, 92));
            colors.put(48, toCol(76, 50, 35));
            colors.put(49, toCol(76, 82, 42));
            colors.put(50, toCol(142, 60, 46));
            colors.put(51, toCol(37, 22, 16));
            colors.put(52, toCol(189, 48, 49));
            colors.put(53, toCol(148, 63, 97));
            colors.put(54, toCol(92, 25, 29));
            colors.put(55, toCol(22, 126, 134));
            colors.put(56, toCol(58, 142, 140));
            colors.put(57, toCol(86, 44, 62));
            colors.put(58, toCol(20, 180, 133));
            colors.put(59, toCol(100, 100, 100));
            colors.put(60, toCol(216, 175, 147));
            colors.put(61, toCol(127, 167, 150));
        }
    }

}
