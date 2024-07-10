package org.elsquatrecaps.netvolution.view.swing.backgroundtask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import org.elsquatrecaps.rsjcb.netvolution.events.ErrorOnProcessEvolution;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryEvent;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcesSubscriptor;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcessEventStore;
import org.elsquatrecaps.rsjcb.netvolution.events.EvolutionaryProcessInfoEditor;
import org.elsquatrecaps.rsjcb.netvolution.events.FinishedEvolutionaryCycleEvent;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.calculators.PtpNeuralNetworkTrueTableGlobalCalculator;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.PtpNeuralNetworkTrueTableEvolutionaryEnvironment;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.optimization.OptimizationMethod;
import org.elsquatrecaps.rsjcb.netvolution.evolutiveprocess.optimization.SurviveOptimizationMethodValues;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpNeuralNetworkConfiguration;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetwork;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetworkMutationProcessor;
import org.elsquatrecaps.rsjcb.netvolution.neuralnetwork.PtpVectorNeuralNetworkRandomInitializer;
import org.elsquatrecaps.utilities.tools.Pair;

/**
 *
 * @author josep
 */
public class EvolutionaryEventSwingProcessor implements RunnableFuture<Void>{
    private static final CachedBackgroundTaskService<Void, EvolutionaryEventSwingProcessor> workerService = new CachedBackgroundTaskService<>();
;
    private boolean cancelled=false;
    private boolean done = false;
    
    
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
            double survivalRate,
            boolean keepProgenyLines) {
        
        environment = new PtpNeuralNetworkTrueTableEvolutionaryEnvironment(
                new PtpVectorNeuralNetwork[populationSize], 
                new PtpNeuralNetworkTrueTableGlobalCalculator(vitalAdvantages, reproductiveAdvantages, environmentInputSet, environmentOutputSet), 
                new PtpVectorNeuralNetworkMutationProcessor(), 
                propertiesToFollow,
               optimizationMethod,
               survivalRate,
               keepProgenyLines);
        for(int i=0; i<populationSize; i++){
            PtpVectorNeuralNetwork net = new PtpVectorNeuralNetwork();
            PtpVectorNeuralNetworkRandomInitializer.initialize(net, nnConfig);
            environment.getPopulation()[i] = net;
        }
        environment.getMutationProcessor().setConnectionMutationRate(nnConfig.getConnectionMutationRate());
        environment.getMutationProcessor().setDisconnectionMutationRate(nnConfig.getDisconnectionMutationRate());
        environment.getMutationProcessor().setMaxThresholdExchangeFactorValue(nnConfig.getMaxThresholdExchangeFactorValue());
        environment.getMutationProcessor().setMaxWeightExchangevalue(nnConfig.getMaxWeightExchangevalue());
        environment.getMutationProcessor().setReceiverNeuronNumberMutationRate(nnConfig.getReceiverNeuronNumberMutationRate());
        environment.getMutationProcessor().setResponseNeuronNumberMutationRate(nnConfig.getResponseNeuronNumberMutationRate());
        environment.getMutationProcessor().setThresholdMutationRate(nnConfig.getThresholdMutationRate());
        environment.getMutationProcessor().setWeightsMutationRate(nnConfig.getWeightsMutationRate());
        environment.getMutationProcessor().setInputContributionrobability(nnConfig.getInputContributionrobability());
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
    
    protected final void publish(EvolutionaryEvent... chunks) {
        SwingUtilities.invokeLater(() -> {
            process(chunks);
        });
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
    
    protected Void doInBackground() {
        try {
            this.editor.start("Editor of Evolution Events");
            environment.process();
        } catch (Exception e) {
            environment.getObserver().updateEvent(new ErrorOnProcessEvolution(e));
        }
        return null;
    }
    
    public void stopProcess(boolean value){
        environment.stopProcess(value);
    }
    
    public void finishProcess(){
        environment.finishProcess();
    }
    
    public void done(){
        done=true;
        this.editor.finish();
        this.editor.join();        
        this.editor.getStore().close("\"WORKER-STORE-1\"");
    }
    
    protected void process(EvolutionaryEvent... chunks) {
        for(EvolutionaryEvent ev: chunks){
            getOuterEventProcessor().accept(ev);
        }
    }
    
    protected void process(List<EvolutionaryEvent> chunks) {
        for(EvolutionaryEvent ev: chunks){
            getOuterEventProcessor().accept(ev);
        }
    }
    
    public void execute(){
        workerService.execute(this);
    }

    public void run() {
        cancelled=false;
        done = false;
        doInBackground();
        done();
    }

    @Override
    public boolean cancel(boolean bln) {
        environment.finishProcess();
        cancelled=true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {        
        return null;
    }

    @Override
    public Void get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

}
