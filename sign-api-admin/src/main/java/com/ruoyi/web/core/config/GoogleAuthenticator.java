package com.ruoyi.web.core.config;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthenticator {
    private final com.warrenstrange.googleauth.GoogleAuthenticator googleAuthenticator = new com.warrenstrange.googleauth.GoogleAuthenticator();
 
    public String generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    public boolean verifyCode(String secretKey, int verificationCode) {
        return googleAuthenticator.authorize(secretKey, verificationCode);
    }

    @Value("${spring.profiles.active}")
    private String envValue;

    private Map<String,String> map;

    public Map<String,String> getMap(){
        String env = System.getProperty("BOOT_ENV");
        env = env == null ? envValue : env;
        if ("dev".equals(env) || "test".equals(env) || "test01".equals(env)) {
            if(map == null){
                map = new HashMap<>();
                map.put("X4PO6ONOMK33NQCWU57GQUZ7U222FXQI","X4PO6ONOMK33NQCWU57GQUZ7U222FXQI");
            }
        } else if ("prod".equals(env)) {
            if(map == null){
                map = new HashMap<>();
                map.put("5GJGBXDJ3ID5TI6A","5GJGBXDJ3ID5TI6A");
                map.put("UM2LVXUOTJPEUWAQ","UM2LVXUOTJPEUWAQ");
                map.put("H56BYFH6L5DKWBSYLXHKCNPGYDYMHPY3","H56BYFH6L5DKWBSYLXHKCNPGYDYMHPY3");
                map.put("7LKED2VSVYST7KP467EMVFIWITSJBFIW","7LKED2VSVYST7KP467EMVFIWITSJBFIW");
            }
        }
        return map;
    }

    public boolean verifyCode(int verificationCode) {
        for (String key : getMap().keySet()){
            Boolean reut = googleAuthenticator.authorize(key, verificationCode);
            if(reut){
                return true;
            }
        }
        return false;
    }
}