package org.elsquatrecaps.netvolution.shareddata;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.netvolution.constants.CntFiles;
import org.elsquatrecaps.netvolution.persistence.NetvolutionDataFileManager;
import org.elsquatrecaps.netvolution.view.swing.NetvolutionBasicFrame;
import org.elsquatrecaps.rsjcb.netvolution.events.CompletedEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.InitialEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.lineage.ParentLine;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetwork;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.ActivationFunction;
import org.elsquatrecaps.utilities.tools.Pair;

/**
 *
 * @author josep
 */
public class NetvolutionSharedData {
    public Map<String, NetvolutionBasicFrame> frames;
    public String currentFileConfigName;    
    public String defaultFileConfigName = CntFiles.DEFAULT_CONFIG_FILEPATH;    
    public JsonNode jsonConfigData;
    public ActivationFunction activationFunctionInstance;
    public String displayedDataFilename = CntFiles.DISPLAYED_DATA_FILEPATH;
    public InitialEvolutionaryProcessEvent initialEvolutionaryProcessEvent;
    public CompletedEvolutionaryProcessEvent completedEvolutionaryProcessEvent;
    public List<String> serieTitles;
    public Map<String, List<Double>> extraInfo;
    public ParentLine<PtpNeuralNetwork>[] bestLines;
    public ParentLine<PtpNeuralNetwork>[] worstLines;

                
        
        
    private void loadInitEvent(NetvolutionDataFileManager netvolutionDataFileManager){
        this.initialEvolutionaryProcessEvent = netvolutionDataFileManager.fileReadDataOfInitialEvolutionaryProcess();
    }
    
    public void loadFinalEvent(NetvolutionDataFileManager netvolutionDataFileManager){
        this.completedEvolutionaryProcessEvent = netvolutionDataFileManager.fileReadDataOnCompletedEvolutionaryProcess();
    }
    
    private void loadChartData(NetvolutionDataFileManager netvolutionDataFileManager){
        this.extraInfo = new HashMap<>();
        this.serieTitles = new ArrayList<>();
        netvolutionDataFileManager.fileReadDataOnFinishEvolutionCycle(extraInfo, serieTitles);
    }
    
    private void loadJsonConfig(NetvolutionDataFileManager netvolutionDataFileManager){
        this.jsonConfigData = netvolutionDataFileManager.fileReadConfigFromEvolutionaryProcessData();        
    }
    
    private void loadLines(NetvolutionDataFileManager netvolutionDataFileManager){
        Pair<ParentLine<PtpNeuralNetwork>[], ParentLine<PtpNeuralNetwork>[]> allLines;
        if(isRecordingProgenyLines()){
            allLines = netvolutionDataFileManager.fileReadDataOnProgenyLinesEvolutionaryProcess();
            this.bestLines = allLines.getFirst();
            this.worstLines = allLines.getLast();
        }
    }
    
    private NetvolutionDataFileManager getNetvolutionDataFileManagerInstance(String filepath){
        NetvolutionDataFileManager ret;
        if(this.isRecordingProgenyLines()){
            ret = new NetvolutionDataFileManager(filepath, getBetterProgenyLines(), getBadProgenyLines());
        }else{
            ret = new NetvolutionDataFileManager(filepath);
        }
        return ret;
    }
    
    public void loadAllData(File filedata){
        NetvolutionDataFileManager netvolutionDataFileManager = getNetvolutionDataFileManagerInstance(filedata.getAbsolutePath());
        netvolutionDataFileManager.initForReading();
        loadAllData(netvolutionDataFileManager);
        netvolutionDataFileManager.endForReading();
    }
    
    public void loadAllData(NetvolutionDataFileManager netvolutionDataFileManager){
        loadJsonConfig(netvolutionDataFileManager);
        loadInitEvent(netvolutionDataFileManager);
        loadChartData(netvolutionDataFileManager);
        loadLines(netvolutionDataFileManager);
        loadFinalEvent(netvolutionDataFileManager);
    }
    
    public void loadFinalEvent(File filedata){
        NetvolutionDataFileManager netvolutionDataFileManager = getNetvolutionDataFileManagerInstance(filedata.getAbsolutePath());
        netvolutionDataFileManager.initForReading();
        loadFinalEvent(netvolutionDataFileManager);
        netvolutionDataFileManager.endForReading();        
    }
    
    public void loadInitialEvent(File filedata){
        NetvolutionDataFileManager netvolutionDataFileManager = getNetvolutionDataFileManagerInstance(filedata.getAbsolutePath());
        netvolutionDataFileManager.initForReading();
        loadInitEvent(netvolutionDataFileManager);
        netvolutionDataFileManager.endForReading();        
    }
    
    public void loadChartData(File filedata){
        NetvolutionDataFileManager netvolutionDataFileManager = getNetvolutionDataFileManagerInstance(filedata.getAbsolutePath());
        netvolutionDataFileManager.initForReading();
        loadChartData(netvolutionDataFileManager);
        netvolutionDataFileManager.endForReading();        
    }
    
    public void loadLines(File filedata){
        NetvolutionDataFileManager netvolutionDataFileManager = getNetvolutionDataFileManagerInstance(filedata.getAbsolutePath());
        netvolutionDataFileManager.initForReading();
        loadLines(netvolutionDataFileManager);
        netvolutionDataFileManager.endForReading();        
    }
    
    
    /**
     * @return the recordingProgenyLines
     */
    public boolean isRecordingProgenyLines() {
        return this.jsonConfigData.get("evolutionarySystem").get("optionalDataToRecording").get("lineage").get("recording").asBoolean();
    }

    /**
     * @return the betterProgenyLines
     */
    public int getBetterProgenyLines() {
        return this.jsonConfigData.get("evolutionarySystem").get("optionalDataToRecording").get("lineage").get("numberOfBestProgenyLinesToSave").asInt();
    }

    /**
     * @return the badProgenyLines
     */
    public int getBadProgenyLines() {
        return this.jsonConfigData.get("evolutionarySystem").get("optionalDataToRecording").get("lineage").get("numberOfWorstProgenyLinesToSave").asInt();
    }
    
    public Object[] getArrayValuesFromAttributeListOfConfig(String[] at){
        Object[] ret = new Object[at.length];
        for(int i=0; i<at.length; i++){
            ret[i] = getConfigValueFromPath(at[i]);
        }
        return ret;
    }
    
    private Object getConfigValueFromPath(String path){
        JsonNode node = jsonConfigData;
        String[] aPath = path.split("\\.");
        for(String f: aPath){
            node = node.get(f);
        }
        return getValueFromNode(node);
    }
    
    private Object getValueFromNode(JsonNode node){
        Object ret = null;
        if(node.isBoolean()){
            ret =node.asBoolean();
        }else if(node.isDouble()){
            ret = node.asDouble();
        }else if(node.isLong()){
            ret = node.asLong();
        }else if(node.isInt()){
            ret = node.asInt();
        }else if(node.isBigDecimal()){
            ret = node.decimalValue();
        }else if(node.isBigInteger()){
            ret = node.bigIntegerValue();
//        }else if(node.isBinary()){
//            try {
//                ret = node.binaryValue();
//            } catch (IOException ex) {
//                Logger.getLogger(NetvolutionSharedData.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        return ret;
    }

}
