package org.elsquatrecaps.netvolution.view.swing.graphmodel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.calculators.PtpNeuralNetworkNeuralCalculationEficiencyCalculator;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.calculators.PtpNeuralNetworkNeuronConnectionDensityCalculator;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.calculators.PtpNeuralNetworkTrueTableGlobalCalculator;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetwork;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuron;

/**
 *
 * @author josep
 */
public class PtpNeuralNetworkPopulationViewer {
    private PtpNeuralNetworkTrueTableGlobalCalculator calculator;
    private final PtpNeuralNetworkNeuronConnectionDensityCalculator neuronDensityCalculator = new PtpNeuralNetworkNeuronConnectionDensityCalculator();
    private final PtpNeuralNetworkNeuralCalculationEficiencyCalculator eficiencyCalculator = new PtpNeuralNetworkNeuralCalculationEficiencyCalculator();
    private PtpNeuralNetwork[] population;
    private Integer[] populationIndex;
    private NeuralNetworkInformationSheet[] populationInfo;
    private int currentPos=0;
    private Float[][] environmentInputSet;
    private Float[][] environmentOutputSet;
    private List<Object> vertexRoots;

    
    
//new PtpNeuralNetworkTrueTableGlobalCalculator(vitalAdvantages, reproductiveAdvantages, environmentInputSet, environmentOutputSet), 

    public PtpNeuralNetworkPopulationViewer(){
    }
    
    public void init(PtpNeuralNetwork[] pop, Float[][] inputSet, Float[][] outputSet, List<String> va, List<String> ra){
        calculator = new PtpNeuralNetworkTrueTableGlobalCalculator(va, ra, inputSet, outputSet);
        this.environmentInputSet = inputSet;
        this.environmentOutputSet = outputSet;
        this.population = pop;
        updateInfo();
        order();
    }
    
    public void init(PtpNeuralNetworkTrueTableGlobalCalculator calc, PtpNeuralNetwork[] pop){
        calculator = calc;
        population = pop;
        updateInfo();
        order();
    }
    
    private void updateInfo(){
        populationInfo = new NeuralNetworkInformationSheet[getPopulation().length];
        populationIndex = new Integer[getPopulation().length];
        for(int i=0; i<getPopulation().length; i++){
            populationIndex[i]=i;
            populationInfo[i] = getNetworkInformationSheet(i, getPopulation()[i]);
        }        
    }
    
    private NeuralNetworkInformationSheet getNetworkInformationSheet(int id, PtpNeuralNetwork nn){
        NeuralNetworkInformationSheet ret;
        PtpNeuralNetworkTrueTableGlobalCalculator.PerformaceAndReproductiveAdvantage calc = getCalculator().calculate(nn);
        double performanece = calc.getPerformance().doubleValue();
        double toOrder = calc.getReproductiveAdvantage().doubleValue();
        double eficiency = getEficiencyCalculator().calculate(nn).doubleValue();
        double density = getNeuronDensityCalculator().calculate(nn).doubleValue();
        int[] fbConnections = calculateConections(nn);
        ret = new NeuralNetworkInformationSheet(
                String.valueOf(id),
                nn.getInputNeuronsLength(),
                nn.getActualConnectionsLength(),
                nn.getOutputNeuronsLength(),
                density,
                fbConnections[0], 
                fbConnections[1], 
                eficiency, 
                0, 
                performanece,
                toOrder);
        updateResults(nn, ret);
        ret.setTextualView(nn.toString());
        updateBiasAndBeta(nn, ret);
        return ret;
    }
    
    private void updateBiasAndBeta(PtpNeuralNetwork nn, NeuralNetworkInformationSheet info){
        info.addBiasAndBetaValues(nn);
    }
    
    private void updateResults(PtpNeuralNetwork nn, NeuralNetworkInformationSheet info){
        for(int i=0; i<getEnvironmentInputSet().length; i++){
            Float[] s = nn.update(getEnvironmentInputSet()[i]);
            Float[] r = nn.updateSM(getEnvironmentInputSet()[i]);
            info.addResultsFromValueTable(getEnvironmentInputSet()[i], getEnvironmentOutputSet()[i], r, s);
        }
    }
    
