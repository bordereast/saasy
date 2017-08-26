package com.bordereast.saasy.routes.response;

import java.util.Map;

import com.bordereast.saasy.SaaSyConfig;
import com.bordereast.saasy.domain.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

public class JwtResponse implements RouteResponse {

    private String redirectUrl;
    private Map<String, String> headers;
    Cookie cookie;
    String token = null;
    
    public JwtResponse(User user, String redirectUrl){
        this.redirectUrl = redirectUrl;
        JsonObject payload = new JsonObject().put("sub", user.getEmail());
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload.encode()));

        try {
            jwsObject.sign(SaaSyConfig.getInstance().getJwtSigner());
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        
        token = jwsObject.serialize();
        
        cookie = Cookie.cookie("access_token",token);
        cookie.setPath("/");
        cookie.setMaxAge(10800);

    }
    
    @Override
    public void endResponse(final RoutingContext context) {
        context.addCookie(cookie);
        
        context.response().headers().add("Authorization", "Bearer " + token);
        context.response().setStatusCode(302);
        context.response().headers().add("Location", getRedirectUrl());
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
