package org.elsquatrecaps.netvolution.view.swing.backgroundtask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.elsquatrecaps.rsjcb.netvolution.events.ErrorOnProcessEvolution;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcesSubscriptor;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcessEventStore;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcessInfoEditor;
import org.elsquatrecaps.rsjcb.netvolution.events.FinishedEvolutionaryCycleEvent;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.calculators.PtpNeuralNetworkTrueTableGlobalCalculator;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.PtpNeuralNetworkTrueTableEvolutionaryEnvironment;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.PtpVectorNeuralNetworkTrueTableEvolutionaryEnvironmentBuilder;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.optimization.OptimizationMethod;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetworkConfiguration;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetwork;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetworkMutationProcessor;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetworkRandomInitializer;
import org.elsquatrecaps.utilities.tools.Pair;

/**
 *
 * @author josep
 */
public class EvolutionaryEventSwingWorker extends SwingWorker<Void, EvolutionaryEvent>{
    ExecutorService es;
    /**
     * @return the outerEventProcessor
     */
    public Consumer<EvolutionaryEvent> getOuterEventProcessor() {
        return outerEventProcessor;
    }

    /**
     * @param outerEventProcessor the outerEventProcessor to set
     */
    public void setOuterEventProcessor(Consumer<EvolutionaryEvent> outerEventProcessor) {
        this.outerEventProcessor = outerEventProcessor;
    }
    PtpNeuralNetworkTrueTableEvolutionaryEnvironment environment;
    EvolutionaryProcessInfoEditor editor;
    List<Pair<String, Consumer<EvolutionaryEvent>>> consumers = new ArrayList<>();
    List<Pair<String, EvolutionaryProcesSubscriptor>> subscriptors = new ArrayList<>();
    private Consumer<EvolutionaryEvent> outerEventProcessor;

    public void createEnvironment(int populationSize, 
            PtpNeuralNetworkConfiguration nnConfig, 
            Float[][] environmentInputSet, 
            Float[][] environmentOutputSet, 
            List<String> vitalAdvantages, 
            List<String> reproductiveAdvantages, 
            List<String> propertiesToFollow, 
            OptimizationMethod optimizationMethod, 
            double minSurvivalRate,
            boolean keepProgenyLines) {
        PtpVectorNeuralNetworkTrueTableEvolutionaryEnvironmentBuilder environmentBuilder = new PtpVectorNeuralNetworkTrueTableEvolutionaryEnvironmentBuilder();
        environmentBuilder.setPopulationSize(populationSize)
                .setViAdv(vitalAdvantages)
                .setRepAdv(vitalAdvantages)
                .setEnvironmentInputSet(environmentInputSet)
                .setEnvironmentOutputSet(environmentOutputSet)
                .setNnConfig(nnConfig)
                .setNnPropertiesToFollow(propertiesToFollow)
                .setOptimizationMethod(optimizationMethod)
                .setPerformaceName("performance");
        environment = environmentBuilder.build();
        
//        PtpVectorNeuralNetwork[] population = new PtpVectorNeuralNetwork[populationSize];
//        
//        for(int i=0; i<populationSize; i++){
//            PtpVectorNeuralNetwork net = new PtpVectorNeuralNetwork();
//            PtpVectorNeuralNetworkRandomInitializer.initialize(net, nnConfig);
//            population[i] = net;
//        }
//        environment = new PtpNeuralNetworkTrueTableEvolutionaryEnvironment(
//                population, 
//                new PtpNeuralNetworkTrueTableGlobalCalculator(vitalAdvantages, reproductiveAdvantages, environmentInputSet, environmentOutputSet), 
//                new PtpVectorNeuralNetworkMutationProcessor(), 
//                propertiesToFollow,
//                optimizationMethod,
//                minSurvivalRate,
//                keepProgenyLines);
//        environment.getMutationProcessor().setConnectionMutationRate(nnConfig.getConnectionMutationRate());
//        environment.getMutationProcessor().setDisconnectionMutationRate(nnConfig.getDisconnectionMutationRate());
//        environment.getMutationProcessor().setMaxThresholdExchangeFactorValue(nnConfig.getMaxThresholdExchangeFactorValue());
//        environment.getMutationProcessor().setMaxWeightExchangevalue(nnConfig.getMaxWeightExchangevalue());
//        environment.getMutationProcessor().setReceiverNeuronNumberMutationRate(nnConfig.getReceiverNeuronNumberMutationRate());
//        environment.getMutationProcessor().setResponseNeuronNumberMutationRate(nnConfig.getResponseNeuronNumberMutationRate());
//        environment.getMutationProcessor().setThresholdMutationRate(nnConfig.getThresholdMutationRate());
//        environment.getMutationProcessor().setWeightsMutationRate(nnConfig.getWeightsMutationRate());
//        environment.getMutationProcessor().setInputContributionrobability(nnConfig.getInputContributionrobability());
        if(environmentInputSet.length!=environmentOutputSet.length){
            throw new RuntimeException("The input array must be the same length as the output array");
        }
    }
    
