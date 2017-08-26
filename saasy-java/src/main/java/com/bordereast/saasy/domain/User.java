package com.bordereast.saasy.domain;

import java.util.ArrayList;
import java.util.List;

import com.arangodb.velocypack.annotations.Expose;
import com.bordereast.jaql.arango.annotation.ArangoRelation;
import com.bordereast.saasy.domain.authorization.Role;

public class User extends JoinedEntity {
    private String email;
    private String passwordHash;
    private String passwordSalt;
    
    @ArangoRelation(localField="roles", targetCollection="role", joinCollection="user_roles")
    @Expose(serialize = false, deserialize = true)
    private List<Role> roles;

/*    public boolean hasRole(String roleName){
        return getRoles().stream()
                .anyMatch(r -> r.getKey().equals(roleName));
    }
    
    public boolean hasPermission(String permissionName){
        return getRoles().stream()
                .anyMatch(r -> r.getPermissions().stream()
                        .anyMatch(p -> p.getKey().equals(permissionName)));
    }*/
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getPasswordSalt() {
        return passwordSalt;
    }
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
    public List<Role> getRoles() {
        if(roles == null) roles = new ArrayList<Role>();
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    
}
