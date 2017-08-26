package com.bordereast.saasy.routes;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.bordereast.saasy.Constants;
import com.bordereast.saasy.SaaSyLogger;
import com.bordereast.saasy.cache.ObjectCacheFactory;
import com.bordereast.saasy.content.ContentLocator;
import com.bordereast.saasy.routes.response.RouteResponse;

import io.netty.handler.codec.http.HttpMethod;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.ext.web.RoutingContext;

public class ContentHandler {

	private ContentHandler() {}

	public void accept(RoutingContext context, Vertx vertx, SaaSyLogger logger, WorkerExecutor executor){
	    
/*	    if(!(boolean)context.get("authorized")) {
	        context.put("redirect", context.request().path())
	        .reroute("/en/app/core/authorize/login/");
	    }*/
	    
		String paramLang = context.request().getParam("lang");
		String paramModule = context.request().getParam("module");
		String paramComponent = context.request().getParam("component");
		String paramAction = context.request().getParam("action");
		String paramData = context.request().getParam("data");
		Map<String, List<String>> params = null;
		if(context.request().method().name().equals(HttpMethod.POST.name())){
		    String body = context.getBodyAsString("UTF-8");
		    try {
                params = RouteUtil.splitQuery(body);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		
		
		RouteDTO dto = new RouteDTO("saasy", paramLang, paramModule, paramComponent, 
		        paramAction, paramData, context.get(Constants.REQUEST_ID), context.request().method(), params, (boolean)context.get("authorized"), null);
		
		Future<RouteResponse> returnFuture = Future.future();
		
		returnFuture.setHandler(res -> {
			logger.debug(ContentHandler.class,"{0} - {1} - {2} - {3} - {4} - {5}", dto.getRequestId(), paramLang, paramModule, paramComponent, paramAction, paramData);

			RouteResponse response = (RouteResponse) res.result();
			

		    if(response.getHeaders() != null) {
	            for(Map.Entry<String, String> entry : response.getHeaders().entrySet()){
	                if(entry != null)
	                    context.response().putHeader(entry.getKey(), entry.getValue());
	            }
		    }

			
			response.endResponse(context);
			
			logger.info(getClass(), "finished response {0}", context.get(Constants.REQUEST_ID).toString());
		});
		
		ContentLocator
			.create(logger, ObjectCacheFactory.getCache())
			.Locate(dto, returnFuture);

	}

	public static ContentHandler create() {
		return new ContentHandler();
	}
}
