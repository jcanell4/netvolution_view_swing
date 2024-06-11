package org.elsquatrecaps.netvolution.menus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.elsquatrecaps.netvolution.constants.CntFrames;
import org.elsquatrecaps.netvolution.persistence.ConfigurationDataFileManager;
import org.elsquatrecaps.netvolution.shareddata.NetvolutionSharedData;
import org.elsquatrecaps.netvolution.view.swing.ConfigurationSystemFrame;
import org.elsquatrecaps.netvolution.view.swing.EvolutionProcessFrame;
import org.elsquatrecaps.netvolution.view.swing.NetvolutionBasicFrame;
import org.elsquatrecaps.netvolution.view.swing.PopulationDetailFrame;
import org.elsquatrecaps.netvolution.view.swing.dialogs.ErrorDialog;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.ActivationFunction;
import org.elsquatrecaps.utilities.tools.Callback;
import org.elsquatrecaps.utilities.tools.FileAndContent;


/**
 *
 * @author josep
 */
public class SystemMenuEvents {
    JFrame parentComponent;

    public SystemMenuEvents(JFrame ownerComponent) {
        this.parentComponent = ownerComponent;
    }
    
    public void aboutItemEvent(){
        JOptionPane.showMessageDialog(parentComponent, "Netvolution v0.1.0\n...", "About Netvolution", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void createAndShowConfigurationSystemFrame(NetvolutionSharedData sd){
        if(sd.frames.containsKey(CntFrames.CONFIGURE_SYSTEM)){
            showFrame(sd.frames.get(CntFrames.CONFIGURE_SYSTEM));
        }else{
            ConfigurationSystemFrame prg = new ConfigurationSystemFrame(sd);
            sd.frames.put(CntFrames.EVOLUTION_PROCESS, prg);
            java.awt.EventQueue.invokeLater(() -> {
                prg.setPreferredSize(new Dimension(1126, 780));
                prg.pack();
                prg.setVisible(true);            
            });
        }
    }
    
    public void createAndShowEvolutionaryProcessFrame(NetvolutionSharedData sd){
        createAndShowEvolutionaryProcessFrame(sd, sd.activationFunctionInstance);
    }
    
    public void createAndShowEvolutionaryProcessFrame(NetvolutionSharedData sd, ActivationFunction af){
        createAndShowEvolutionaryProcessFrame(sd, af, true);
    }
    
    public void createAndShowEvolutionaryProcessFrame(NetvolutionSharedData sd, ActivationFunction af, boolean fromFile){
        EvolutionProcessFrame prg;
        if(sd.frames.containsKey(CntFrames.EVOLUTION_PROCESS)){
            prg = (EvolutionProcessFrame) sd.frames.get(CntFrames.EVOLUTION_PROCESS);
            prg.setForRunningProcess(!fromFile);
            showFrame(sd.frames.get(CntFrames.EVOLUTION_PROCESS));
        }else{
            prg = new EvolutionProcessFrame(sd);
            sd.frames.put(CntFrames.EVOLUTION_PROCESS, prg);
            java.awt.EventQueue.invokeLater(() -> {
                prg.setPreferredSize(new Dimension(1256, 780));
                prg.pack();
                prg.init(af, fromFile);
                prg.setVisible(true);
            });   
        }
    
    }

    public void createAndShowPopulationDetailFrame(NetvolutionSharedData sd){
        PopulationDetailFrame prg;
        if(sd.frames.containsKey(CntFrames.POPULATION_DETAIL)){
            showFrame(sd.frames.get(CntFrames.POPULATION_DETAIL));
        }else{
            prg = new PopulationDetailFrame(sd);
            sd.frames.put(CntFrames.POPULATION_DETAIL, prg);
            java.awt.EventQueue.invokeLater(() -> {
                prg.init();
                prg.setPreferredSize(new Dimension(1256, 780));
                prg.pack();
                prg.setVisible(true);
            });         
        }
        
    }
    
    public void showFrame(NetvolutionBasicFrame frameToShow){
        java.awt.EventQueue.invokeLater(() -> {
            frameToShow.updateData();
            frameToShow.setVisible(true);
        });        
    }

    public void openConfigItemEvent(Callback<JsonNode, Void> callback, String filename){
//        File file = new File(filename);
        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            callback.call(objectMapper.readTree(file));
            callback.call(ConfigurationDataFileManager.getContent(filename));
        } catch (IOException ex) {
            ErrorDialog.displayException(parentComponent, "Error opening", "Error opening a config file", ex);
        }
    }
    
    public void selectAndOpenConfigItemEvent(Callback<FileAndContent<JsonNode>, Void> callback){
        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "json", "cfg", "ini");
        fc.setCurrentDirectory(new File("./"));
        fc.setFileFilter(filter);
        int resp = fc.showOpenDialog(parentComponent);
        if(resp!=JFileChooser.CANCEL_OPTION){
            File file = fc.getSelectedFile();
            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                callback.call(new FileAndContent<>(file.getPath(), objectMapper.readTree(file)));
                callback.call(new FileAndContent<>(file.getPath(), ConfigurationDataFileManager.getContent(file)));
            } catch (IOException ex) {
                ErrorDialog.displayException(parentComponent, "Error opening", "Error opening a config file", ex);
            }
        }
    }

    public void saveConfigItemEvent(JsonNode configJson, String defaultFile){
        saveConfigItemEvent(configJson, new File(defaultFile));
    }
    
    protected boolean saveConfigItemEvent(JsonNode configJson, File defaultFile){
        boolean ret = true;
        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.writeValue(defaultFile, configJson);
            ConfigurationDataFileManager.saveContent(configJson, defaultFile);
        } catch (IOException ex) {
            ErrorDialog.displayException(parentComponent, "Error saving", "Error saving a config file", ex);
            ret=false;
        }
        return ret;
    }

    public void saveConfigAsItemEvent(JsonNode configJson, Callback<String, Void> callback){
        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Files", "json");
        //fc.setApproveButtonText(approveButtonText);
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File("./"));
        int resp = fc.showSaveDialog(parentComponent);
        if(resp!=JFileChooser.CANCEL_OPTION){
            File file = fc.getSelectedFile();
            if(!file.getName().endsWith(".json")){
                file = new File(file.getParentFile(), file.getName().concat(".json"));
            }
            if(saveConfigItemEvent(configJson, file)){
                callback.call(file.getPath());
            }
        }
    }    
    
    public void saveDataItemEvent(File defaultDataFile){
        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data Files", "dat");
        //fc.setApproveButtonText(approveButtonText);
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File("./data"));
        int resp = fc.showSaveDialog(parentComponent);
        if(resp!=JFileChooser.CANCEL_OPTION){
            File file = fc.getSelectedFile();
            if(!file.getName().endsWith(".dat")){
                file = new File(file.getParentFile(), file.getName().concat(".dat"));
            }
            copyFileTo(defaultDataFile, file);
        }
    }
    
    public void loadDataItemEvent(File defaultFile, NetvolutionSharedData sharedData){
        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data Files", "dat");
        //fc.setApproveButtonText(approveButtonText);
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File("./data"));
        int resp = fc.showOpenDialog(parentComponent);
        if(resp!=JFileChooser.CANCEL_OPTION){
            File file = fc.getSelectedFile();
            copyFileTo(file, defaultFile);
            sharedData.loadAllData(file);
        }        
    }
    
    protected void copyFileTo(File fromFile, File toFile){
        try {
            Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(SystemMenuEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
