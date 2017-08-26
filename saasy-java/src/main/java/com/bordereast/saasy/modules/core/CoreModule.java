package com.bordereast.saasy.modules.core;


import java.util.HashMap;
import java.util.Map;

import com.bordereast.saasy.cache.ObjectCache;
import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.domain.menu.Menu;
import com.bordereast.saasy.domain.menu.MenuItem;
import com.bordereast.saasy.modules.ModuleDTO;
import com.bordereast.saasy.modules.ModuleDelegate;
import com.bordereast.saasy.routes.response.ViewResponse;
import com.bordereast.saasy.routes.response.RouteResponse;

public class CoreModule implements ModuleDelegate {

	private DataAccess db;
	private ObjectCache cache;
	
	public CoreModule(){}
	
	@Override
	public RouteResponse get(ModuleDTO dto){
		
		switch(dto.getRouteDto().getComponent()){
		case "dashboard":
			return new CoreDashboard(db, cache).getDashboard(dto);
		case "authorize":
		    return new CoreAuthorization(db, cache).getAuthorize(dto);
		case "modules":
		    return new CoreModules(db, cache).getModule(dto);
	    default:
	        return new CoreDashboard(db, cache).getDashboard(dto);
		}
	}
	


	@Override
	public void setDataAccess(DataAccess db) {
		this.db = db;		
	}

	@Override
	public void setCache(ObjectCache objectCache) {
		this.cache = objectCache;
	}
	
	private void ensureMenu(){
		try{
			
			Menu menu = new Menu();
			menu.setKey("leftmenu");
			menu.setSortOrder(0);
			
			MenuItem i1 = new MenuItem();
			i1.setLocaleFile("menu");
			i1.setLocaleTitle("administration");
			i1.setHref("/{lang}/app/core/admin/view");
			
			MenuItem i2 = new MenuItem();
			i2.setLocaleFile("menu");
			i2.setLocaleTitle("modules");
			i2.setHref("/{lang}/app/core/modules/view");
			
			i1.getChildren().add(i2);
			
			menu.getItems().add(i1);
			
			db.insert(menu, Menu.class);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	
}
