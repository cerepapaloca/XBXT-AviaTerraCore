package net.atcore.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import javax.swing.plaf.ViewportUI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangAPI {

    public static boolean isLoginOnMojang(String playerName) {
        try {
            // URL de la API de Mojang para obtener el UUID a partir del nombre
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Si la respuesta es 200, el jugador tiene una cuenta Mojang
                return true;
            } else {
                // Si la respuesta es distinta de 200, no tiene cuenta Mojang
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRegisteredOnMojang(String playerName) {
        try {
            // URL de la API de Mojang para obtener el UUID basado en el nombre del jugador
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            Bukkit.getConsoleSender().sendMessage(responseCode + " code");
            if (responseCode == 200) {
                // Si la respuesta es 200, significa que el jugador está registrado en Mojang
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parsear la respuesta JSON
                if (!response.isEmpty()) {
                    return true; // Jugador registrado en Mojang
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false; // Jugador no está registrado en Mojang
    }

    public static JsonObject checkPlayerStatus(String username, String serverId, String ip) throws Exception {
        try {
            String urlString = String.format("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s&ip=%s", username, serverId, ip);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Leer la respuesta
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Convertir la respuesta a un objeto JSON
            return JsonParser.parseString(response.toString()).getAsJsonObject();
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    /*
                        // Crear el contenedor del paquete Play.Server.LOGIN
                        PacketContainer loginPacket = new PacketContainer(PacketType.Play.Server.LOGIN);

                        // Establecer el ID de la entidad del jugador
                        int playerId = event.getPlayer().getEntityId();
                        loginPacket.getIntegers().write(0, playerId);

                        // Modo Hardcore
                        loginPacket.getBooleans().write(0, false); // Hardcore desactivado

                        // Lista de niveles (dimensiones) disponibles
                        // Establecer el tamaño del array de dimensiones
                        loginPacket.getIntegers().write(0, 1); // Indicar que hay un nombre de dimensión
                        loginPacket.getStringArrays().write(0, new String[]{"minecraft:overworld"});

                        // Máximo de jugadores
                        loginPacket.getIntegers().write(1, 20); // Max players

                        // Chunk Radius (distancia de renderizado)
                        loginPacket.getIntegers().write(2, 10); // Distancia de chunks

                        // Simulation Distance (distancia de simulación)
                        loginPacket.getIntegers().write(3, 10); // Simulation distance

                        // Reduced Debug Info (información de depuración reducida)
                        loginPacket.getBooleans().write(1, false); // Depuración completa habilitada

                        // Show Death Screen (pantalla de muerte)
                        loginPacket.getBooleans().write(2, true); // Pantalla de muerte habilitada

                        // Limited Crafting (crafteo limitado)
                        loginPacket.getBooleans().write(3, false); // No restricciones de crafteo

                        // CommonPlayerSpawnInfo (información común del spawn)
                        loginPacket.getMinecraftKeys().write(0, new MinecraftKey("minecraft:overworld")); // Dimension Type
                        loginPacket.getMinecraftKeys().write(1, new MinecraftKey("minecraft:overworld")); // Dimension
                        loginPacket.getLongs().write(0, event.getPlayer().getWorld().getSeed()); // Semilla del mundo
                        loginPacket.getGameModes().write(0, EnumWrappers.NativeGameMode.CREATIVE); // Modo de juego actual
                        loginPacket.getGameModes().write(1, EnumWrappers.NativeGameMode.NOT_SET); // Modo de juego anterior
                        loginPacket.getBooleans().write(4, false); // No es un mundo de depuración
                        loginPacket.getBooleans().write(5, true); // Es un mundo plano

                        // Secure Chat (chat seguro obligatorio)
                        loginPacket.getBooleans().write(6, true); // Enforzar chat seguro

                        // Enviar el paquete al jugador
                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), loginPacket);*/

    //packetLogin.getLev().write(0, 256);

}

