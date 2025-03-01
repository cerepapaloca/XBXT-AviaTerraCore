package net.atcore.webapi;

import io.undertow.server.HttpServerExchange;
import lombok.Getter;

@Getter
public abstract class BaseApi {

    private final String identifier;
    public final ContentType contentType;
    public boolean toJson = true;


    public BaseApi(String identifier, ContentType contentType) {
        this.identifier = identifier;
        this.contentType = contentType;
        ApiHandler.APIS.add(this);
    }

    /**
     * Se llama cuando tiene una petición
     * @param request la solicitud
     * @return la repuesta de la solicitud
     */

    public abstract Object onRequest(HttpServerExchange request);

}
