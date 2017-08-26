package com.bordereast.saasy.modules;

import com.bordereast.saasy.domain.module.Module;
import com.bordereast.saasy.routes.RouteDTO;

public class ModuleDTO {
	private RouteDTO routeDto;
	private Module module;
	
	public ModuleDTO(){}
	
	public ModuleDTO(RouteDTO routeDto, Module module){
		this.module = module;
		this.routeDto = routeDto;
	}
	
	public RouteDTO getRouteDto() {
		return routeDto;
	}
	public void setRouteDto(RouteDTO routeDto) {
		this.routeDto = routeDto;
	}
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
}
