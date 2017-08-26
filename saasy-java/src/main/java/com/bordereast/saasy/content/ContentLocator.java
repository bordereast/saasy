package com.bordereast.saasy.content;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.bordereast.saasy.SaaSyConfig;
import com.bordereast.saasy.SaaSyLogger;
import com.bordereast.saasy.cache.ClassCache;
import com.bordereast.saasy.cache.ObjectCache;
import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.dal.DataAccessImpl;
import com.bordereast.saasy.domain.menu.Menu;
import com.bordereast.saasy.domain.module.Action;
import com.bordereast.saasy.domain.module.Component;
import com.bordereast.saasy.domain.module.Module;
import com.bordereast.saasy.exception.DataException;
import com.bordereast.saasy.modules.ModuleDTO;
import com.bordereast.saasy.modules.ModuleDelegate;
import com.bordereast.saasy.routes.RouteDTO;
import com.bordereast.saasy.routes.response.RedirectResponse;
import com.bordereast.saasy.routes.response.RouteResponse;
import com.bordereast.saasy.routes.response.ViewResponse;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.nimbusds.jose.util.Base64;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;


public class ContentLocator {
	private SaaSyLogger logger;
	private ObjectCache cache;

	
	public ContentLocator(SaaSyLogger logger, ObjectCache cache) {
		this.logger = logger;
		this.cache = cache;
	}

	public static ContentLocator create(SaaSyLogger logger, ObjectCache cache) {
		return new ContentLocator(logger, cache);
	}

