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
public class ConfigurationDataUpgraderFrom0_0_0_2_to_0_0_0_3 extends ConfigurationDataUpgrader{
    @Override
    public JsonNode upgrade(JsonNode content) {
        ObjectNode metadata;
        metadata = (ObjectNode) content.get("metadata");
        metadata.put("version", ConfigurationDataFileManager.currentVersion);
        
        ObjectNode evolutionProcessConditions = (ObjectNode) content.get("evolutionarySystem").get("evolutionProcessConditions");
        evolutionProcessConditions.put("fixDeathTaxValue", 50);
        return content;
    }

    @Override
    public String forUpgradingTo() {
        return "0.0.0.3";
    }

    @Override
    public String forUpgradingFrom() {
        return "0.0.0.2";
    }
    
}
