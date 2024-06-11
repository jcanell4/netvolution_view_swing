package org.elsquatrecaps.netvolution.persistence.upgradeprocessors;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import org.elsquatrecaps.netvolution.persistence.NetvolutionDataFileManager.NetvolutionData;

/**
 *
 * @author josep
 */
public abstract class NetvolutionDataDataUpgrader {
    private static HashMap<String, NetvolutionDataDataUpgrader> mapInstances;
    static{
        mapInstances = new HashMap<>();
        mapInstances.put("1.0.0", new NetvolutionDataDataUpgraderFrom_1_0_0_to_1_0_1());
        mapInstances.put("1.0.1", new NetvolutionDataDataUpgraderFrom_1_0_1_to_1_0_2());
    }
    
    public static NetvolutionDataDataUpgrader getInstanceUpgraderForOldVersion(String version){
        return mapInstances.get(version);
    }
    
    public abstract NetvolutionData upgrade(NetvolutionData content);

    public abstract String forUpgradingTo();

    public abstract String forUpgradingFrom();
    
    public abstract NetvolutionData getContent(RandomAccessFile raf);
}
