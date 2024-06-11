package org.elsquatrecaps.netvolution.persistence.upgradeprocessors;

import java.io.RandomAccessFile;
import org.elsquatrecaps.netvolution.persistence.NetvolutionDataFileManager.NetvolutionData;

/**
 *
 * @author josep
 */
public class NetvolutionDataDataUpgraderFrom_1_0_1_to_1_0_2 extends NetvolutionDataDataUpgrader {

    public NetvolutionDataDataUpgraderFrom_1_0_1_to_1_0_2() {
    }

    @Override
    public String forUpgradingTo() {
        return "1.0.2";
    }

    @Override
    public String forUpgradingFrom() {
        return "1.0.1";
    }

    @Override
    public NetvolutionData upgrade(NetvolutionData content) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NetvolutionData getContent(RandomAccessFile raf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
