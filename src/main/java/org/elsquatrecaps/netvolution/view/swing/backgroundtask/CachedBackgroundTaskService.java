package org.elsquatrecaps.netvolution.view.swing.backgroundtask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.netvolution.view.swing.EvolutionProcessFrame;

/**
 *
 * @author josep
 */
public class CachedBackgroundTaskService<R, W extends RunnableFuture<R>> implements BackgroundTaskService<W> {
    private ExecutorService executorService;
    private W worker;

    @Override
    public void execute(W worker) {
        forcePrevoiousCancel();
        executorService = Executors.newCachedThreadPool();
        this.worker =  worker;
        executorService.execute(this.worker);
    }
    
    private void forcePrevoiousCancel(){
        if(worker!=null){
            for(int i=0; i<10 && !worker.isDone(); i++){                    
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EvolutionProcessFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(!worker.isCancelled()){
                worker.cancel(true);
            }        
        }
        if(executorService!=null){
            executorService.shutdownNow();
        }
    }

    public W getWorker(){
        return worker;
    }

    @Override
    public void shutdown() {
        try {
            if(!worker.isCancelled()){
                worker.get();
                worker.cancel(true);
            }
            executorService.shutdownNow();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(CachedBackgroundTaskService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void shutdownNow() {
        executorService.shutdownNow();
    }
}