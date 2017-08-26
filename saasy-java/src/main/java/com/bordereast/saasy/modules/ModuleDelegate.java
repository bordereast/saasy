package com.bordereast.saasy.modules;

import com.bordereast.saasy.cache.ObjectCache;
import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.routes.response.RouteResponse;

public interface ModuleDelegate {

	public RouteResponse get(ModuleDTO dto);
	
	public void setDataAccess(DataAccess db);
	
	public void setCache(ObjectCache objectCache);
}
