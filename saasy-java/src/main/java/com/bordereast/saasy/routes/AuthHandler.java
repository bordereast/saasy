package com.bordereast.saasy.routes;

import com.bordereast.saasy.Constants;
import com.bordereast.saasy.SaaSyConfig;
import com.bordereast.saasy.SaaSyLogger;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

public class AuthHandler {

	public AuthHandler() {}
	
	public void accept(final RoutingContext context, final Vertx vertx, SaaSyLogger logger){
	    
	    vertx.executeBlocking(future -> {
/*	           for(Entry<String, String> map : context.request().headers()){
	                logger.debug(getClass(), "header = {0} - {1}", map.getKey(), map.getValue());
	            }*/
	            Cookie authToken = context.getCookie("access_token");
	            
	            if(authToken == null){
	                logger.debug(getClass(), "not authorized!");
	                context.put("authorized", false);
	            } else {
	                
	                JWSObject jws;
	                try {
	                    jws = JWSObject.parse(authToken.getValue());
	                    JWSVerifier verifier = new MACVerifier(SaaSyConfig.getInstance().getJwtSharedSecret());
	                    if(jws.verify(verifier)){
	                        JsonObject user = new JsonObject(jws.getPayload().toString());
	                        context.put("authorized", true);
	                        
	                        context.put("user", user);

	                        logger.debug(getClass(), "{0} Authorized user = {1}", context.get(Constants.REQUEST_ID),user);
	                    }
	                } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	                
	                
	            }
	            
	        future.complete();
	    }, result -> {
	        context.next();
	    });
	    
	}
	
	public static AuthHandler create(){
		return new AuthHandler();
	}
	
	public static String getJWT() {
	    
	    return null;
	}
}
