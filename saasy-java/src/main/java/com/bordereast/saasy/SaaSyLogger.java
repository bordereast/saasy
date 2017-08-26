package com.bordereast.saasy;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SaaSyLogger {
	final Logger logger = LoggerFactory.getLogger(SaaSyLogger.class);
	
	public SaaSyLogger(){}
	
	public void info(Class<?> cls, String message){
		logger.info("{0}: {1}", cls.getName(), message);
	}
	
	public void info(Class<?> cls, String message, Object...objects){
		logger.info(cls.getName() + ": " + message, objects);
	}
	
	
	public void debug(Class<?> cls, String message){
		if(SaaSyConfig.getInstance().isDebug()) logger.info("{0}: {1}", cls.getName(), message);
	}
	
	public void debug(Class<?> cls, String message, Object...objects){
		if(SaaSyConfig.getInstance().isDebug()) logger.info(cls.getName() + ": " + message, objects);
	}
	
	public void warn(Class<?> cls, String message){
		logger.warn("{0}: {1}", cls.getName(), message);
	}
	
	public void warn(Class<?> cls, String message, Object...objects){
		logger.warn(cls.getName() + ": " + message, objects);
	}
	
	public void fatal(Class<?> cls, String message){
		logger.fatal(cls.getName() + ": " + message);
	}
	
}
