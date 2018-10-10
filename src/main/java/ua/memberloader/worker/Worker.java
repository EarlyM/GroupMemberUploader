package ua.memberloader.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {

    private ExecutorService executor;

    public Worker(){
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void addJob(Runnable job){
        executor.submit(job);
    }

    public void shutdown(){
        if(executor != null){
            executor.shutdown();
        }
    }

    public boolean isShatdown(){
        return executor.isShutdown();
    }
}
