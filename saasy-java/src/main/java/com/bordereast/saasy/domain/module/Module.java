package com.bordereast.saasy.domain.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bordereast.saasy.cache.CustomCacheKey;
import com.bordereast.saasy.domain.BaseEntity;

public class Module extends BaseEntity implements Serializable, CustomCacheKey {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String title;
	private String description;
	private List<Component> components;
	private String type;
	private HashMap<String, String> config;
	private String delegateName;
	
	public Module(){super();}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Component> getComponents() {
		if(components == null){
			components = new ArrayList<Component>();
		}
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, String>  getConfig() {
		if(config == null){
			config = new HashMap<>();
		}
		return config;
	}

	public void setConfig(HashMap<String, String> config) {
		this.config = config;
	}

	public String getDelegateName() {
		return delegateName;
	}

	public void setDelegateName(String delegateName) {
		this.delegateName = delegateName;
	}
	
	public Component getComponentByName(String componentName) {
	    if(this.components == null) return null;
	    return components.stream().filter(x -> x.getName().equals(componentName)).findFirst().orElse(null);
	}

    @Override
    public String getCacheKey() {
        String type = getComponents().size() <= 1 ? "Single" : "Multiple";
        return buildCacheKey( 
                type, 
                getName(), 
                getComponents().get(0).getName(),
                getComponents().get(0).getActions().get(0).getName());
    }
	
    public static String buildCacheKey(String type, String module, String component, String action) {
        return String.format("%s-%s-%s-%s", type, module, component, action);
    }
	
	
}
