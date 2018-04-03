package com.aikya.konnek2.call.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lenovo on 08-11-2017.
 */

public abstract class QMBaseService {


    private QMServiceManagerListener serviceManagerListener;
    private QMBaseCache cache;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    protected BlockingQueue<Runnable> threadQueue;
    protected ThreadPoolExecutor threadPool;

    public void init(QMBaseCache cache){
        this.cache = cache;
        initThreads();
        serviceWillStart();
    }

    private void initThreads() {
        threadQueue = new LinkedBlockingQueue<>();
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }

    protected abstract void serviceWillStart();
}
