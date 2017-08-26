package com.bordereast.saasy.domain.authorization;

import com.bordereast.saasy.domain.BaseEntity;

public class Permission extends BaseEntity {
    private String name;
    private String title;
    
    public Permission() {}
    public Permission(String name) {
        this.name = name;
    }
    
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

    public String getResourceLabel() {
        return this.getName().replace(":", "_");
    }
    
}
