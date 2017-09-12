package com.thread.asyc.core.handler;

import com.thread.asyc.core.work.AsynWork;
import com.thread.asyc.service.ThreadService;

public abstract class WorkQueueFullHandler implements AsynHandler {

	protected ThreadService  asynService;
	
	public abstract void process() ;

	public abstract boolean addAsynWork(AsynWork  asynWork);

	public abstract int getSize();

	public ThreadService getAsynService() {
		return asynService;
	}

	public void setAsynService(ThreadService asynService) {
		this.asynService = asynService;
	}

}
