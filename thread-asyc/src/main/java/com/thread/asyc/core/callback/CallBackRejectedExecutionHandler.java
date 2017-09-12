package com.thread.asyc.core.callback;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CallBackRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Log log = LogFactory.getLog(CallBackRejectedExecutionHandler.class);
	
    @Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    		AsynCallBack asynCallBack = (AsynCallBack) r;
	        log.warn(r + " not execute Thread:"+Thread.currentThread().getName());
	}
}