    private int[] calculateConections(PtpNeuralNetwork nn){
        int[] ret = {0,0};
        for(int fromNeuron = 0; fromNeuron<nn.getMaxNeuronsLength(); fromNeuron++){
            for(int toNeuron=0; toNeuron<nn.getMaxNeuronsLength(); toNeuron++){
                if(nn.getWeight(fromNeuron, toNeuron)!=0){
                    if(toNeuron>fromNeuron){
                        ret[0]++;
                    }else{
                        ret[1]++;
                    }
                }
            }
        }
        return ret;
    }
    
    private void order(){
        Arrays.sort(getPopulationIndex(), new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                int ret = Double.compare(getPopulationInfo()[t1].getValueToOrdering(),
                    getPopulationInfo()[t2].getValueToOrdering()
                );
                return ret;
            }
        });        
    }
    
    public boolean canJumpPosition(int positionsToJump){
        return getCurrentPos()+positionsToJump<getPopulation().length;
    }
    
    public void jumpPostion(int positionsToJump){
        setPostion(getCurrentPos()+positionsToJump);
    }
    
    public void setPostion(int pos){
        if(pos<0){
            pos =0;
        }
        if(pos>=getPopulation().length){
            pos = getPopulation().length-1;
        }
        currentPos = pos;
    }
    
    public NeuralNetworkInformationSheet getInformationSheet(){
        return getPopulationInfo()[getPopulationIndex()[getCurrentPos()]];
    }
    
    public void setPositionToLast(){
        setPostion(getPopulation().length-1);
    }

//    public PtpNeuralNetwork getPopulation(){
//        return population[populationIndex[currentPos]];
//    }
    
