package com.bordereast.saasy.cache;

import com.bordereast.saasy.SaaSyConfig;

public class ObjectCacheFactory {
	
	public static ObjectCache getCache(){
		if(SaaSyConfig.getInstance().isUnitTest()){
			return new EmptyCache();
		} else {
			return RedisObjectCache.getCache();
		}
	}
	
}
