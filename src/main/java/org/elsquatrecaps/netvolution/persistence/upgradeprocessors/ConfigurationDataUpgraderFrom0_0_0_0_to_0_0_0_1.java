/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.persistence.upgradeprocessors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elsquatrecaps.netvolution.persistence.ConfigurationDataFileManager;

/**
 *
 * @author josep
 */
public class ConfigurationDataUpgraderFrom0_0_0_0_to_0_0_0_1 extends ConfigurationDataUpgrader{
    @Override
    public JsonNode upgrade(JsonNode content) {
        ObjectNode metadata;
        metadata = ((ObjectNode)content).putObject("metadata");
        metadata.put("version", ConfigurationDataFileManager.currentVersion);
        
        ObjectNode evolutionProcessConditions = (ObjectNode) content.get("evolutionarySystem").get("evolutionProcessConditions");
        evolutionProcessConditions.put("deathRateType", 0);
        evolutionProcessConditions.put("deathRateValue", 50);
        
        ObjectNode toRecording;
        toRecording = ((ObjectNode)(content.get("evolutionarySystem"))).putObject("optionalDataToRecording");
        ObjectNode lineage;
        lineage = toRecording.putObject("lineage");       
        lineage.put("recording", Boolean.FALSE);
        lineage.put("numberOfBestProgenyLinesToSave", 0);
        lineage.put("numberOfWorstProgenyLinesToSave", 0);
        lineage.put("defaultFileName", "tmp.lin");
        return content;
    }

    @Override
    public String forUpgradingTo() {
        return "0.0.0.1";
    }

    @Override
    public String forUpgradingFrom() {
        return "0.0.0.0";
    }
    
}
