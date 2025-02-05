package net.atcore.webapi;

import io.undertow.Undertow;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.MessagesManager;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
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
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
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
                            if (!exchange.getRequestPath().equals("/" + api.getIdentifier())) continue;
                            exchange.setStatusCode(200);
                            exchange.getResponseSender().send(gson.toJson(api.onRequest(exchange)));
                            return;
                        }
                        exchange.setStatusCode(404);
                        exchange.getResponseSender().send(gson.toJson(new HashMap<String, String[]>().put("Allow Sections", APIS.stream().map(BaseApi::getIdentifier).toArray(String[]::new))));
                    }).build();
            server.start();
            ApiHandler.server = server;
        } catch (Exception e) {
            MessagesManager.sendWaringException("Error al iniciar la api de pagina web", e);
        }
    }
}
