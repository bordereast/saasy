package com.bordereast.saasy.cache;


public interface ObjectCache {

	public <T> T getObject(String key, Class<T> c);
	public <T> T setObject(String key, T value);
	
}
