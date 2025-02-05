package net.atcore.webapi;

import io.undertow.server.HttpServerExchange;
import lombok.Getter;

@Getter
public abstract class BaseApi {

    private final String identifier;

    public BaseApi(String identifier) {
        this.identifier = identifier;
        ApiHandler.APIS.add(this);
    }

    public abstract Object onRequest(HttpServerExchange request);

}
