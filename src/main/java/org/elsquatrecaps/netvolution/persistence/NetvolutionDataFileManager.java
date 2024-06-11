package org.elsquatrecaps.netvolution.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.netvolution.persistence.upgradeprocessors.NetvolutionDataDataUpgrader;
import org.elsquatrecaps.netvolution.view.swing.EvolutionProcessFrame;
import org.elsquatrecaps.rsjcb.netvolution.events.CompletedEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.FinishedEvolutionaryCycleEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.InitialEvolutionaryProcessEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.ProgenyLinesEvent;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.lineage.ParentLine;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetwork;
import org.elsquatrecaps.utilities.tools.Pair;

/**
 *
 * @author josep
 */
public class NetvolutionDataFileManager {
    private static final String CURRENT_VERSION = "1.0.1";
    private static final int POSITIONS_SIZE = 6;
    private static final int HEADER_SIZE = Long.BYTES*POSITIONS_SIZE;
    private static final int VERSION_START_POSITION = 0;
    private static final int CONFIGURATION_DATA_START_POSITION = 1;
    private static final int INITIAL_PROCESS_DATA_START_POSITION = 2;
    private static final int CYCLIC_RESULTS_PROCESS_DATA_START_POSITION = 3;
    private static final int PROGENY_LINES_START_POSITION = 4;
    private static final int FINAL_PROCESS_DATA_START_POSITION = 5;
    
    private final String filePath;
    private RandomAccessFile rwf;
    private boolean fileOpen = false;
    private final List<String> propertiesToFollow=new ArrayList<>();
    private long[] positions;
    private int maxBetterProgenyLines;
    private int maxBadProgenyLines;

//    public static NetvolutionData getContent(RandomAccessFile rwf) throws IOException{
//        NetvolutionData content = new NetvolutionData();
//        content.setPositions(getHeader(rwf));
//        content.setVersion(getFileVersion(rwf, content.positions));
//        content.setJsonNode(getConfigData(rwf, content.positions));
//        content.setInitialEvolutionaryProcessEvent(getDataOfInitialEvolutionaryProcess(rwf, content.positions));
//        Pair<Map<String, List<Double>>, List<String>> p = getDataOnFinishEvolutionCycle(rwf, content.positions);
//        content.setSerieTitles(p.getSecond());
//        content.setExtraInfo(p.getFirst());
//        if(content.positions)
//        content.setParentLines(getDataOnProgenyLinesEvolutionaryProcess(rwf, content.positions));
//        return content;
//    }
//    
//    public static NetvolutionData getContent(File file) throws IOException{
//        NetvolutionData content;
//        try(RandomAccessFile rwf = new RandomAccessFile(file, "rw")){
//            content = getContent(rwf);
//        }
//        return content;
//    }


//    public static void saveContent(NetvolutionData content, File file) throws IOException{
//        if(needUpgrade(content)){
//            content = upgradeOldVersionToCurrent(content);
//        }
//        _saveContent(content, file);
//         
//    }
//    
//    private static void _saveContent(NetvolutionData content, File file) throws IOException{
//       
//    }
//    
//    private static String getVersion(NetvolutionData content){
//        String ret; 
//        ret = content.getVersion();
//        return ret;
//    }
//    
//    private static boolean needUpgrade(NetvolutionData content){
//        int cmp = compareVersions(getVersion(content), CURRENT_VERSION);
//        if (cmp>0){
//            //EXCEPTION
//        }
//        return cmp<0;
//    }
//    
//    private static NetvolutionData upgradeOldVersionToCurrent(NetvolutionData oldVersion){
//        return upgradeOldVersionToCurrent(getVersion(oldVersion), oldVersion);
//    }
//    
//    private static NetvolutionData upgradeOldVersionToCurrent(String versionOfContent, NetvolutionData oldVersionContent){        
//        while(!CURRENT_VERSION.equals(versionOfContent)){
//            NetvolutionDataDataUpgrader up = NetvolutionDataDataUpgrader.getInstanceUpgraderForOldVersion(versionOfContent);
//            oldVersionContent = up.upgrade(oldVersionContent);
//            versionOfContent = up.forUpgradingTo();
//        }
//        return oldVersionContent;
//    }
//    
//    private static int compareVersions(String v1, String v2){
//        int ret=0;
//        String[] av1 = v1.split("\\.");
//        String[] av2 = v2.split("\\.");
//        for(int i=0; ret==0 && i<av1.length; i++){
//            ret = Integer.valueOf(av1[i]).compareTo(Integer.valueOf(av2[i]));
//        }
//        return ret;
//    }
    

    
    public NetvolutionDataFileManager(String filePath) {
        this(filePath, 0, 0);
    }

