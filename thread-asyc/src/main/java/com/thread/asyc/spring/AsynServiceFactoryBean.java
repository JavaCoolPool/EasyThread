package com.thread.asyc.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.thread.asyc.core.handler.AsynServiceHandler;
import com.thread.asyc.core.handler.DefaultErrorAsynWorkHandler;
import com.thread.asyc.core.handler.ErrorAsynWorkHandler;
import com.thread.asyc.core.handler.WorkQueueFullHandler;
import com.thread.asyc.service.ThreadService;
import com.thread.asyc.service.ThreadServiceImpl;

public class AsynServiceFactoryBean implements FactoryBean<ThreadService>, InitializingBean {

	private final static Log        log                  = LogFactory.getLog(AsynServiceFactoryBean.class);

    private final static int        CPU_NUMBER           = Runtime.getRuntime().availableProcessors();

    // default work queue cache size
    private int                     maxCacheWork         = 300;

    // default add work wait time
    private long                    addWorkWaitTime      = Long.MAX_VALUE;

    // work thread pool size
    private int                     workThreadNum        = (CPU_NUMBER / 2) + 1;

    // callback thread pool size
    private int                     callbackThreadNum    = CPU_NUMBER / 2;
    
    //close service wait time
    private long                    closeServiceWaitTime = 60 * 1000;

    private WorkQueueFullHandler    workQueueFullHandler;

    private ErrorAsynWorkHandler    errorAsynWorkHandler;

    private AsynServiceHandler asynServiceCloseHandler;

    private ThreadService threadService;

    
	public ThreadService getAsynService() {
		return threadService;
	}
	
	public void setAsynService(ThreadServiceImpl asynService) {
		this.threadService = asynService;
	}

	public WorkQueueFullHandler getWorkQueueFullHandler() {
		return workQueueFullHandler;
	}

	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
		this.workQueueFullHandler = workQueueFullHandler;
	}

	public ErrorAsynWorkHandler getErrorAsynWorkHandler() {
		return errorAsynWorkHandler;
	}

	public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler) {
		this.errorAsynWorkHandler = errorAsynWorkHandler;
	}

	public AsynServiceHandler getAsynServiceCloseHandler() {
		return asynServiceCloseHandler;
	}

	public void setAsynServiceCloseHandler(AsynServiceHandler asynServiceCloseHandler) {
		this.asynServiceCloseHandler = asynServiceCloseHandler;
	}

	public void setMaxCacheWork(int maxCacheWork) {
		this.maxCacheWork = maxCacheWork;
	}

	public void setAddWorkWaitTime(long addWorkWaitTime) {
		this.addWorkWaitTime = addWorkWaitTime;
	}

	public void setWorkThreadNum(int workThreadNum) {
		this.workThreadNum = workThreadNum;
	}

	public void setCallbackThreadNum(int callbackThreadNum) {
		this.callbackThreadNum = callbackThreadNum;
	}

	public void setCloseServiceWaitTime(long closeServiceWaitTime) {
		this.closeServiceWaitTime = closeServiceWaitTime;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		 threadService = ThreadServiceImpl.getService(maxCacheWork, addWorkWaitTime, workThreadNum,
	                callbackThreadNum,closeServiceWaitTime);
	        //set some handler
	        if (workQueueFullHandler != null) {
	            threadService.setWorkQueueFullHandler(workQueueFullHandler);
	        }
	        if (errorAsynWorkHandler != null) {
	            threadService.setErrorAsynWorkHandler(errorAsynWorkHandler);
	        }else{
	            threadService.setErrorAsynWorkHandler(new DefaultErrorAsynWorkHandler());
	        }
	        if (asynServiceCloseHandler != null) {
	            threadService.setServiceHandler(asynServiceCloseHandler);
	        }
	        threadService.init();
	}

	@Override
	public ThreadService getObject() throws Exception {
		   return threadService;
	}

	@Override
	public Class<?> getObjectType() {
		return   threadService!=null?threadService.getClass():ThreadService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
