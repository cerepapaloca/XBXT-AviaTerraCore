package net.atcore.misc;

import io.undertow.Undertow;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.*;
import static com.vexsoftware.votifier.util.GsonInst.gson;
import static net.atcore.messages.MessagesManager.logConsole;

public class WebServer {

    public WebServer() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (var is = new File(AviaTerraCore.getInstance().getDataFolder() + "/keystore.p12").toURI().toURL().openStream()) {
                keyStore.load(is, Config.getPasswordSSL().toCharArray());
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, Config.getPasswordSSL().toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            Undertow server = Undertow.builder()
                    .addHttpsListener(8443, "0.0.0.0", sslContext)
                    .setHandler(exchange -> {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_ORIGIN), "*");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_METHODS), "GET, POST, OPTIONS");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_HEADERS), "Content-Type, Authorization");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_CREDENTIALS), "true");
                        if (exchange.getRequestMethod().toString().equals("OPTIONS")) {
                            exchange.getResponseSender().send("CORS OK");
                            return;
                        }

                        // Recopilación de datos
                        Map<String, Object> data = new HashMap<>();
                        data.put("uniqueUsers", LoginManager.getDataLogin().size());
                        data.put("activeTime", AviaTerraCore.getActiveTime());
                        data.put("onlinePlayer", Bukkit.getOnlinePlayers().size());
                        data.put("maxPlayers", Bukkit.getMaxPlayers());
                        data.put("frameDupe", Config.getChaceDupeFrame());

                        long size = 0;
                        for (World world : Bukkit.getWorlds()) {
                            size += GlobalUtils.getFolderSize(world.getWorldFolder());
                        }
                        data.put("sizeWorlds", size);

                        String[] jugadores = Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new);
                        data.put("namesPlayers", jugadores);

                        String jsonResponse = gson.toJson(data);
                        // Respuesta con CORS habilitado
                        exchange.getResponseSender().send(jsonResponse);
                    }).build();
            server.start();
            logConsole("API website" + TypeMessages.SUCCESS.getMainColor() + " Ok", TypeMessages.INFO, CategoryMessages.PRIVATE, false);
        } catch (Exception e) {
            MessagesManager.sendWaringException("Error al iniciar la api de pagina web", e);
        }
    }
}
