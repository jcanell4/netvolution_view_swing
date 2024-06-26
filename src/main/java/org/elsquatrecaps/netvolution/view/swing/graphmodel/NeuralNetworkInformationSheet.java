package org.elsquatrecaps.netvolution.view.swing.graphmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetwork;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuron;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.actfunctions.SigmoidActivationFunction;

/**
 *
 * @author josep
 */
public class NeuralNetworkInformationSheet {

    /**
     * @return the valueToOrdering
     */
    public double getValueToOrdering() {
        return valueToOrdering;
    }
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
    private final double valueToOrdering;
    private String textualView="";    
    private final ArrayList<Map<String,List<Float>>> resultsFromValueTable = new ArrayList<>();
    private final List<Integer> biasList = new ArrayList<>();
    private final List<Integer> gostBiasList = new ArrayList<>();
    private final List<Integer> realBiasList = new ArrayList<>();
    private final List<String>  biasIntervals = new ArrayList<>();
    private final List<String> betaIntervals  = new ArrayList<>();
    private final List<Integer> betaList = new ArrayList<>();
    private final List<Integer> gostBetaList = new ArrayList<>();
    private final List<Integer> realBetaList = new ArrayList<>();

    public NeuralNetworkInformationSheet(String id, int inputNeurons, int connectionsLength, int outpuNeurons, double neuronDensityIndex, int forwardConnections, int backwardConnections, double calculationEficiencyIndex, double activationFunctionLinearityDegree, double performance, double valueToOrdering) {
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
        this.valueToOrdering = valueToOrdering;
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

    /**
     * @return the textualView
     */
    public String getTextualView() {
        return textualView;
    }

    /**
     * @param textualView the textualView to set
     */
    public void setTextualView(String textualView) {
        this.textualView = textualView;
    }

    /**
     * @return the biasList
     */
    public List<Integer> getBiasList() {
        return biasList;
    }

    /**
     * @return the betaList
     */
    public List<Integer> getBetaList() {
        return betaList;
    }
    
    /**
     * @return the biasList
     */
    public List<String> getBiasIntervals() {
        return biasIntervals;
    }

    /**
     * @return the betaList
     */
    public List<String> getBetaIntervals() {
        return betaIntervals;
    }
    
    public void addBiasAndBetaValues(PtpNeuralNetwork nn){
        float minBias = Float.MAX_VALUE;
        float maxBias = Float.MIN_VALUE;
        float minBeta = Float.MAX_VALUE;
        float maxBeta = Float.MIN_VALUE;
        this.betaList.clear();
        this.biasList.clear();
        this.betaIntervals.clear();
        this.biasIntervals.clear();
        for(PtpNeuron n: nn.getNeurons()){            
            if(n.getBias()<minBias){
                minBias = n.getBias();
            }
            if(((SigmoidActivationFunction)n.getActivationFunction()).getBeta()<minBeta){
                minBeta = ((SigmoidActivationFunction)n.getActivationFunction()).getBeta();
            }
            if(n.getBias()>maxBias){
                maxBias = n.getBias();
            }
            if(((SigmoidActivationFunction)n.getActivationFunction()).getBeta()>maxBeta){
                maxBeta = ((SigmoidActivationFunction)n.getActivationFunction()).getBeta();
            }
        }
        float biasInterval = (maxBias-minBias)/10;
        float betaInterval = (maxBeta-minBeta)/10;
        float biasAcum = minBias;
        float betaAcum = minBeta;
        for(int i=0; i<11; i++){
            if(biasInterval!=0){
                biasIntervals.add(String.format("%3.1f-%3.1f", biasAcum, biasAcum+biasInterval));
                biasAcum +=biasInterval;
                biasList.add(0);
                realBiasList.add(0);
                gostBiasList.add(0);                
            }else if(biasInterval==0 && i==0){
                biasIntervals.add(String.format("%3.1f-%3.1f", biasAcum, biasAcum+biasInterval));
                biasList.add(0);
                realBiasList.add(0);
                gostBiasList.add(0);
            }
            if(betaInterval!=0){
                betaIntervals.add(String.format("%3.1f-%3.1f", betaAcum, betaAcum+betaInterval));
                betaAcum+=betaInterval;
                betaList.add(0);
                realBetaList.add(0);
                gostBetaList.add(0);
            }else if(betaInterval==0 && i==0){
                betaIntervals.add(String.format("%3.1f-%3.1f", betaAcum, betaAcum+betaInterval));
                betaList.add(0);
                realBetaList.add(0);
                gostBetaList.add(0);
            }
        }
        for(PtpNeuron n: nn.getNeurons()){
            int posBias = (int) ((n.getBias()-minBias)/biasInterval);
            int posBeta = (int) ((((SigmoidActivationFunction)n.getActivationFunction()).getBeta()-minBeta)/betaInterval);
            if(n.isParticipatingInCalculation()){
                realBiasList.set(posBias, realBiasList.get(posBias) + 1);
                realBetaList.set(posBeta, realBetaList.get(posBeta) + 1);
            }else{
                gostBiasList.set(posBias, gostBiasList.get(posBias) + 1);
                gostBetaList.set(posBeta, gostBetaList.get(posBeta) + 1);
            }
            biasList.set(posBias, biasList.get(posBias) + 1);
            betaList.set(posBeta, betaList.get(posBeta) + 1);
        }        
    }

    /**
     * @return the gostBiasList
     */
    public List<Integer> getGhostBiasList() {
        return gostBiasList;
    }

    /**
     * @return the realBiasList
     */
    public List<Integer> getRealBiasList() {
        return realBiasList;
    }

    /**
     * @return the gostBetaList
     */
    public List<Integer> getGhostBetaList() {
        return gostBetaList;
    }

    /**
     * @return the realBetaList
     */
    public List<Integer> getRealBetaList() {
        return realBetaList;
    }
}
