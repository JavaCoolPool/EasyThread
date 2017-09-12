package com.thread.asyc.service;

import java.util.Map;

import com.thread.asyc.core.WorkWeight;
import com.thread.asyc.core.callback.AsynCallBack;
import com.thread.asyc.core.handler.AsynServiceHandler;
import com.thread.asyc.core.handler.ErrorAsynWorkHandler;
import com.thread.asyc.core.handler.WorkQueueFullHandler;
import com.thread.asyc.core.work.AsynWork;

public interface ThreadService {
	
	/**
	 * service init status
	 */
	public final static  int SERVICE_INIT = 0;
	
	/**
	 * service close status
	 */
	public final static int SERVICE_CLOSE = 1;
	
	
	public void addThread(Object tagerObject, String method);
	
	public void addThread(Object tagerObject, String method, Object[] params);
	
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack);
	
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack, WorkWeight weight);
	
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack, WorkWeight weight,
              boolean cache);
	
	

	public void addAsynWork(AsynWork asynWork);
	
	  /**
     * get run stat map
     * 
     * @return
     */
    public Map<String, Long> getRunStatMap();

    /**
     * get run stat string
     * 
     * @return
     */
    public String getRunStatInfo();

    /**
     * start service
     */
    public void init();

    /**
     * close service
     * @ wait time
     */
    public void close(long waitTime);
    
    /**
     * close service
     */
    public void close();
    /**
     * add work cache work queue
     *
     * @param workQueueFullHandler
     */
    public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler);

    /**
     * set close service handler
     *
     * @param serviceHandler
     */
    public void setServiceHandler(AsynServiceHandler serviceHandler);

    /**
     * set error asyn work handler
     *
     * @param errorAsynWorkHandler
     */
    public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler);


    public int getMaxCacheWork();

    public void addWorkWithSpring(String target, String method, Object[] params, AsynCallBack asynCallBack, WorkWeight weight);

}
