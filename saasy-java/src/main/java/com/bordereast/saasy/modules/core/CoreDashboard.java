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

public class CoreDashboard {
    private DataAccess db;
    private ObjectCache cache;
    
    public CoreDashboard(DataAccess db, ObjectCache cache) {
        this.db = db;
        this.cache = cache;
    }
    

    public RouteResponse getDashboard(ModuleDTO dto) {
        switch(dto.getRouteDto().getAction()){
        case "view":
            return getDashboardView(dto);
        }
        
        return null;
    }

    private RouteResponse getDashboardView(ModuleDTO dto) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("title", dto.getModule().getComponents().get(0).getActions().get(0).getTitle());    
        

        
        //ensureMenu();
        return new ViewResponse(map);
    }
}
