/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.elsquatrecaps.netvolution.persistence.upgradeprocessors.ConfigurationDataUpgrader;

/**
 *
 * @author josep
 */
public class ConfigurationDataFileManager {
    public static String currentVersion="0.0.0.3";
    
    public static JsonNode getContent(String filename) throws IOException{
        File file = new File(filename);
        return getContent(file);
    }
    
    public static JsonNode getContent(File file) throws IOException{
        JsonNode ret;
        ObjectMapper objectMapper = new ObjectMapper();
        ret = objectMapper.readTree(file);
        if(needUpgrade(ret)){
            ret = upgradeOldVersionToCurrent(ret);
            _saveContent(ret, file);
        }
        return ret;
    }
    
    public static void saveContent(JsonNode configJson, File file) throws IOException{
        if(needUpgrade(configJson)){
            configJson = upgradeOldVersionToCurrent(configJson);
        }
        _saveContent(configJson, file);
         
    }
    
    private static void _saveContent(JsonNode configJson, File file) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, configJson);
    }
    
    public static String getVersion(JsonNode content){
        String ret; 
        if(content.has("metadata")){
            ret = content.get("metadata").get("version").asText();
        }else{
            ret = "0.0.0.0";
        }
        return ret;
    }
    
    public static boolean needUpgrade(JsonNode content){
        int cmp = compareVersions(getVersion(content), currentVersion);
        if (cmp>0){
            //EXCEPTION
        }
        return cmp<0;
    }
    
    public static JsonNode upgradeOldVersionToCurrent(JsonNode oldVersion){
        return upgradeOldVersionToCurrent(getVersion(oldVersion), oldVersion);
    }
    
    public static JsonNode upgradeOldVersionToCurrent(String versionOfContent, JsonNode oldVersionContent){        
        while(!currentVersion.equals(versionOfContent)){
            ConfigurationDataUpgrader up = ConfigurationDataUpgrader.getInstanceUpgraderForOldVersion(versionOfContent);
            oldVersionContent = up.upgrade(oldVersionContent);
            versionOfContent = up.forUpgradingTo();
        }
        return oldVersionContent;
    }
    
    private static int compareVersions(String v1, String v2){
        int ret=0;
        String[] av1 = v1.split("\\.");
        String[] av2 = v2.split("\\.");
        for(int i=0; ret==0 && i<av1.length; i++){
            ret = Integer.valueOf(av1[i]).compareTo(Integer.valueOf(av2[i]));
        }
        return ret;
    }
    
}
