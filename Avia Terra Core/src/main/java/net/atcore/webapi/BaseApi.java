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


    public abstract Object onRequest(HttpServerExchange request);

}
