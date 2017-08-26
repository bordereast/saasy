package com.bordereast.saasy.cache;

import java.util.concurrent.ConcurrentHashMap;

import com.bordereast.saasy.modules.ModuleDelegate;

public class ClassCache {
	private static ConcurrentHashMap<String, ModuleDelegate> moduleDelegateHash = new ConcurrentHashMap<String, ModuleDelegate>();
	
	public static ModuleDelegate getModuleDelegate(String delegateName){
		return moduleDelegateHash.getOrDefault(delegateName, null);
	}
	
	public static void setModuleDelegate(String delegateName, ModuleDelegate delegate){
		moduleDelegateHash.put(delegateName,delegate);
	}
}
