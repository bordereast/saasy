package com.bordereast.saasy.domain.module;

import java.io.Serializable;
import java.util.HashMap;

public class Action implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String title;
	private String description;
	private HashMap<String, String> data;
	private boolean requiresAuthorization;
	private String PermissionKey;
	
	public Action(){super();}

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

	public HashMap<String, String> getData() {
		if(data == null){
			data = new HashMap<>();
		}
		return data;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

    public boolean isRequiresAuthorization() {
        return requiresAuthorization;
    }

    public void setRequiresAuthorization(boolean requiresAuthorization) {
        this.requiresAuthorization = requiresAuthorization;
    }

    public String getPermissionKey() {
        return PermissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        PermissionKey = permissionKey;
    }


}
