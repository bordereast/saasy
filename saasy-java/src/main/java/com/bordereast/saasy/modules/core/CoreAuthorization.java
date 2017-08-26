package com.bordereast.saasy.modules.core;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.bordereast.saasy.cache.ObjectCache;
import com.bordereast.saasy.dal.DataAccess;
import com.bordereast.saasy.domain.User;
import com.bordereast.saasy.exception.DataException;
import com.bordereast.saasy.modules.ModuleDTO;
import com.bordereast.saasy.routes.response.JwtResponse;
import com.bordereast.saasy.routes.response.RedirectResponse;
import com.bordereast.saasy.routes.response.RouteResponse;
import com.bordereast.saasy.routes.response.ViewResponse;

public class CoreAuthorization {
    
    private DataAccess db;
    private ObjectCache cache;
    
    public CoreAuthorization(DataAccess db, ObjectCache cache) {
        this.db = db;
        this.cache = cache;
    }
    
    public RouteResponse getAuthorize(ModuleDTO dto) {

        switch(dto.getRouteDto().getAction()){
        case "login":
            return getLoginView(dto);
        case "register":
            return getRegisterView(dto);
        default:
            return getLoginView(dto);
        }
    }
    
    private RouteResponse getRegisterView(ModuleDTO dto) {
        switch(dto.getRouteDto().getHttpMethod()){
        case GET:
            return new ViewResponse(new HashMap<String, Object>());
        case POST:
            return handleRegisterPost(dto);
        default:
            return new ViewResponse(new HashMap<String, Object>());
        }
    }
    

    private RouteResponse handleRegisterPost(ModuleDTO dto) {
        List<String> d = new ArrayList<String>();
        d.add(null);
        String password = dto.getRouteDto().getPostVars().getOrDefault("password", d).get(0);
        String confirmpassword = dto.getRouteDto().getPostVars().getOrDefault("confirmpassword", d).get(0);
        String email = dto.getRouteDto().getPostVars().getOrDefault("email", d).get(0);
        
        // TODO: replace Jsoup with just allowing [a-z][A-Z][0-9][special characters] via some security library
        if(password != null && confirmpassword != null && password.equals(confirmpassword)) {
            String safe = Jsoup.clean(password, Whitelist.basic());
            if(safe.equals(password)) {
                // safe == sanitized string
                User user = new User();
                user.setEmail(Jsoup.clean(email, Whitelist.basic()));
                user.setKey(user.getEmail());
                String salt = null;
                try {
                   
                    salt = getSalt();
                    String encrypted = getEncryptedPassword(password, salt.getBytes());
                    user.setPasswordSalt(salt);
                    user.setPasswordHash(encrypted);
                    
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return new RedirectResponse("/en/app/core/authorize/register/");
                }
                
                try {
                    db.insert(user, User.class);
                } catch (DataException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return new RedirectResponse("/en/app/core/authorize/register/");
                }
                
            }
        }
        
        return new RedirectResponse("/en/app/core/dashboard/view/");
    }
    

    
    private String getEncryptedPassword(String password, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return generatedPassword;
    }
    

    private RouteResponse getLoginView(ModuleDTO dto) {
        switch(dto.getRouteDto().getHttpMethod()){
        case GET:
            return new ViewResponse(new HashMap<String, Object>());
        case POST:
            return handleLoginPost(dto);
        default:
            return new ViewResponse(new HashMap<String, Object>());
            
        }
    }
    

    private RouteResponse handleLoginPost(ModuleDTO dto) {
        List<String> d = new ArrayList<String>();
        d.add(null);
        String password = dto.getRouteDto().getPostVars().getOrDefault("password", d).get(0);
        String email = dto.getRouteDto().getPostVars().getOrDefault("email", d).get(0);
        String returnString = String.format("/%s/app/core/dashboard/view/", dto.getRouteDto().getLang());
        
        if(dto.getRouteDto().getData() != null) {
            byte[] asBytes = Base64.getDecoder().decode(dto.getRouteDto().getData());
            try {
                returnString = new String(asBytes, "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
        
     // TODO: replace Jsoup with just allowing [a-z][A-Z][0-9][special characters] via some security library
        if(password != null && email != null) {
            try {
                User user = db.getByKey(email.trim(), User.class);
            
            
                byte [] salt = user.getPasswordSalt().getBytes();
                String encrypted = getEncryptedPassword(password, salt);
                
                if(encrypted.equals(user.getPasswordHash())) {
                    user.setPasswordHash(null);
                    user.setPasswordSalt(null);
                    return new JwtResponse(user, returnString);
                }
            } catch (DataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return new RedirectResponse("/en/app/core/authorize/login/");
    }
    
    //Add salt
    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        return new BigInteger(130, sr).toString(32).substring(0, 16);
    }
}
