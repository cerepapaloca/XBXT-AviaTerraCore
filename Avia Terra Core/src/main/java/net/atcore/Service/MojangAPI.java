package net.atcore.Service;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangAPI {

    public static boolean isRegisteredOnMojang(String playerName) {
        try {
            //URL de la API de Mojang para obtener el UUID basado en el nombre del jugador
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            Bukkit.getConsoleSender().sendMessage(responseCode + " code");
            if (responseCode == 200) {
                //si la respuesta es 200, significa que el jugador está registrado en Mojang
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parsear la respuesta JSON
                if (!response.isEmpty()) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}

