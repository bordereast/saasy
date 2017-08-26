package com.bordereast.saasy.routes.response;

import java.util.Map;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class RedirectResponse implements RouteResponse {

    private String redirectUrl;
    private String redirectFromUrl = null;
    private boolean useReroute = false;
    private Map<String, String> headers;
    
    public RedirectResponse(){}
    public RedirectResponse(String redirectUrl){
        this.redirectUrl = redirectUrl;
    }
    public RedirectResponse(String redirectUrl, String redirectFromUrl){
        this.redirectUrl = redirectUrl;
        this.redirectFromUrl = redirectFromUrl;
    }
    public RedirectResponse(String redirectUrl, String redirectFromUrl, boolean useReroute){
        this.redirectUrl = redirectUrl;
        this.redirectFromUrl = redirectFromUrl;
        this.useReroute = useReroute;
    }
    
    @Override
    public void endResponse(final RoutingContext context) {
        if(redirectFromUrl != null) {
            context.put("redirectFromUrl", redirectFromUrl);
        }
        
        if(useReroute) {
            context.reroute(redirectUrl);
            return;
        }
        
        context.response().setStatusCode(302);
        
        if(redirectFromUrl != null) {
            context.response().headers().add("Location", getRedirectUrl() + "?redirect=true&location=" + redirectFromUrl);
        } else {
            context.response().headers().add("Location", getRedirectUrl());
        }
        context.response().end();
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    @Override
    public Map<String, Object> getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    
}
