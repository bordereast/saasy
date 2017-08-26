package com.bordereast.saasy.cache;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceCache {

	public static ConcurrentHashMap<String, ResourceBundle> Resources = new ConcurrentHashMap<String, ResourceBundle>();
}
