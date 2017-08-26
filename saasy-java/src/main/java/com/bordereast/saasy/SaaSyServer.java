package com.bordereast.saasy;


import java.util.concurrent.atomic.AtomicLong;

import com.bordereast.saasy.cache.RedisObjectCache;
import com.bordereast.saasy.cache.ObjectCacheFactory;
import com.bordereast.saasy.routes.AuthHandler;
import com.bordereast.saasy.routes.ContentHandler;
import com.lambdaworks.redis.RedisClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class SaaSyServer extends AbstractVerticle {

	public SaaSyLogger logger = new SaaSyLogger();
	private WorkerExecutor contentExecutor;
	AtomicLong requestId = new AtomicLong();
	@Override
	public void stop() {
		contentExecutor.close();
	}
	
	@Override	
	public void start() {
		contentExecutor = vertx.createSharedWorkerExecutor("content-executor");

		JsonObject config = config();
		
		SaaSyConfig.setInstance(config, vertx);
		
		if(!SaaSyConfig.getInstance().isUnitTest()) {

			RedisClient client = RedisClient.create("redis://localhost"); 
			RedisObjectCache cache = ((RedisObjectCache)ObjectCacheFactory.getCache());
			cache.setRedis(client);
		}
	  
		Router router = Router.router(vertx);
		router.route("/static/*").handler(StaticHandler.create("./www"));
		router.route("/*").handler(CookieHandler.create());
		router.route("/*").handler(BodyHandler.create().setMergeFormAttributes(true));
	  
		router.route("/*").handler(context -> {
			context.put(Constants.REQUEST_ID, "req-" + requestId.incrementAndGet());
			AuthHandler.create().accept(context, vertx, logger);
		});
		
	  
		router.route("/:lang/app/:module/:component/:action/:data*")
			.produces("text/html; charset=utf-8")
			.handler(context -> { ContentHandler.create().accept(context, vertx, logger, contentExecutor);});
		
		router.route("/:lang/app/:module/:component/:action*")
			.produces("text/html; charset=utf-8")
			.handler(context -> { ContentHandler.create().accept(context, vertx, logger, contentExecutor);});
		
		router.route("/:lang/app/:module/:component*")
			.produces("text/html; charset=utf-8")
			.handler(context -> { ContentHandler.create().accept(context, vertx, logger, contentExecutor);});
	  
		
	  
	  /* warm up caches */

	  
		vertx.createHttpServer()
        	.requestHandler(router::accept).listen(8080);
    

  }

}