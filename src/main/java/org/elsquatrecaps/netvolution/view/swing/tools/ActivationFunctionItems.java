package org.elsquatrecaps.netvolution.view.swing.tools;

import org.elsquatrecaps.utilities.tools.ClassGroupItem;
import java.util.List;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.ActivationFunction;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.ActivationFunctionInfo;
import org.elsquatrecaps.utilities.tools.Callback;
import org.elsquatrecaps.utilities.tools.Pair;

/**
 *
 * @author josep
 */
public class ActivationFunctionItems extends ClassGroupItem<ActivationFunction>{
    private String name;

    public ActivationFunctionItems(String id, String name, Class<ActivationFunction> type) {
        super(id, type);
        this.name=name;
    }

    public ActivationFunctionItems(String name, Class<ActivationFunction> type) {
        this(name.trim().replace(" ", "_").toUpperCase(), name, type);
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public static List<ActivationFunctionItems> getItemsList(String packageToSearch, Class<ActivationFunction> clazz){
        return ClassGroupItem.getItemsList(packageToSearch, ActivationFunctionInfo.class, new Callback<Pair<ActivationFunctionInfo, Class<ActivationFunction>>, ActivationFunctionItems>() {
            @Override
            public ActivationFunctionItems call(Pair<ActivationFunctionInfo, Class<ActivationFunction>> param) {
                return new ActivationFunctionItems(param.getFirst().id(), param.getFirst().name(), param.getSecond()); 
            }
        });
    }    
}
