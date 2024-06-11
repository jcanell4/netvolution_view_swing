package org.elsquatrecaps.netvolution.persistence.upgradeprocessors;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;

/**
 *
 * @author josep
 */
public abstract class ConfigurationDataUpgrader {
    private static HashMap<String, ConfigurationDataUpgrader> mapInstances;
    static{
        mapInstances = new HashMap<>();
        mapInstances.put("0.0.0.0", new ConfigurationDataUpgraderFrom0_0_0_0_to_0_0_0_1());
        mapInstances.put("0.0.0.1", new ConfigurationDataUpgraderFrom0_0_0_1_to_0_0_0_2());
    }
    
    public static ConfigurationDataUpgrader getInstanceUpgraderForOldVersion(String version){
        return mapInstances.get(version);
    }
    
    public abstract JsonNode upgrade(JsonNode content);

    public abstract String forUpgradingTo();

    public abstract String forUpgradingFrom();
    
}
