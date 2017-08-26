package com.bordereast.saasy.cache;

public class EmptyCache implements ObjectCache {

	
	@Override
	public <T> T getObject(String key, Class<T> c) {
		return null;
		
	}

	@Override
	public <T> T setObject(String key, T value) {
		return value;
	}

}