//    private mxCell insertVertexFromAgentNeuron(PtpNeuralNetwork nn, int neuronId, mxGraph ret, int posx, int posy){
//        Object parent  = ret.getDefaultParent();
//        PtpNeuron neuron = nn.getNeuron(neuronId);
//        mxCell v = (mxCell) ret.insertVertex(parent, String.valueOf(neuron.getId()), neuron, posx, posy, 40, 40);
//        String style="shape=ellipse;perimeter=ellipsePerimeter;spacingTop=2;spacingBottom=2;spacingLeft=2;spacingRight=2;width=40;height=40;%s%s";
//        if(neuron.getId()<nn.getInputNeuronsLength()){
//            getVertexRoots().add(v);       
//            style = String.format(style, mxConstants.STYLE_FILLCOLOR, "=#FF0000;");
//        }else if(neuron.getId()>=nn.getMaxNeuronsLength()-nn.getOutputNeuronsLength()){
//            style = String.format(style, mxConstants.STYLE_FILLCOLOR, "=#00FF00;");
//        //}else if(nn.){
//
//        }else{
//            style = String.format(style, "", "");
//        }
//        v.setStyle(style);
//        return v;        
//    }
    
    public mxGraph getGraph(){
        List<Boolean> cellsToPaint = new ArrayList<>();
        Map<Integer, mxCell> controlVertex = new HashMap<>();
        vertexRoots = new ArrayList<>();
        mxGraph ret = new mxGraph();
        Object parent  = ret.getDefaultParent();
        ret.getModel().beginUpdate();
        PtpNeuralNetwork nn = getPopulation()[getPopulationIndex()[getCurrentPos()]];
        for(int i=0; i<nn.getMaxNeuronsLength(); i++){
            cellsToPaint.add(false);
        }
        for(int toNeuron=0; toNeuron<nn.getMaxNeuronsLength(); toNeuron++){
            for(int fromNeuron = 0; fromNeuron<nn.getMaxNeuronsLength(); fromNeuron++){
                if(nn.getWeight(fromNeuron, toNeuron)!=0){
                    cellsToPaint.set(fromNeuron, nn.getNeuron(fromNeuron).isParticipatingInCalculation());
                    cellsToPaint.set(toNeuron, nn.getNeuron(toNeuron).isParticipatingInCalculation());
                }
            }
        }
        
        try {
            int c=0;
            int posx = 10;
            int posy = -90;
            for(int i=0; i< nn.getNeurons().length; i++){
                PtpNeuron neuron = nn.getNeuron(i);
                if(cellsToPaint.get(i)){
                    String style="shape=ellipse;perimeter=ellipsePerimeter;spacingTop=2;spacingBottom=2;spacingLeft=2;spacingRight=2;width=25;height=25;%s%s";
                    if(i<nn.getInputNeuronsLength()){
                        posy = 10;
                        posx+=10;
                        style = String.format(style, mxConstants.STYLE_FILLCOLOR, "=#FF0000;");
                    }else if(i>=nn.getMaxNeuronsLength()-nn.getOutputNeuronsLength()){
                        posy = 10*c;
                        if(i==nn.getMaxNeuronsLength()-nn.getOutputNeuronsLength()){
                            posx=10;
                        }else{
                            posx+=10;
                        }
                        style = String.format(style, mxConstants.STYLE_FILLCOLOR, "=#00FF00;");
                    }else{
                        if((i-nn.getInputNeuronsLength())%7==0){
                            posx=0;
                            posy+=10;
                        }else{
                            posy+=10;
                            posx+=10;
                        }
                        style = String.format(style, "", "");
                    }
                    c++;
                    mxCell v = (mxCell) ret.insertVertex(parent, String.valueOf(neuron.getId()), neuron, posx, posy, 40, 40);
                    v.setStyle(style);
                    if(i<nn.getInputNeuronsLength()){
                        getVertexRoots().add(v);
                    }
                    controlVertex.put(i, v);
                }
            }
            for(int toNeuron=0; toNeuron<nn.getMaxNeuronsLength(); toNeuron++){
                for(int fromNeuron = 0; fromNeuron<nn.getMaxNeuronsLength(); fromNeuron++){
                    if(nn.getWeight(fromNeuron, toNeuron)!=0){
                        mxCell v1;
                        mxCell v2;
//                        if(!controlVertex.containsKey(fromNeuron)){
//                            v1 =  insertVertexFromAgentNeuron(nn, fromNeuron, ret, posy, posx);
//                            controlVertex.put(fromNeuron, v1);
//                        }else{
//                            v1 = controlVertex.get(fromNeuron);
//                        }
//                        posx+=50;
//                        if(!controlVertex.containsKey(toNeuron)){
//                            v2 =  insertVertexFromAgentNeuron(nn, toNeuron, ret, posy, posx+100);
//                            controlVertex.put(toNeuron, v2);
//                        }else{
//                            v2 = controlVertex.get(toNeuron);
//                        }
                        v1 = controlVertex.get(fromNeuron);
                        v2 = controlVertex.get(toNeuron);
                        ret.insertEdge(parent, null, String.format("%6.4f", nn.getWeight(fromNeuron, toNeuron)), v1, v2);
                    }
                }
//                posy+=50;
            }
        } finally {
            ret.getModel().endUpdate();
        }
        
        return ret;
    }

    /**
     * @return the calculator
     */
    public PtpNeuralNetworkTrueTableGlobalCalculator getCalculator() {
        return calculator;
    }

    /**
     * @return the neuronDensityCalculator
     */
    public PtpNeuralNetworkNeuronConnectionDensityCalculator getNeuronDensityCalculator() {
        return neuronDensityCalculator;
    }

    /**
     * @return the eficiencyCalculator
     */
    public PtpNeuralNetworkNeuralCalculationEficiencyCalculator getEficiencyCalculator() {
        return eficiencyCalculator;
    }

    /**
     * @return the population
     */
    public PtpNeuralNetwork[] getPopulation() {
        return population;
    }

    /**
     * @return the populationIndex
     */
    public Integer[] getPopulationIndex() {
        return populationIndex;
    }

    /**
     * @return the populationInfo
     */
    public NeuralNetworkInformationSheet[] getPopulationInfo() {
        return populationInfo;
    }

    /**
     * @return the currentPos
     */
    public int getCurrentPos() {
        return currentPos;
    }

    /**
     * @return the environmentInputSet
     */
    public Float[][] getEnvironmentInputSet() {
        return environmentInputSet;
    }

    /**
     * @return the environmentOutputSet
     */
    public Float[][] getEnvironmentOutputSet() {
        return environmentOutputSet;
    }

    /**
     * @return the vertexRoots
     */
    public List<Object> getVertexRoots() {
        return vertexRoots;
    }
}