    public NetvolutionDataFileManager(String filePath, int betterLines, int badLines) {
        this.filePath = filePath;
        this.maxBetterProgenyLines=betterLines;
        this.maxBadProgenyLines=badLines;
    }

    public void initForReading(){
        try {
            if (!this.isFileOpen()){
                connect(filePath);
            }
            readHeader();
            String version = getFileVersion();
            if(!CURRENT_VERSION.equals(version)){
                NetvolutionDataDataUpgrader upgrader = NetvolutionDataDataUpgrader.getInstanceUpgraderForOldVersion(version);
                NetvolutionData content = upgrader.upgrade(upgrader.getContent(rwf));
                writeContent(content);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initForWriting(JsonNode jsonConfigData){
        List<String> mPropertiesToFollow=new ArrayList<>();
        Iterator<String> iterator = jsonConfigData.get("evolutionarySystem").get("propertiesToFollow").fieldNames();
        iterator.forEachRemaining((fn) -> {
            if(jsonConfigData.get("evolutionarySystem").get("propertiesToFollow").get(fn).asBoolean()){
                mPropertiesToFollow.add(fn);
            }
        });
        initForWriting(jsonConfigData, mPropertiesToFollow);        
    }
    
//    public void initForWriting(JsonNode jsonConfigData, String... morePropertiesToFollow){
//        initForWriting(jsonConfigData, Arrays.asList(morePropertiesToFollow));
//    }
//    
//    public void initForWriting(JsonNode jsonConfigData, List<String> pPropertiesToFollow, String... morePropertiesToFollow){
//        List<String> mPropertiesToFollow = new ArrayList<>(Arrays.asList(morePropertiesToFollow));
//        mPropertiesToFollow.addAll(pPropertiesToFollow);
//        initForWriting(jsonConfigData, mPropertiesToFollow);
//    }
    
    public void initForWriting(JsonNode jsonConfigData, List<String> propertiesToFollow){
        try {
            if (!this.isFileOpen()){
                connect(filePath);
            }
            this.rwf.setLength(0);
            positions = new long[POSITIONS_SIZE];
            positions[0] = HEADER_SIZE;
            for(int i=1; i< POSITIONS_SIZE; i++){
                positions[i]=-1; 
            }
            writeCurrentVersion();
            writeConfigJson(jsonConfigData);
            writeHeader();
            this.propertiesToFollow.addAll(propertiesToFollow);
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void endForWriting(){
            writeHeader();
            close();
    }
    
    public void endForReading(){
            close();
    }
    
    private void connect(String filePath) throws FileNotFoundException{
        File f = new File(filePath);
        connect(f);
    }
    
    private void connect(File f) throws FileNotFoundException{
        this.rwf = new RandomAccessFile(f, "rw");
        fileOpen = true;
    }
    
    private void close(){
        try {
            rwf.close();
            fileOpen=false;
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeHeader(){
        writeHeader(positions);
    }
    
    private void writeHeader(long[] positions){
        try {
            rwf.seek(0);
            for(int i=0; i<POSITIONS_SIZE; i++){
                rwf.writeLong(positions[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readHeader(){
        try {
            rwf.seek(0);
            positions = new long[POSITIONS_SIZE];
            for(int i=0; i<POSITIONS_SIZE; i++){
                positions[i] = rwf.readLong();
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getFileVersion(){
        String ret = null;
        try {
            rwf.seek(positions[VERSION_START_POSITION]);
            ret = rwf.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private void writeVersion(String version){
        try {
            this.rwf.seek(positions[VERSION_START_POSITION]);
            this.rwf.writeUTF(version);
            this.positions[VERSION_START_POSITION+1]= this.rwf.getFilePointer();
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeCurrentVersion(){
        writeVersion(CURRENT_VERSION);
    }
    
    private void writeConfigJson(JsonNode configJson){
        try(ByteArrayOutputStream outputStrem= new ByteArrayOutputStream()){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(outputStrem, configJson);
            this.rwf.seek(positions[CONFIGURATION_DATA_START_POSITION]);
            this.rwf.writeUTF(new String(outputStrem.toByteArray()));
            this.positions[CONFIGURATION_DATA_START_POSITION+1]= this.rwf.getFilePointer();            
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JsonNode fileReadConfigFromEvolutionaryProcessData() {
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
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return configJson;
    }
    
    public InitialEvolutionaryProcessEvent fileReadDataOfInitialEvolutionaryProcess() {
        InitialEvolutionaryProcessEvent eev=null;
        try {
            int len = (int) (positions[INITIAL_PROCESS_DATA_START_POSITION+1]-positions[INITIAL_PROCESS_DATA_START_POSITION]);
            byte[] buf = new byte[len];
            rwf.seek(positions[INITIAL_PROCESS_DATA_START_POSITION]);
            rwf.readFully(buf);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bis);
            eev = (InitialEvolutionaryProcessEvent) ois.readObject();
            for (PtpNeuralNetwork net : eev.getInitialPopulation()) {
                net.updateInputNeurons();
                net.updateOutputNeurons();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eev;
    }
    
    private void writeDataOnInitialEvolutionaryProcess(InitialEvolutionaryProcessEvent eev) {
        writeDataOnInitialEvolutionaryProcess(eev, true);
    }
    
    private void writeDataOnInitialEvolutionaryProcess(InitialEvolutionaryProcessEvent eev, boolean writeHeader) {
        try {
            this.rwf.seek(positions[INITIAL_PROCESS_DATA_START_POSITION]);            
            ObjectOutputStream oos = null;
            try {
                for (PtpNeuralNetwork initialPopulation : eev.getInitialPopulation()) {
                    initialPopulation.updateInputNeuronsLength();
                    initialPopulation.updateOutputNeuronsLength();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(eev);
                oos.flush();
                byte[] ao = baos.toByteArray();
                ByteBuffer bodyByteBuffer = ByteBuffer.allocate(ao.length);
                bodyByteBuffer.put(ao);
                bodyByteBuffer.flip();
                rwf.write(bodyByteBuffer.array());
                
                positions[INITIAL_PROCESS_DATA_START_POSITION+1] = rwf.getFilePointer();
                if(writeHeader){
                    writeHeader();
                }
            } catch (IOException ex) {
                Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(oos!=null){
                        oos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void fileWriteDataOnInitialEvolutionaryProcess(InitialEvolutionaryProcessEvent eev) {
        writeDataOnInitialEvolutionaryProcess(eev, false);
        try {
//            this.rwf.seek(positions[INITIAL_PROCESS_DATA_START_POSITION+1]);            
            rwf.writeShort(propertiesToFollow.size()+1);
            rwf.writeUTF("performance");
            for(String k: propertiesToFollow){
                rwf.writeUTF(k);
            }
            positions[INITIAL_PROCESS_DATA_START_POSITION+2] = rwf.getFilePointer();

            writeHeader();
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void writeDataPropertiesToFollow(List<String> propertiesToFollow, List<Double> values) {
        try {
            this.rwf.seek(positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION]);            
            rwf.writeShort(propertiesToFollow.size()+1);
            rwf.writeUTF("performance");
            for(String k: propertiesToFollow){
                rwf.writeUTF(k);
            }
            for(Double v: values){
                rwf.writeDouble(v);
            }

            positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+2] = positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+1] = rwf.getFilePointer();

            writeHeader();
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    

    public void fileReadDataOnFinishEvolutionCycle(Map<String, List<Double>> extraInfo, List<String> serieTitles) {
        try {
            rwf.seek(positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION]);
            extraInfo.clear();
            serieTitles.clear();
            short nSeries = rwf.readShort();
            for(int i=0; i<nSeries; i++){
                String t = rwf.readUTF();
                serieTitles.add(t);
                extraInfo.put(t, new ArrayList<>());
            }
            while(rwf.getFilePointer()<positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+1]){
                for(int i=0; i<nSeries; i++){
                    extraInfo.get(serieTitles.get(i)).add(rwf.readDouble());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fileWriteDataOnFinishEvolutionCycle(FinishedEvolutionaryCycleEvent eev) {
        try {
            rwf.seek(positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+1]);            
            rwf.writeDouble(eev.getAvgPerformance());
            for(String k: propertiesToFollow){
                rwf.writeDouble(eev.getExtraInfo(k));
            }
            
            positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+2] = positions[CYCLIC_RESULTS_PROCESS_DATA_START_POSITION+1] = rwf.getFilePointer();
            writeHeader();
        } catch (Exception /*IOException*/ ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    
    
    public Pair<ParentLine<PtpNeuralNetwork>[], ParentLine<PtpNeuralNetwork>[]> fileReadDataOnProgenyLinesEvolutionaryProcess() {
        ParentLine<PtpNeuralNetwork>[] bpro = new ParentLine[0];
        ParentLine<PtpNeuralNetwork>[] wpro = new ParentLine[0];
        try {
            rwf.seek(positions[PROGENY_LINES_START_POSITION]);
            long nextPointer = rwf.readLong();
            int len = (int) (nextPointer - rwf.getFilePointer());
            byte[] buf = new byte[len];
            rwf.readFully(buf);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bis);
            bpro = (ParentLine<PtpNeuralNetwork>[]) ois.readObject();
            for (ParentLine<PtpNeuralNetwork> l : bpro) {
                ParentLine<PtpNeuralNetwork> i = l;
                while(i.getParent()!=null){
                    i.getAgent().updateInputNeurons();
                    i.getAgent().updateOutputNeurons();
                    i=i.getParent();
                }
            }
            len = (int) (positions[PROGENY_LINES_START_POSITION+1] - rwf.getFilePointer());
            buf = new byte[len];
            rwf.readFully(buf);
            bis = new ByteArrayInputStream(buf);
            ois = new ObjectInputStream(bis);
            wpro = (ParentLine<PtpNeuralNetwork>[]) ois.readObject();
            for (ParentLine<PtpNeuralNetwork> l : wpro) {
                ParentLine<PtpNeuralNetwork> i = l;
                while(i.getParent()!=null){
                    i.getAgent().updateInputNeurons();
                    i.getAgent().updateOutputNeurons();
                    i=i.getParent();
                }
            }            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Pair<>(bpro, wpro);
    }

    private void writeDataOnProgenyLinesEvolutionaryProcess(Pair<ParentLine<PtpNeuralNetwork>[],ParentLine<PtpNeuralNetwork>[]> lines) {
        long arSizeInBytes=0;
        ObjectOutputStream oos = null;
        try {
            //maxBetterProgenyLines = lines.getFirst().length;
            rwf.seek(positions[PROGENY_LINES_START_POSITION]);            
            rwf.writeLong(0);
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(lines.getFirst());
                oos.flush();
                byte[] ao = baos.toByteArray();
                rwf.write(ao);  
                arSizeInBytes=rwf.getFilePointer();
            } finally {
                try {
                    if(oos!=null){
                        oos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }               
            //maxBadProgenyLines = lines.getLast().length;
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(lines.getLast());
                oos.flush();
                byte[] ao = baos.toByteArray();
                rwf.write(ao);                        
            } finally {
                try {
                    if(oos!=null){
                        oos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }               
            positions[PROGENY_LINES_START_POSITION+1] = rwf.getFilePointer();
            rwf.seek(positions[PROGENY_LINES_START_POSITION]);
            rwf.writeLong(arSizeInBytes);
            writeHeader();        
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }                            
        
    }
    
    private void writeDataOnProgenyLinesEvolutionaryProcess(ParentLine<PtpNeuralNetwork>[] lines) {
        long arSizeInBytes=0;
        ObjectOutputStream oos = null;
        try {
            int l = Math.min(lines.length, maxBetterProgenyLines);
            ParentLine<PtpNeuralNetwork>[] best = new ParentLine[l];
            System.arraycopy(lines, 0, best, 0, l);            
            rwf.seek(positions[PROGENY_LINES_START_POSITION]);            
            rwf.writeLong(arSizeInBytes);
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(best);
                oos.flush();
                byte[] ao = baos.toByteArray();
                rwf.write(ao);                        
                arSizeInBytes=rwf.getFilePointer();
            } finally {
                try {
                    if(oos!=null){
                        oos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }               
            l = Math.min(lines.length -l, maxBadProgenyLines);
            ParentLine<PtpNeuralNetwork>[] bad = new ParentLine[l];
            System.arraycopy(lines, lines.length-l, bad, 0, l);            
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(bad);
                oos.flush();
                byte[] ao = baos.toByteArray();
                rwf.write(ao);                        
            } finally {
                try {
                    if(oos!=null){
                        oos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }               
            positions[PROGENY_LINES_START_POSITION+1] = rwf.getFilePointer();
            rwf.seek(positions[PROGENY_LINES_START_POSITION]);
            rwf.writeLong(arSizeInBytes);
            writeHeader();        
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        }                            
    }
    
    public void fileWriteDataOnProgenyLinesEvolutionaryProcess(ProgenyLinesEvent eev) {
        writeDataOnProgenyLinesEvolutionaryProcess(eev.getProgenyLines());
//        ObjectOutputStream oos = null;
//        try {
//            rwf.seek(positions[PROGENY_LINES_START_POSITION]);            
//            rwf.writeDouble(eev.getProgenyLines().length);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            oos = new ObjectOutputStream(baos);
//            oos.writeObject(eev.getProgenyLines());
//            oos.flush();
//            byte[] ao = baos.toByteArray();
//            rwf.write(ao);                        
//            positions[PROGENY_LINES_START_POSITION+1] = rwf.getFilePointer();
//            writeHeader();        
//        } catch (IOException ex) {
//            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                if(oos!=null){
//                    oos.close();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }                            
    }
    
    public CompletedEvolutionaryProcessEvent fileReadDataOnCompletedEvolutionaryProcess() {
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
            Logger.getLogger(NetvolutionDataFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eev;
    }
    
    public void writeDataOnCompletedEvolutionaryProcess(CompletedEvolutionaryProcessEvent eev) {
        fileWriteDataOnCompletedEvolutionaryProcess(eev);
    }
    
    public void fileWriteDataOnCompletedEvolutionaryProcess(CompletedEvolutionaryProcessEvent eev) {
        ObjectOutputStream oos = null;
        try {
            for (PtpNeuralNetwork finalPopulation : eev.getFinalPopulation()) {
                finalPopulation.updateInputNeuronsLength();
                finalPopulation.updateOutputNeuronsLength();
            }
            if(positions[FINAL_PROCESS_DATA_START_POSITION]==-1){
                positions[FINAL_PROCESS_DATA_START_POSITION]=positions[PROGENY_LINES_START_POSITION];
            }
            rwf.seek(positions[FINAL_PROCESS_DATA_START_POSITION]);   
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(eev);
            oos.flush();
            byte[] ao = baos.toByteArray();
            rwf.write(ao);            
        } catch (IOException ex) {
            Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(oos!=null){
                    oos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                            
    }

    /**
     * @return the fileOpen
     */
    public boolean isFileOpen() {
        return fileOpen;
    }

    private void writeContent(NetvolutionData content) {
        this.writeHeader(content.positions);
        this.writeVersion(content.version);
        this.writeDataOnInitialEvolutionaryProcess(content.initialEvolutionaryProcessEvent);
        this.writeDataPropertiesToFollow(content.getSerieTitles(), content.extraInfo);
        if(content.getPositions()[PROGENY_LINES_START_POSITION] < content.getPositions()[FINAL_PROCESS_DATA_START_POSITION]){
            this.writeDataOnProgenyLinesEvolutionaryProcess(content.getLines());
        }
        this.writeDataOnCompletedEvolutionaryProcess(content.getCompletedEvolutionaryProcessEvent());
    }
    
    
    public static class NetvolutionData{
        private long[] positions=null;
        private String version=null;
        private JsonNode jsonNode=null;
        private InitialEvolutionaryProcessEvent initialEvolutionaryProcessEvent=null;
        private List<Double> extraInfo=null;
        private List<String> serieTitles=null;
        private ParentLine<PtpNeuralNetwork>[] betterLines=null; 
        private ParentLine<PtpNeuralNetwork>[] badLines=null; 
        private CompletedEvolutionaryProcessEvent completedEvolutionaryProcessEvent=null;

        /**
         * @return the positions
         */
        public long[] getPositions() {
            return positions;
        }

        /**
         * @param positions the positions to set
         */
        public void setPositions(long[] positions) {
            this.positions = positions;
        }

        /**
         * @return the version
         */
        public String getVersion() {
            return version;
        }

        /**
         * @param version the version to set
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * @return the jsonNode
         */
        public JsonNode getJsonNode() {
            return jsonNode;
        }

        /**
         * @param jsonNode the jsonNode to set
         */
        public void setJsonNode(JsonNode jsonNode) {
            this.jsonNode = jsonNode;
        }

        /**
         * @return the initialEvolutionaryProcessEvent
         */
        public InitialEvolutionaryProcessEvent getInitialEvolutionaryProcessEvent() {
            return initialEvolutionaryProcessEvent;
        }

        /**
         * @param initialEvolutionaryProcessEvent the initialEvolutionaryProcessEvent to set
         */
        public void setInitialEvolutionaryProcessEvent(InitialEvolutionaryProcessEvent initialEvolutionaryProcessEvent) {
            this.initialEvolutionaryProcessEvent = initialEvolutionaryProcessEvent;
        }

        /**
         * @return the extraInfo
         */
        public List<Double> getExtraInfo() {
            return extraInfo;
        }

        /**
         * @param extraInfo the extraInfo to set
         */
        public void setExtraInfo(List<Double> extraInfo) {
            this.extraInfo = extraInfo;
        }

        /**
         * @return the serieTitles
         */
        public List<String> getSerieTitles() {
            return serieTitles;
        }

        /**
         * @param serieTitles the serieTitles to set
         */
        public void setSerieTitles(List<String> serieTitles) {
            this.serieTitles = serieTitles;
        }
        
        /**
         * @param bestLines
         * @param badLines
         */
        public void setLines(ParentLine<PtpNeuralNetwork>[] bestLines, ParentLine<PtpNeuralNetwork>[] badLines) {
            this.betterLines = bestLines;
            this.badLines = badLines;                    
        }

        public void setLines(Pair<ParentLine<PtpNeuralNetwork>[],ParentLine<PtpNeuralNetwork>[]> lines) {
            this.betterLines = lines.getFirst();
            this.badLines = lines.getLast();                    
        }

        public Pair<ParentLine<PtpNeuralNetwork>[],ParentLine<PtpNeuralNetwork>[]> getLines() {
            return new Pair<>(betterLines, badLines);
        }
        
        /**
         * @return the lines
         */
        public ParentLine<PtpNeuralNetwork>[] getBetterLines() {
            return betterLines;
        }

        /**
         * @param lines the lines to set
         */
        public void setBetterLines(ParentLine<PtpNeuralNetwork>[] lines) {
            this.betterLines = lines;
        }
        
        /**
         * @return the lines
         */
        public ParentLine<PtpNeuralNetwork>[] getWorstLines() {
            return badLines;
        }

        /**
         * @param lines the lines to set
         */
        public void setWorstLines(ParentLine<PtpNeuralNetwork>[] lines) {
            this.badLines = lines;
        }
        

        /**
         * @return the completedEvolutionaryProcessEvent
         */
        public CompletedEvolutionaryProcessEvent getCompletedEvolutionaryProcessEvent() {
            return completedEvolutionaryProcessEvent;
        }

        /**
         * @param completedEvolutionaryProcessEvent the completedEvolutionaryProcessEvent to set
         */
        public void setCompletedEvolutionaryProcessEvent(CompletedEvolutionaryProcessEvent completedEvolutionaryProcessEvent) {
            this.completedEvolutionaryProcessEvent = completedEvolutionaryProcessEvent;
        }
    }
}
