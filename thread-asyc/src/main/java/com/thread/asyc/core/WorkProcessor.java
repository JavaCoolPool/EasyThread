package com.thread.asyc.core;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thread.asyc.core.callback.AsynCallBack;
import com.thread.asyc.core.work.AsynWork;
import com.thread.asyc.exception.AsynException;

public class WorkProcessor implements Serializable,Runnable ,Comparable<WorkProcessor> {
	
	private static final long serialVersionUID = -87845465748777754L;
	
	private static final Log     log = LogFactory.getLog(WorkProcessor.class);
    private AsynWork             asynWork;
    private AsynApplicationContext applicationContext;
    
	public WorkProcessor(AsynWork asynWork, AsynApplicationContext applicationContext) {
		this.asynWork = asynWork;
		this.applicationContext = applicationContext;
	}

	public AsynWork getAsynWork() {
		return asynWork;
	}

	public void setAsynWork(AsynWork asynWork) {
		this.asynWork = asynWork;
	}

	public AsynApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(AsynApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public int compareTo(WorkProcessor o) {
		return o.getAsynWork().getWeight() - this.asynWork.getWeight();
	}
	
	@Override
	public void run() {
	       Thread currentThread = Thread.currentThread();
	        if (asynWork.getThreadName() != null) {
	            setName(currentThread, asynWork.getThreadName());
	        }
	        AsynCallBack result;
	        try {
	            //asyn work execute
	            result = asynWork.call();
	            
	            if (result != null) {//execute callback
	            	applicationContext.getCallBackExecutor().execute(result);
	            }
	            
	        } catch (Throwable e) {
	            log.error(String.format("asynWork:%s call exception ",asynWork),e);
	            //if execute asyn work is error,errorAsynWorkHandler disposal
	            if (applicationContext.getErrorAsynWorkHandler() != null) {
	            	applicationContext.getErrorAsynWorkHandler().addErrorWork(asynWork,e);
	            }
	            throw new AsynException(e);
	        }finally{
	        	applicationContext.getSemaphore().release();
	        }
		}
	
    private void setName(Thread thread, String name) {
        try {
            thread.setName(name);
        } catch (SecurityException se) {
            log.error(se);
        }
    }
}
