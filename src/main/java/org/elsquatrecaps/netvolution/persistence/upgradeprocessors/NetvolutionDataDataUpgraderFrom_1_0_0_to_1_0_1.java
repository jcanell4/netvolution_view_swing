/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.persistence.upgradeprocessors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.netvolution.persistence.ConfigurationDataFileManager;
import org.elsquatrecaps.netvolution.persistence.NetvolutionDataFileManager.NetvolutionData;
import org.elsquatrecaps.rsjcb.netvolution.events.CompletedEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.InitialEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetwork;
import org.elsquatrecaps.utilities.tools.Pair;


/**
 *
 * @author josep
 */
public class NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1 extends NetvolutionDataDataUpgrader {
    private static final int POSITIONS_SIZE = 5;
    private static final int HEADER_SIZE = Long.BYTES*POSITIONS_SIZE;
    private static final int VERSION_START_POSITION = 0;
    private static final int CONFIGURATION_DATA_START_POSITION = 1;
    private static final int INITIAL_PROCESS_DATA_START_POSITION = 2;
    private static final int CYCLIC_RESULTS_PROCESS_DATA_START_POSITION = 3;
    private static final int FINAL_PROCESS_DATA_START_POSITION = 4;

    public NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1() {
    }

    @Override
    public String forUpgradingTo() {
        return "1.0.1";
    }

    @Override
    public String forUpgradingFrom() {
        return "1.0.1";
    }

    /**
     *
     * @param content
     * @return
     */
    @Override
    public NetvolutionData upgrade(NetvolutionData content) {
        long[] p = content.getPositions();
        content.setPositions(new long[p.length+1]);
        for(int i=0; i<p.length; i++){
            content.getPositions()[i] = p[i];
        } 
        content.getPositions()[p.length]=p[p.length-1];
        content.setVersion(this.forUpgradingTo());
        return content;
    }
    
        
    @Override
    public NetvolutionData getContent(RandomAccessFile rwf){
        NetvolutionData content = new NetvolutionData();
        content.setPositions(getHeader(rwf));
        content.setVersion(getFileVersion(rwf, content.getPositions()));
        content.setJsonNode(getConfigData(rwf, content.getPositions()));
        content.setInitialEvolutionaryProcessEvent(getDataOfInitialEvolutionaryProcess(rwf, content.getPositions()));
        Pair<List<Double>, List<String>> p = getDataOnFinishEvolutionCycle(rwf, content.getPositions());
        content.setSerieTitles(p.getSecond());
        content.setExtraInfo(p.getFirst());
        content.setCompletedEvolutionaryProcessEvent(getDataOnCompletedEvolutionaryProcess(rwf, content.getPositions()));
        return content;
    }
    
    
    private static long[] getHeader(RandomAccessFile rwf){
        long[] lpositions = null;
        try {
            rwf.seek(0);
            lpositions = new long[POSITIONS_SIZE];
            for(int i=0; i<POSITIONS_SIZE; i++){
                lpositions[i] = rwf.readLong();
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lpositions;
    }
    
    private static String getFileVersion(RandomAccessFile rwf, long[] positions){
        String ret = null;
        try {
            rwf.seek(positions[VERSION_START_POSITION]);
            ret = rwf.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    private static JsonNode getConfigData(RandomAccessFile rwf, long[] positions) {
        JsonNode configJson = null;
        try {
            String strConfig;
            rwf.seek(positions[CONFIGURATION_DATA_START_POSITION]);
            strConfig = rwf.readUTF();
            
            ObjectMapper objectMapper = new ObjectMapper();
            configJson = objectMapper.readTree(strConfig);
            if(ConfigurationDataFileManager.needUpgrade(configJson)){
                configJson = ConfigurationDataFileManager.upgradeOldVersionToCurrent(configJson);
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return configJson;
    }
    
    public static InitialEvolutionaryProcessEvent getDataOfInitialEvolutionaryProcess(RandomAccessFile rwf, long[] positions) {
        InitialEvolutionaryProcessEvent eev=null;
        try {
            int len = (int) (positions[INITIAL_PROCESS_DATA_START_POSITION+1]-positions[INITIAL_PROCESS_DATA_START_POSITION]);
            byte[] buf = new byte[len];
            rwf.seek(positions[INITIAL_PROCESS_DATA_START_POSITION]);
            rwf.readFully(buf);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object oread = ois.readObject();
            eev = (InitialEvolutionaryProcessEvent) ois.readObject();
            for (PtpNeuralNetwork net : eev.getInitialPopulation()) {
                net.updateInputNeurons();
                net.updateOutputNeurons();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eev;
    }
    
    private static Pair<List<Double>, List<String>> getDataOnFinishEvolutionCycle(RandomAccessFile rwf, long[] positions) {
        List<Double> extraInfo = new ArrayList<>();
        List<String> serieTitles = new ArrayList<>();
        try {
            rwf.seek(positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION]);
            short nSeries = rwf.readShort();
            for(int i=0; i<nSeries; i++){
                String t = rwf.readUTF();
                serieTitles.add(t);
            }
            while(rwf.getFilePointer()<positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+1]){
                extraInfo.add(rwf.readDouble());
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Pair<>(extraInfo, serieTitles);
    }
    
    private static CompletedEvolutionaryProcessEvent getDataOnCompletedEvolutionaryProcess(RandomAccessFile rwf, long[] positions) {
        CompletedEvolutionaryProcessEvent eev = null;
        try {
            int len = (int) (rwf.length()-positions[FINAL_PROCESS_DATA_START_POSITION]);
            byte[] buf = new byte[len];
            rwf.seek(positions[FINAL_PROCESS_DATA_START_POSITION]);
            rwf.readFully(buf);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bis);
            eev = (CompletedEvolutionaryProcessEvent) ois.readObject();
            for (PtpNeuralNetwork net : eev.getFinalPopulation()) {
                net.updateInputNeurons();
                net.updateOutputNeurons();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eev;
    }
}
