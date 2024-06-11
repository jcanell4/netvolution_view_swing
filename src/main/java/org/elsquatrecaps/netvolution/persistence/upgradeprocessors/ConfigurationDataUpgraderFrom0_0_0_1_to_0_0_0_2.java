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
public class ConfigurationDataUpgraderFrom0_0_0_1_to_0_0_0_2 extends ConfigurationDataUpgrader{
    @Override
    public JsonNode upgrade(JsonNode content) {
        ObjectNode metadata;
        metadata = (ObjectNode) content.get("metadata");
        metadata.put("version", ConfigurationDataFileManager.currentVersion);
        
        ObjectNode evolutionProcessConditions = (ObjectNode) content.get("evolutionarySystem").get("evolutionProcessConditions");
        evolutionProcessConditions.put("optimizationMethodUsed", evolutionProcessConditions.get("deathRateType").asInt());
        evolutionProcessConditions.put("survivalRateValue", evolutionProcessConditions.get("deathRateValue").asInt());
        evolutionProcessConditions.remove("deathRateType");
        evolutionProcessConditions.remove("deathRateValue");
        
        ObjectNode lineage;
        lineage = (ObjectNode) content.get("evolutionarySystem").get("optionalDataToRecording").get("lineage");       
        lineage.remove("defaultFileName");
        return content;
    }

    @Override
    public String forUpgradingTo() {
        return "0.0.0.2";
    }

    @Override
    public String forUpgradingFrom() {
        return "0.0.0.1";
    }
    
}
