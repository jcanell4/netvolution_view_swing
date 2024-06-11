/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.view.swing;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import javax.swing.JFrame;
import org.elsquatrecaps.netvolution.menus.SystemMenuEvents;
import org.elsquatrecaps.netvolution.persistence.ConfigurationDataFileManager;
import org.elsquatrecaps.netvolution.shareddata.NetvolutionSharedData;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.ActivationFunction;

/**
 *
 * @author josep
 */
public abstract class NetvolutionBasicFrame extends JFrame {
    private NetvolutionSharedData sharedData;
    private final SystemMenuEvents menuEvents;

    public NetvolutionBasicFrame(NetvolutionSharedData sh) {
        this.menuEvents = new SystemMenuEvents(this);
        sharedData = sh;
    }

    protected JsonNode getJsonConfigData() {
        return this.sharedData.jsonConfigData;
    }

    /**
     * @param jsonConfigData the jsonConfigData to set
     */
    public void setJsonConfigData(JsonNode jsonConfigData) {
        if(ConfigurationDataFileManager.needUpgrade(jsonConfigData)){
            jsonConfigData = ConfigurationDataFileManager.upgradeOldVersionToCurrent(jsonConfigData);
//            menuEvents.saveConfigItemEvent(jsonConfigData, getFileConfigName());
        }
        this.sharedData.jsonConfigData = jsonConfigData;
    }
    
    /**
     * @return the fileConfigName
     */
    public String getFileConfigName() {
        return this.sharedData.currentFileConfigName;
    }

    /**
     * @param fileConfigName the fileConfigName to set
     */
    public void setFileConfigName(String fileConfigName) {
        this.sharedData.currentFileConfigName = fileConfigName;
    }

    public String getDefaultFileNameConfig() {
        return sharedData.defaultFileConfigName;
    }

    /**
     * @param frames the frames to set
     */
    public void setFrames(Map<String, NetvolutionBasicFrame> frames) {
        this.sharedData.frames = frames;
    }

    protected Map<String, NetvolutionBasicFrame> getEvolutionFrames() {
        return this.sharedData.frames;
    }
    
    protected NetvolutionBasicFrame getEvolutionFrames(String frameId) {
        return this.sharedData.frames.get(frameId);
    }
    
    protected NetvolutionSharedData getSharedData(){
        return sharedData;
    }
    
    protected ActivationFunction getActivationFunctionInstance(){
        return sharedData.activationFunctionInstance;
    }
    
    protected String getDisplayedDataFilename(){
        return sharedData.displayedDataFilename;
    }    

    /**
     * @return the menuEvents
     */
    protected SystemMenuEvents getMenuEvents() {
        return menuEvents;
    }
    
    protected ActivationFunction getActivationFunction(){
        return this.sharedData.activationFunctionInstance;
    }
    
    protected void setActivationFunction(ActivationFunction af){
        this.sharedData.activationFunctionInstance = af;
    }
    
    public abstract void updateData();
}
