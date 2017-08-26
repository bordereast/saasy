package com.bordereast.saasy.domain.authorization;

import java.util.ArrayList;
import java.util.List;

import com.arangodb.velocypack.annotations.Expose;
import com.bordereast.jaql.arango.annotation.ArangoRelation;
import com.bordereast.saasy.domain.BaseEntity;
import com.bordereast.saasy.domain.JoinedEntity;

public class Role extends JoinedEntity {
    private String name;
    private String title;
    
    @ArangoRelation(joinCollection="role_permissions", localField="permissions", targetCollection="permission")
    @Expose(serialize = false, deserialize = true)
    private List<Permission> permissions;
    
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
    
    public List<Permission> getPermissions() {
        if(permissions == null) permissions = new ArrayList<Permission>();
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
    public String getResourceLabel() {
        return this.getName().replace(":", "_");
    }
    
}