	// return module?
	public void Locate(RouteDTO routeDto, Future<RouteResponse> returnFuture) {
		//ensureModule(routeDto.getTenant(), routeDto.getLang(), routeDto.getModule(), routeDto.getComponent(), routeDto.getAction(), routeDto.getData());


		Future<Module> moduleFuture = Future.future();
		Future<TemplateDTO> templateFuture = Future.future();
		Future<Menu> menuFuture = Future.future();
		
		getModule(routeDto, moduleFuture);
		
		
		moduleFuture.setHandler(res -> {
		    Module mod = null;
		    
		    if(res.succeeded()){
		        mod = moduleFuture.result();
		    } else {
		        returnFuture.fail("failed to get module");
		        return;
		    }
		    
		    // do auth redirect here;
		    Component comp = mod.getComponents().size() > 0 ? mod.getComponents().get(0) : null; //mod.getComponentByName(routeDto.getComponent());
		    if(comp != null) {
		        Action action = comp.getActions().get(0); //comp.getActionByName(routeDto.getAction());
		        
		        if(action != null && action.isRequiresAuthorization() && !routeDto.isAuthorized()) {
		            String data = "";
		            if(routeDto.getData() != null) data = routeDto.getData();
		            String returnString = String.format("/%s/app/%s/%s/%s/%s", 
                            routeDto.getLang(), routeDto.getModule(), routeDto.getComponent(), routeDto.getAction(), data);
		            
		            returnFuture.complete(new RedirectResponse(
		                    String.format("/%s/app/core/authorize/login/%s",routeDto.getLang(), Base64.encode(returnString).toString())));
		            return;
		        }
		    }
		    
		    final Module module = mod;
		    getTemplate(routeDto, module, templateFuture);
		    getMenu(routeDto, menuFuture);
		    
            CompositeFuture.all(templateFuture, menuFuture).setHandler(ar -> {
                RouteResponse response = null;
                if (ar.succeeded()) {
                    // All futures completed
                  
                    TemplateDTO templateDto = templateFuture.result();
                    Menu menu = menuFuture.result();
                  
                    // now we merge module and template
                    Writer writer = new StringWriter();
    
                    Map<String, Object> context = new HashMap<>();
                    context.put("title", "SaaSy");
                    context.put("moduleContent", templateDto.getContentPath());
                    context.put("moduleData", module);
                    context.put("leftmenu", menu);
                    context.put("locale", routeDto.getLang());
                      
                    // get module specific data
                    try {
                        
                        ModuleDelegate delegate = ClassCache.getModuleDelegate(module.getDelegateName());
                        
                        if(delegate == null){
                            delegate = (ModuleDelegate) Class.forName(module.getDelegateName()).newInstance();
                            delegate.setCache(cache);
                            delegate.setDataAccess(new DataAccessImpl(routeDto.getTenant()));
                            
                            ClassCache.setModuleDelegate(module.getDelegateName(), delegate);
                    
                        } 
                        
                        response = delegate.get(new ModuleDTO(routeDto, module));
                        
                        if(response != null && response.getContext() != null) {
                            context.putAll(response.getContext());
                        }
                        
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } 
                      
                    
                    
                    if(response != null && response instanceof ViewResponse){
                        try {
                            
                            templateDto.getTemplate().evaluate(writer, context);
                            ((ViewResponse)response).setTemplateContent(writer.toString());
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                                  
                    returnFuture.complete(response);
                    
              } else {
                // At least one server failed
                  returnFuture.complete(null);
              }
            });
		});


	}
	
	
	private void getMenu(RouteDTO routeDto, Future<Menu> future) {

		Menu menu = null;
		//if(module == null){
			// now we get our module from database
				DataAccess db = new DataAccessImpl(routeDto.getTenant());
				try {
					menu = db.getByKey("leftmenu", Menu.class);
					
					//cache.setObject(routeDto.getModule(), module);

				} catch (DataException e) {
					e.printStackTrace();
					
				}
			//}
			future.complete(menu);
	}

	private void getModule(RouteDTO routeDto, Future<Module> future){
	    String key = Module.buildCacheKey("Single", routeDto.getModule(), routeDto.getComponent(), routeDto.getAction());
		Module module =  cache.getObject(key, Module.class);

		if(module == null){
		// now we get our module from database
			DataAccess db = new DataAccessImpl(routeDto.getTenant());
			try {
			    Map<String, Object> params = new HashMap<String, Object>();
			    params.put("mod", routeDto.getModule());
			    params.put("comp", routeDto.getComponent());
			    params.put("action", routeDto.getAction());
			    module = db.getSingleByAQL(db.getAql_ModuleSingleByParams(), params, Module.class);
				
				cache.setObject(key, module);

			} catch (DataException e) {
				e.printStackTrace();
				
			}
		}
		future.complete(module);
	}

	private void getTemplate(RouteDTO routeDto, Module module, Future<TemplateDTO> f) {
		try {
			TemplateDTO dto = new TemplateDTO();
			String contentPath = buildModulePath(routeDto);
			String templatePath = "system\\layout";
			for(Component com : module.getComponents()){
			    if(com.getName().equals(routeDto.getComponent())){
			        templatePath = com.getTemplate() != null ? com.getTemplate() : templatePath;
			    }
			}
			
			
			PebbleTemplate compiledTemplate = SaaSyConfig.getInstance().getPebble().getTemplate(contentPath);
			
			dto.setTemplate(compiledTemplate);
			dto.setContentPath(contentPath);
			f.complete(dto);
		} catch (PebbleException e) {
			e.printStackTrace();
			f.fail(e.getMessage());
		}
	}
	
	private String buildModulePath(RouteDTO dto){
		// build path to possibly existing theme version
		String themePath = Paths.get("themes", dto.getTenant(), "modules", dto.getModule(), dto.getComponent(), dto.getAction()).toString() + SaaSyConfig.getInstance().getTemplateExtension();
		// if that file exists, return the relative path to that file
		if(Files.exists(Paths.get(SaaSyConfig.getInstance().getTemplatePath(), themePath) , LinkOption.NOFOLLOW_LINKS)){
			//logger.debug(getClass(), "theme file exists at {0}", Paths.get(SaaSyConfig.getInstance().getTemplatePath(), themePath) );
			return Paths.get("themes", dto.getTenant(), "modules", dto.getModule(), dto.getComponent(), dto.getAction()).toString();
		}
		// otherwise, just use base file
		//logger.debug(getClass(), "no theme file exists at {0}", Paths.get(SaaSyConfig.getInstance().getTemplatePath(), themePath));
		return Paths.get("modules", dto.getModule(), dto.getComponent(), dto.getAction()).toString();
	}
	
	@SuppressWarnings("unused")
    private void ensureModule(String tenant, String lang, String module, String component, String action, String data){
		DataAccess db = new DataAccessImpl(tenant);


		Module m = new Module();
		Component c = new Component();
		Action a = new Action();
		
		a.setName(action);
		a.setDescription("description of " + action);
		a.setTitle(action.toUpperCase());
		
		c.setName(component);
		c.setDescription("description of " + component);
		c.setTitle(component.toUpperCase());
		c.getActions().add(a);
		
		m.setName(module);
		m.setDescription("description of " + module);
		m.setTitle(module.toUpperCase());
		m.getComponents().add(c);
		m.setKey(module);
		
		m.setModifiedBy("system");
		m.setModifiedOn((Instant.now(Clock.systemUTC())));
		m.setModifiedFunc(this.getClass().toString());
		m.setCreatedBy("system");
		m.setCreatedOn((Instant.now(Clock.systemUTC())));
		m.setCreatedFunc(this.getClass().toString());
		m.setDelegateName("com.bordereast.saasy.modules.CoreModuleDelegate");
		
		try {
			db.insert(m, Module.class);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
