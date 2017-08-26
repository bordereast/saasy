package com.bordereast.saasy.cache;

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module;
import com.bordereast.saasy.SaaSyConfig;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.SetArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

public class RedisObjectCache implements ObjectCache {
	private static RedisObjectCache _instance = new RedisObjectCache();

	private RedisClient redisClient = null;
	private StatefulRedisConnection<String, String> connection;
	private RedisCommands<String, String> redis = null;
	private VPackParser parser; 
	private VPack vpack;
	//private Cache<String, Module> cache;
	
	private RedisObjectCache(){
		parser = new VPackParser.Builder().build();
		vpack = new VPack.Builder().registerModule(new VPackJdk8Module()).build();
	}
	
	public static RedisObjectCache getCache(){
		return _instance;
	}


	@Override
	public <T> T getObject(String key, Class<T> c) {
		if(redis == null) return null;

		String result = redis.get(c.getName() + ":" + key);
		
		if(result != null){
			return stringToObject(result, c);
		}	
		
		return null;
	}

	@Override
	public <T> T setObject(String key, T value) {
		if(redis == null) return null;
		redis.set(value.getClass().getName() + ":" + key, objectToString(value), SetArgs.Builder.ex(SaaSyConfig.getInstance().getCacheTTL()));

		return value;
	}
	

	
	private <T> T stringToObject(String json, Class<T> c){
		VPackParser parser1 = new VPackParser.Builder().build();
		VPack vpack1 = new VPack.Builder().registerModule(new VPackJdk8Module()).build();
		VPackSlice slice = (VPackSlice) parser1.fromJson(json); 
		
		T object = null;
		try{
			object = vpack1.deserialize(slice, c);
		} catch(Exception e){
			// funny, if we just try again, it usually works the second time.
			System.out.println(json);
			e.printStackTrace();
			object = vpack1.deserialize(slice, c);
		}
		return object;
	}
	
	private <T> String objectToString(T entity){
		VPackSlice slice = vpack.serialize(entity);
		String json = parser.toJson(slice);
		return json;
	}
	
	public void setRedis(RedisClient redis) {
		this.redisClient = redis;
		connection = redisClient.connect();
		this.redis = connection.sync();
	}
	
}