    public void setProcessingParamenters(float averagePerformanceForStopping, float desiredPerformance, int maxTimes) {
        EvolutionaryProcessEventStore eventStore = new EvolutionaryProcessEventStore("WORKER-STORE-1", Math.max(100, maxTimes/1000));
        environment.init(averagePerformanceForStopping, desiredPerformance, maxTimes); 
        environment.setEvolutionaryProcessObserver(eventStore);
        this.editor = new EvolutionaryProcessInfoEditor(eventStore);
        if(!consumers.isEmpty()){
            for(Pair<String, Consumer<EvolutionaryEvent>> p: consumers){
                this.editor.subscribe(p.getFirst(), p.getSecond());
            }
            consumers.clear();
        }
        if(!subscriptors.isEmpty()){
            for(Pair<String, EvolutionaryProcesSubscriptor> p: subscriptors){
                this.editor.subscribe(p.getFirst(), p.getSecond());
            }
            subscriptors.clear();
        }
        this.editor.subscribe(FinishedEvolutionaryCycleEvent.eventType, this::publish);
    }
    
    public void addEventHandler(String tyop, EvolutionaryProcesSubscriptor subs){
        if(editor==null){
            subscriptors.add(new Pair<>(tyop, subs));
        }else{
            editor.subscribe(tyop, subs);
        }
    }
    
    public void addEventHandler(String tyop, Consumer<EvolutionaryEvent> c){
        if(editor!=null){
            editor.subscribe(tyop, c);
        }else{
            consumers.add(new Pair<>(tyop, c));
        }
    }
    
    @Override
    protected Void doInBackground() {
        //F1
        try {
//            System.out.println(String.format("STARTING -EDITOR-TH(%s)", Thread.currentThread().getName()));
            this.editor.start("Editor of Evolution Events");
            environment.process();
        } catch (Exception e) {
            environment.getObserver().updateEvent(new ErrorOnProcessEvolution(e));
        }
//        System.out.println(String.format("FINISH - EvolutionaryEventSwingWorker::doInBackground (TH:%s)", Thread.currentThread().getName()));
        return null;
    }
    
    public void stopProcess(boolean value){
        environment.stopProcess(value);
    }
    
    public void finishProcess(){
        environment.finishProcess();
    }
    
    @Override
    public void done(){
        super.done();
        this.editor.finish();
        this.editor.join();
        this.editor.getStore().close("WORKER-STORE-1");
        try {
            this.get();
            this.cancel(true);
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(EvolutionaryEventSwingWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void process(List<EvolutionaryEvent> chunks) {
        super.process(chunks); 
        for(EvolutionaryEvent ev: chunks){
            getOuterEventProcessor().accept(ev);
        }
    }
    
    public void executeWorker(){
        es = Executors.newFixedThreadPool(1);
        es.submit(this);
    }
    
    public ExecutorService getExecutor(){
        return es;
    }
    
}
