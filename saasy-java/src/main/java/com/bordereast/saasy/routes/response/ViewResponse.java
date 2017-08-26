package com.bordereast.saasy.routes.response;

import java.util.Map;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class ViewResponse implements RouteResponse {

    private String templateContent;
    private Map<String, String> headers;
    private Map<String, Object> context;
    
    public ViewResponse(){}
    public ViewResponse(Map<String, Object> context){
        this.context = context;
    }
    
    @Override
    public void endResponse(final RoutingContext context) {
        context.response().headers().add("Content-Type", "text/html");
        context.response().end(templateContent);
    }
    
    public String getTemplateContent() {
        return templateContent;
    }
    
    public void setTemplateContent(String content) {
        this.templateContent = content;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }


}
