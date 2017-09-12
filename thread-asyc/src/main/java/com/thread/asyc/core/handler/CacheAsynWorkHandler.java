package com.thread.asyc.core.handler;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thread.asyc.core.work.AsynWork;

public final class CacheAsynWorkHandler extends WorkQueueFullHandler {

	private final static Log log = LogFactory.getLog(CacheAsynWorkHandler.class);

	protected static int maxCacheWork = 300;

	private BlockingQueue<AsynWork> cacheLink = null;

	public CacheAsynWorkHandler() {
		cacheLink = new ArrayBlockingQueue<>(maxCacheWork);
	}

	public CacheAsynWorkHandler(int max) {
		cacheLink = new ArrayBlockingQueue<>(max);
	}
	
	@Override
	public void process() {
	
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
			      while (true) {
	                    Map<String, Long> runstatMap = asynService.getRunStatMap();
	                    boolean isFull =   runstatMap.get("total") - runstatMap.get("execute") >= asynService.getMaxCacheWork();
	                    if (cacheLink.isEmpty() || isFull) {
	                        try {
	                            log.debug(String.format("work queue is full or cache queue is empty,wait 2s RunStatInfo:%s ",asynService.getRunStatInfo()));
	                            Thread.sleep(2000);
	                        } catch (InterruptedException e) {
	                            Thread.currentThread().interrupt();
	                        }
	                        continue;
	                    }
	                    AsynWork asynWork;
	                    try {
	                        asynWork = cacheLink.take();
	                        asynService.addAsynWork(asynWork);
	                    } catch (InterruptedException e) {
	                    }
	                }
			}
		};
	}
	
	@Override
	public boolean addAsynWork(AsynWork asynWork) {
			boolean offer = cacheLink.offer(asynWork);
			if(offer==false){
				   log.info(String.format("cacheLink is full ",asynService.getRunStatInfo()));
			}
		return offer;
	}
	
	@Override
	public int getSize() {
		return cacheLink!=null?cacheLink.size():0;
	}
}
