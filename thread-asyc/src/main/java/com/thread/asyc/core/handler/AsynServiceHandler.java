package com.thread.asyc.core.handler;

import java.util.concurrent.BlockingQueue;

import com.thread.asyc.service.ThreadService;

public abstract class AsynServiceHandler implements AsynHandler {

	private volatile int serviceStat = ThreadService.SERVICE_INIT;

    protected BlockingQueue<Runnable>     asynWorkQueue;

    protected BlockingQueue<Runnable> callBackQueue;
	
    protected ThreadService asynService;
    
	public void setServiceStat(int serviceStat) {
		this.serviceStat = serviceStat;
	}

	public void setAsynWorkQueue(BlockingQueue<Runnable> asynWorkQueue) {
		this.asynWorkQueue = asynWorkQueue;
	}

	public void setCallBackQueue(BlockingQueue<Runnable> callBackQueue) {
		this.callBackQueue = callBackQueue;
	}

	@Override
	public void process() {
		if(serviceStat==ThreadService.SERVICE_INIT){
    		this.init();
    	}else{
    		this.destroy();
    	}
	}
	
	public abstract void init();
		
	public abstract void destroy();

	public ThreadService getAsynService() {
		return asynService;
	}

	public void setAsynService(ThreadService asynService) {
		this.asynService = asynService;
	}
}
