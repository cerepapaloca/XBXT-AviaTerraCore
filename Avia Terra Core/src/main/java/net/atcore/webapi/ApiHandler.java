package net.atcore.webapi;

import com.google.common.io.Files;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.net.HttpHeaders.*;
import static com.vexsoftware.votifier.util.GsonInst.gson;

public final class ApiHandler {

    public static final HashSet<BaseApi> APIS = new HashSet<>();
    public static Undertow server;

    public ApiHandler() {
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
                        //Se le añade las propiedades del encabezado
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_ORIGIN), "*");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_METHODS), "GET, POST, OPTIONS");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_HEADERS), "Content-Type, Authorization");
                        exchange.getResponseHeaders().put(HttpString.tryFromString(ACCESS_CONTROL_ALLOW_CREDENTIALS), "true");
                        if (exchange.getRequestPath().equals("/favicon.ico")) {
                            exchange.setStatusCode(404);
                            exchange.endExchange();
                            return;
                        }
                        for (BaseApi api : APIS) {
                            if (!exchange.getRequestPath().startsWith("/" + api.getIdentifier())) continue;
                            exchange.setStatusCode(200);


                            Object o = api.onRequest(exchange);

                            if (o == null) {
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ContentType.APPLICATION_JSON.getType());
                                exchange.setStatusCode(404);
                                exchange.getResponseSender().send(gson.toJson(null));
                                return;
                            }else {
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, api.getContentType().getType());
                            }

                            switch (api.getContentType()) {
                                case APPLICATION_JSON -> {
                                    if (o instanceof File file) {
                                        exchange.getResponseSender().send(Files.asCharSource(file, StandardCharsets.UTF_8).read());
                                    }else {
                                        exchange.getResponseSender().send(gson.toJson(o));
                                    }
                                }
                                case IMAGE_PNG -> {
                                    if (o instanceof byte[] image) {
                                        exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, image.length);
                                        exchange.getResponseSender().send(ByteBuffer.wrap(image));
                                    }
                                }
                            }
                            return;
                        }
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ContentType.APPLICATION_JSON.getType());
                        exchange.setStatusCode(404);
                        HashMap<String, String[]> response = new HashMap<>();
                        response.put("Allow Sections", APIS.stream().map(BaseApi::getIdentifier).toArray(String[]::new));
                        exchange.getResponseSender().send(gson.toJson(response));
                    }).build();
            server.start();
            ApiHandler.server = server;
        } catch (Exception e) {
            MessagesManager.sendWaringException("Error al iniciar la api de pagina web, Api deshabilitada", e);
        }
    }
}
