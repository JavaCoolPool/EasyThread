package com.thread.asyc.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thread.asyc.core.work.AsynWork;

public class DefaultErrorAsynWorkHandler extends ErrorAsynWorkHandler {

	  private final static Log log = LogFactory.getLog(DefaultErrorAsynWorkHandler.class);
	  
	    @Override
	    public void addErrorWork(AsynWork asynWork, Throwable throwable) {
	        log.error(String.format(" ThreadName :%s  run is error, error info: ",asynWork.getThreadName()) , throwable);
	    }

}
