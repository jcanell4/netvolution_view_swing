package org.elsquatrecaps.netvolution.view.swing.graphmodel;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author josep
 */
public class NeuralNetworkInformationSheet {
    private final String id;
    private final int inputNeurons;
    private final int connectionsLength;
    private final int outpuNeurons;
    private final double neuronDensityIndex;
    private final int forwardConnections;
    private final int backwardConnections;
    private final double calculationEficiencyIndex;
    private final double activationFunctionLinearityDegree;
    private final double performance;
    private ArrayList<Map<String,List<Float>>> resultsFromValueTable = new ArrayList<>();

    public NeuralNetworkInformationSheet(String id, int inputNeurons, int connectionsLength, int outpuNeurons, double neuronDensityIndex, int forwardConnections, int backwardConnections, double calculationEficiencyIndex, double activationFunctionLinearityDegree, double performance) {
        this.id = id;
        this.inputNeurons = inputNeurons;
        this.connectionsLength = connectionsLength;
        this.outpuNeurons = outpuNeurons;
        this.neuronDensityIndex = neuronDensityIndex;
        this.forwardConnections = forwardConnections;
        this.backwardConnections = backwardConnections;
        this.calculationEficiencyIndex = calculationEficiencyIndex;
        this.activationFunctionLinearityDegree = activationFunctionLinearityDegree;
        this.performance = performance;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the inputNeurons
     */
    public int getInputNeurons() {
        return inputNeurons;
    }

    /**
     * @return the connectionsLength
     */
    public int getConnectionsLength() {
        return connectionsLength;
    }

    /**
     * @return the outpuNeurons
     */
    public int getOutpuNeurons() {
        return outpuNeurons;
    }

    /**
     * @return the neuronDensityIndex
     */
    public double getNeuronDensityIndex() {
        return neuronDensityIndex;
    }

    /**
     * @return the forwardConnections
     */
    public int getForwardConnections() {
        return forwardConnections;
    }

    /**
     * @return the backwardConnections
     */
    public int getBackwardConnections() {
        return backwardConnections;
    }

    /**
     * @return the calculationEficiencyIndex
     */
    public double getCalculationEficiencyIndex() {
        return calculationEficiencyIndex;
    }

    /**
     * @return the activationFunctionLinearityDegree
     */
    public double getActivationFunctionLinearityDegree() {
        return activationFunctionLinearityDegree;
    }

    /**
     * @return the performance
     */
    public double getPerformance() {
        return performance;
    }

    /**
     * @return the resultsFromValueTable
     */
    public ArrayList<Map<String,List<Float>>> getResultsFromValueTable() {
        return resultsFromValueTable;
    }

    public void addResultsFromValueTable(Float[] in, Float[] out, Float[] result, Float[] sum) {
        Map<String,List<Float>> item = new HashMap<>();
        this.resultsFromValueTable.add(item);
        item.put("I", new ArrayList<>());
        for (Float in1 : in) {
//            int v = in1.intValue();
//            item.get("I").add(v);
            item.get("I").add(in1);
        }
        item.put("O", new ArrayList<>());
        for (Float out1 : out) {
//            int v = out1.intValue();
//            item.get("O").add(v);
            item.get("O").add(out1);
        }
        item.put("R", new ArrayList<>());
        for (Float result1 : result) {
//            int v = result1.intValue();
//            item.get("R").add(v);
            item.get("R").add(result1);
        }
        item.put("S", new ArrayList<>());
        for (Float sum1 : sum) {
            item.get("S").add(sum1);
        }
    }
}
