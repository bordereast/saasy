package com.bordereast.saasy;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bordereast.saasy.pebble.SaaSyExtension;
import com.mitchellbosecke.pebble.*;
import com.mitchellbosecke.pebble.lexer.Syntax;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;

public class SaaSyConfig {
	private static SaaSyConfig _instance;
	private boolean unitTest;
	private boolean debug;
	private PebbleEngine pebble = null;
	private FileLoader fileLoader;
	private String templatePath;
	private String templateExtension;
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private Long cacheTTL;
	private byte[] jwtSharedSecret = new byte[32];
	private JWSSigner jwtSigner;
	
    private SaaSyConfig(JsonObject config, Vertx vertx){
		
		unitTest = config.getBoolean(Constants.CONFIG_IS_TESTING, false);
		debug = config.getBoolean(Constants.CONFIG_IS_DEBUG, true);
		fileLoader = new FileLoader();
		fileLoader.setSuffix(".html");
		templateExtension = ".html";
		fileLoader.setCharset(StandardCharsets.UTF_8.name());
		fileLoader.setPrefix(config.getString(Constants.CONFIG_TEMPLATE_ROOT));
		templatePath = config.getString(Constants.CONFIG_TEMPLATE_ROOT);
		cacheTTL = config.getLong(Constants.CONFIG_CACHE_TTL, 120L);
		jwtSharedSecret =  config.getString(Constants.CONFIG_JWT_SECRET, "hgXQqBpW5fZBAN5RW6Gg0OcwMFle8uE8").getBytes(StandardCharsets.UTF_8);
		try {
		    jwtSigner = new MACSigner(jwtSharedSecret);
        } catch (KeyLengthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
    

    public JWSSigner getJwtSigner() {
        return jwtSigner;
    }

    public static SaaSyConfig getInstance(){
		return _instance;
	}
	
	static void setInstance(JsonObject config, Vertx vertx){
		_instance = new SaaSyConfig(config, vertx);
	}

	public boolean isUnitTest() {
		return unitTest;
	}

	public boolean isDebug() {
		return debug;
	}
	
	public PebbleEngine getPebble() {
		if(pebble == null){
			Syntax syntax = new Syntax
					.Builder()
					.setPrintOpenDelimiter("{[")
					.setPrintCloseDelimiter("]}")
					.build();
			
			pebble = new PebbleEngine					
					.Builder()
					.templateCache(null)
					.cacheActive(false)
					.tagCache(null)
					.executorService(executor)
					.loader(fileLoader)
					.syntax(syntax)
					.extension(new SaaSyExtension())
					.build();
			
		}
		
		return pebble;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public String getTemplateExtension() {
		return templateExtension;
	}

	public Long getCacheTTL() {
		return cacheTTL;
	}


    public byte[] getJwtSharedSecret() {
        return jwtSharedSecret;
    }
    

	
}
