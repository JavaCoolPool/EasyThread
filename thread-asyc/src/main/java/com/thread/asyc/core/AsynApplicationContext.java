package com.thread.asyc.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.thread.asyc.core.handler.AsynServiceHandler;
import com.thread.asyc.core.handler.ErrorAsynWorkHandler;
import com.thread.asyc.core.handler.WorkQueueFullHandler;
import com.thread.asyc.service.ThreadServiceImpl;

public class AsynApplicationContext implements Serializable, ApplicationContextAware {

	private static final long serialVersionUID = -7526773890653459767L;

	// asyn work default work weight
	protected static final WorkWeight DEFAULT_WORK_WEIGHT = WorkWeight.MIDDLE;

	protected final static int CPU_NUMBER = Runtime.getRuntime().availableProcessors();

	protected static ExecutorService workExecutor = null;

	protected static ExecutorService callBackExecutor = null;

	protected static ApplicationContext applicationContext = null;

	// call back block queue
	protected static BlockingQueue<Runnable> callBackQueue = null;

	// work queue
	protected static BlockingQueue<Runnable> workQueue = null;

	// status map
	protected static Map<String, Long> statMap = new HashMap<String, Long>(3);

	protected WorkQueueFullHandler workQueueFullHandler = null;

	protected AsynServiceHandler serviceHandler = null;

	protected ErrorAsynWorkHandler errorAsynWorkHandler = null;

	// default work queue cache size
	protected static int maxCacheWork = 300;

	// default add work wait time
	protected static long addWorkWaitTime = 0L;

	// work thread pool size
	protected static int work_thread_num = (CPU_NUMBER / 2) + 1;

	// callback thread pool size
	protected static int callback_thread_num = CPU_NUMBER / 2;

	// close service wait time
	protected static long closeServiceWaitTime = 2 * 60 * 1000;

	protected Semaphore semaphore = null;

	protected static ThreadServiceImpl instance = null;

	protected final static AtomicLong totalWork = new AtomicLong(0);

	protected final static AtomicLong executeWorkNum = new AtomicLong(0);

	protected final static AtomicLong callBackNum = new AtomicLong(0);

	public AsynApplicationContext(int maxCacheWork, long addWorkWaitTime, int workThreadNum, int callBackThreadNum,
			long closeServiceWaitTime) {
		AsynApplicationContext.maxCacheWork = maxCacheWork;
		AsynApplicationContext.addWorkWaitTime = addWorkWaitTime;
		AsynApplicationContext.work_thread_num = workThreadNum;
		AsynApplicationContext.callback_thread_num = callBackThreadNum;
		AsynApplicationContext.closeServiceWaitTime = closeServiceWaitTime;
		this.semaphore = new Semaphore(maxCacheWork);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AsynApplicationContext.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static ExecutorService getWorkExecutor() {
		return workExecutor;
	}

	public static void setWorkExecutor(ExecutorService workExecutor) {
		AsynApplicationContext.workExecutor = workExecutor;
	}

	public static ExecutorService getCallBackExecutor() {
		return callBackExecutor;
	}

	public static void setCallBackExecutor(ExecutorService callBackExecutor) {
		AsynApplicationContext.callBackExecutor = callBackExecutor;
	}

	public static BlockingQueue<Runnable> getCallBackQueue() {
		return callBackQueue;
	}

	public static void setCallBackQueue(BlockingQueue<Runnable> callBackQueue) {
		AsynApplicationContext.callBackQueue = callBackQueue;
	}

	public static BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}

	public static void setWorkQueue(BlockingQueue<Runnable> workQueue) {
		AsynApplicationContext.workQueue = workQueue;
	}

	public WorkQueueFullHandler getWorkQueueFullHandler() {
		return workQueueFullHandler;
	}

	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
		this.workQueueFullHandler = workQueueFullHandler;
	}

	public AsynServiceHandler getServiceHandler() {
		return serviceHandler;
	}

	public void setServiceHandler(AsynServiceHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
	}

	public ErrorAsynWorkHandler getErrorAsynWorkHandler() {
		return errorAsynWorkHandler;
	}

	public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler) {
		this.errorAsynWorkHandler = errorAsynWorkHandler;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}
}
