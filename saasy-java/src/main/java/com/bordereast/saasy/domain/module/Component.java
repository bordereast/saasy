package com.bordereast.saasy.domain.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Component implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String title;
	private String description;
	private List<Action> actions;
    private String template;
	
	public Component(){super();}

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

	public List<Action> getActions() {
		if(actions == null){
			actions = new ArrayList<>();
		}
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
    
    public Action getActionByName(String actionName) {
        if(this.actions == null) return null;
        return actions.stream().filter(x -> x.getName().equals(actionName)).findFirst().orElse(null);
    }
	
}
