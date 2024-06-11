package org.elsquatrecaps.netvolution.view.swing.backgroundtask;

/**
 *
 * @author josep
 */
public interface BackgroundTaskService<W>{
    /**
     * Executes SwingWorker task.
     * @param worker Worker to be executed.
     */
    void execute(W worker);

    void shutdown();
    
    void shutdownNow();
}