package com.bordereast.saasy;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.dal.DataAccessImpl;
import com.bordereast.saasy.domain.User;
import com.bordereast.saasy.domain.authorization.Permission;
import com.bordereast.saasy.domain.authorization.Role;
import com.bordereast.saasy.exception.DataException;
import com.bordereast.saasy.file.UTF8Control;

public class Bootstrap {
    @SuppressWarnings("unused")
    public static void init(String tenant){
        DataAccess db = new DataAccessImpl(tenant);
        
        setupPermissions(db);
        setupRoles(db);
        
/*        try {
            User user = db.getByKey("agrothe@gmail.com", User.class);
            List<Role> roles = db.getPagedCollection("role", null, Role.class).getResults();
            //user.setRoles(roles);
            //db.insert(user, User.class);
            
        } catch (DataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
    }
    
    
    private static void setupRoles(DataAccess db){
        try {db.createCollection("role");}catch (Exception e) {}
        ResourceBundle bundle = ResourceBundle.getBundle("roles", Locale.getDefault(), new UTF8Control());
        Enumeration<String> enumeration = bundle.getKeys();
        
        while (enumeration.hasMoreElements())
        {
            String name = (String) enumeration.nextElement();
         
            try {
                Role r1 = new Role();
                r1.setKey(name.trim());
                r1.setTitle(bundle.getString(name));
                initSystemRole(r1);
                db.insert(r1, Role.class);
            } catch (DataException e) {  }
        }   
        
    }
    
    private static void initSystemRole(Role role) {
        switch(role.getKey()) {
        case "saasyadmin":
            role.getPermissions().add(new Permission("module:view"));
            role.getPermissions().add(new Permission("module:edit"));
            role.getPermissions().add(new Permission("module:create"));
            role.getPermissions().add(new Permission("dashboard:all"));
            role.getPermissions().add(new Permission("dashboard:view"));
            break;
        }
    }
    
    private static void setupPermissions(DataAccess db){
        try {db.createCollection("permission");}catch (Exception e) {}
        ResourceBundle bundle = ResourceBundle.getBundle("permissions", Locale.getDefault(), new UTF8Control());
        Enumeration<String> enumeration = bundle.getKeys();
        while (enumeration.hasMoreElements())
        {
            String name = (String) enumeration.nextElement();
         
            try {
                Permission r1 = new Permission();
                r1.setKey(name.trim().replaceAll("_", ":"));
                r1.setTitle(bundle.getString(name));
                db.insert(r1, Permission.class);
            } catch (DataException e) {}
        }   
        
    }
}
