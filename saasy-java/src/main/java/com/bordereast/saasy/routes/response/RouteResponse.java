package com.bordereast.saasy.routes.response;

import java.util.Map;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public interface RouteResponse {
    public void endResponse(final RoutingContext context);
    public Map<String, String> getHeaders();
    public Map<String, Object> getContext();
}
