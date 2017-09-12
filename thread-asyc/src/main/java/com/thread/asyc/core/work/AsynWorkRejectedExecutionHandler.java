package com.thread.asyc.core.work;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.thread.asyc.core.WorkProcessor;
import com.thread.asyc.core.handler.WorkQueueFullHandler;

public final class AsynWorkRejectedExecutionHandler implements RejectedExecutionHandler {

    private WorkQueueFullHandler workQueueFullHandler;
	
	public AsynWorkRejectedExecutionHandler(WorkQueueFullHandler workQueueFullHandler) {
		super();
		this.workQueueFullHandler = workQueueFullHandler;
	}
	
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        AsynWork asynWork = ((WorkProcessor) r).getAsynWork();
        workQueueFullHandler.addAsynWork(asynWork);
	}
}
