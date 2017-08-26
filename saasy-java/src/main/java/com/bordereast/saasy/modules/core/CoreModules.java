package com.bordereast.saasy.modules.core;

import java.util.HashMap;
import java.util.Map;

import com.bordereast.saasy.cache.ObjectCache;
import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.domain.PagedList;
import com.bordereast.saasy.domain.module.Module;
import com.bordereast.saasy.exception.DataException;
import com.bordereast.saasy.modules.ModuleDTO;
import com.bordereast.saasy.routes.response.RouteResponse;
import com.bordereast.saasy.routes.response.ViewResponse;

public class CoreModules {
    private DataAccess db;
    private ObjectCache cache;
    
    public CoreModules(DataAccess db, ObjectCache cache) {
        this.db = db;
        this.cache = cache;
    }
    
    public RouteResponse getModule(ModuleDTO dto) {
        switch(dto.getRouteDto().getAction()){
        case "view":
            return getModuleView(dto);
        case "edit":
            return getModuleEdit(dto);
        }
        
        return null;
    }

    private RouteResponse getModuleEdit(ModuleDTO dto) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("title", dto.getModule().getComponents().get(0).getActions().get(0).getTitle()); 
        
        try {
            // TODO: validate and escape input
            Module module = db.getByKey(dto.getRouteDto().getData().trim(), Module.class);
            map.put("module", module);
        } catch (DataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        //ensureMenu();
        return new ViewResponse(map);
    }

    private RouteResponse getModuleView(ModuleDTO dto) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("title", dto.getModule().getComponents().get(0).getActions().get(0).getTitle()); 
        
        try {
            PagedList<Module> list = db.getPagedCollection("module", null, Module.class);
            map.put("moduleList", list.getResults());
        } catch (DataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        //ensureMenu();
        return new ViewResponse(map);
    }
    
}
