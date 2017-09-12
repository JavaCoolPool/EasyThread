package com.thread.asyc.service;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thread.asyc.core.AsynApplicationContext;
import com.thread.asyc.core.WorkProcessor;
import com.thread.asyc.core.WorkWeight;
import com.thread.asyc.core.callback.AsynCallBack;
import com.thread.asyc.core.callback.CallBackRejectedExecutionHandler;
import com.thread.asyc.core.callback.CallBackThreadFactory;
import com.thread.asyc.core.callback.CallBackThreadPoolExecutor;
import com.thread.asyc.core.handler.AsynServiceHandler;
import com.thread.asyc.core.handler.ErrorAsynWorkHandler;
import com.thread.asyc.core.handler.WorkQueueFullHandler;
import com.thread.asyc.core.work.AsynThreadFactory;
import com.thread.asyc.core.work.AsynThreadPoolExecutor;
import com.thread.asyc.core.work.AsynWork;
import com.thread.asyc.core.work.AsynWorkEntity;
import com.thread.asyc.core.work.AsynWorkRejectedExecutionHandler;
import com.thread.asyc.exception.AsynException;
import com.thread.util.NumberUtils;

public class ThreadServiceImpl extends AsynApplicationContext implements ThreadService {

	private static final Log log = LogFactory.getLog(ThreadServiceImpl.class);
	// service run flag
	private static volatile boolean run = false;
	// status info stringbuffer
	private static StringBuilder infoSb = new StringBuilder();

	public final static ConcurrentHashMap<String, Object> targetCacheMap = new ConcurrentHashMap<String, Object>();

	private static Lock lock = new ReentrantLock();

	private ThreadServiceImpl() {
		this(maxCacheWork, addWorkWaitTime, work_thread_num, callback_thread_num, closeServiceWaitTime);
	}

	private ThreadServiceImpl(int maxCacheWork, long addWorkWaitTime, int workThreadNum, int callBackThreadNum,
			long closeServiceWaitTime) {
		super(maxCacheWork, addWorkWaitTime, workThreadNum, callBackThreadNum, closeServiceWaitTime);
	}

	@Override
	public void addThread(Object tagerObject, String method) {
		addThread(tagerObject, method, null);
	}

	@Override
	public void addThread(Object tagerObject, String method, Object[] params) {
		addThread(tagerObject, method, params, null);
	}

	@Override
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack) {
		addThread(tagerObject, method, params, asynCallBack, DEFAULT_WORK_WEIGHT);
	}

	@Override
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack,
			WorkWeight weight) {
		addThread(tagerObject, method, params, asynCallBack, DEFAULT_WORK_WEIGHT, false);
	}

	@Override
	public void addThread(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack,
			WorkWeight weight, boolean cache) {

		if (tagerObject == null || method == null) {
			throw new IllegalArgumentException("target name is null or  target method name is null");
		}

		Object target = null;
		if (tagerObject.getClass().isAssignableFrom(String.class)) {// tagerObject
																	// form
																	// string to
																	// spirng
																	// name

			addWorkWithSpring(tagerObject.toString(), method, params, asynCallBack, weight);
			return;

		} else if (tagerObject instanceof Class) {// tagerObject form class to
													// targetclass
			String classKey = ((Class) tagerObject).getSimpleName();
			if (cache) {
				target = targetCacheMap.get(classKey);
				if (target == null) {
					target = newObject((Class) tagerObject);
					targetCacheMap.put(classKey, target);
				}
			} else {
				target = newObject((Class) tagerObject);
			}
		} else {// tagerObject is a entity object
			target = tagerObject;
		}

		if (target == null) {
			throw new IllegalArgumentException("target object is null");
		}
		AsynWork anycWork = new AsynWorkEntity(target, method, params, asynCallBack, weight);

		addAsynWork(anycWork);

	}

	@Override
	public void addWorkWithSpring(String target, String method, Object[] params, AsynCallBack asynCallBack,
			WorkWeight weight) {

		if (target == null || method == null) {
			throw new IllegalArgumentException("target name is null or  target method name is null or weight less 0");
		}
		// get spring bean
		Object bean = getApplicationContext().getBean(target);

		if (bean == null)
			throw new IllegalArgumentException("spring bean is null");

		AsynWork anycWork = new AsynWorkEntity(bean, method, params, asynCallBack, weight);

		addAsynWork(anycWork);

	}

	/**
	 * class instantiation object
	 * 
	 * @param clzss
	 * @return
	 */
	private Object newObject(Class clzss) {
		try {
			Constructor constructor = clzss.getConstructor();
			if (constructor == null) {
				throw new IllegalArgumentException("target not have default constructor function");
			}
			// Instance target object
			return clzss.newInstance();
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	@Override
	public void addAsynWork(AsynWork asynWork) {
		if (!run) {// if asyn service is stop or no start!
			throw new AsynException("asyn service is stop or no start!");
		}
		if (asynWork == null) {
			throw new IllegalArgumentException("asynWork is null");
		}
		try {
			// get acquire wait addWorkWaitTime
			if (semaphore.tryAcquire(addWorkWaitTime, TimeUnit.MILLISECONDS)) {
				WorkProcessor workProcessor = new WorkProcessor(asynWork, this);
				// asyn work execute
				workExecutor.execute(workProcessor);
				totalWork.incrementAndGet();
			} else {
				log.warn("work queue is full,add work to cache queue");
				if (workQueueFullHandler != null) {
					workQueueFullHandler.addAsynWork(asynWork);
				} else {
					log.error(String.format("work queue is full,not cache queue rejectedException asynWorkName:%s",
							asynWork.getThreadName()));
					throw new RejectedExecutionException(
							String.format("work queue is full,not cache queue rejectedException asynWorkName:%s",
									asynWork.getThreadName()));
				}
			}
		} catch (InterruptedException e) {
			log.error(e);
		}
	}

	@Override
	public Map<String, Long> getRunStatMap() {
		if (run) {
			statMap.clear();
			statMap.put("total", totalWork.get());
			statMap.put("execute", executeWorkNum.get());
			statMap.put("callback", callBackNum.get());
		}
		return statMap;
	}

	@Override
	public String getRunStatInfo() {
		if (run) {
			infoSb.delete(0, infoSb.length());
			infoSb.append("total asyn work:").append(totalWork.get()).append("\t");
			infoSb.append(",excute asyn work:").append(executeWorkNum.get()).append("\t");
			infoSb.append(",callback asyn result:").append(callBackNum.get()).append("\t");
			infoSb.append(",workQueue size:").append(workQueue.size()).append("\t");
			infoSb.append(",callBackQueue size:").append(callBackQueue.size()).append("\t");
			if (workQueueFullHandler != null) {
				infoSb.append(",cacheQueue size:").append(workQueueFullHandler.getSize()).append("\t");
			}
			infoSb.append(",complete ratio:").append(NumberUtils.formatPercentage(callBackNum.get(), totalWork.get()))
					.append("\t");
		}
		return infoSb.toString();
	}

	@Override
	public void init() {

		if (!run) {
			run = true;
			// init work execute queue
			workQueue = new PriorityBlockingQueue<Runnable>(maxCacheWork);

			if (workQueueFullHandler != null) {
				workExecutor = new AsynThreadPoolExecutor(work_thread_num, work_thread_num, 0L, TimeUnit.MILLISECONDS,
						workQueue, new AsynThreadFactory(), new AsynWorkRejectedExecutionHandler(workQueueFullHandler),
						executeWorkNum);
			} else {
				workExecutor = new AsynThreadPoolExecutor(work_thread_num, work_thread_num, 0L, TimeUnit.MILLISECONDS,
						workQueue, new AsynThreadFactory(), executeWorkNum);
			}

			// init callback queue
			callBackQueue = new LinkedBlockingQueue<Runnable>();

			callBackExecutor = new CallBackThreadPoolExecutor(callback_thread_num, callback_thread_num, 0L,
					TimeUnit.MILLISECONDS, callBackQueue, new CallBackThreadFactory(),
					new CallBackRejectedExecutionHandler(), callBackNum);

			if (serviceHandler != null) {
				serviceHandler.setServiceStat(ThreadService.SERVICE_INIT);
				serviceHandler.setAsynService(this);
				serviceHandler.process();
			}

			if (workQueueFullHandler != null) {
				workQueueFullHandler.process();
			}
			/**
			 * 在这个线程的jvm中增加一个关闭的钩子，当jvm关闭的时候，
			 * 会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子， 当系统执行完这些钩子后，jvm才会关闭。
			 * 所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
			 */
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					close(closeServiceWaitTime);
				}
			});
		}

	}

	@Override
	public void close() {
		this.close(closeServiceWaitTime);
	}

	@Override
	public void close(long waitTime) {
		if (run) {
			run = false;
			try {
				workExecutor.awaitTermination(waitTime, TimeUnit.MILLISECONDS);
				// workExecutor is wait sometime,so callBackExecutor wait time
				// is 0
				callBackExecutor.awaitTermination(0, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.error(e);
			}
			workExecutor.shutdown();
			callBackExecutor.shutdown();
			if (serviceHandler != null) {
				serviceHandler.setAsynWorkQueue(workQueue);
				serviceHandler.setCallBackQueue(callBackQueue);
				serviceHandler.setServiceStat(ThreadService.SERVICE_CLOSE);
				serviceHandler.process();
			}
		}
	}

	@Override
	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler) {
		if (run)
			throw new IllegalArgumentException("asyn running");
		if (workQueueFullHandler == null)
			throw new IllegalArgumentException("workQueueFullHandler is null");
		this.workQueueFullHandler = workQueueFullHandler;
		this.workQueueFullHandler.setAsynService(this);
	}

	@Override
	public void setServiceHandler(AsynServiceHandler serviceHandler) {
		if (run)
			throw new IllegalArgumentException("asyn running");
		if (serviceHandler == null)
			throw new IllegalArgumentException("closeHander is null");
		this.serviceHandler = serviceHandler;
	}

	@Override
	public void setErrorAsynWorkHandler(ErrorAsynWorkHandler errorAsynWorkHandler) {
		if (run)
			throw new IllegalArgumentException("asyn running");
		if (errorAsynWorkHandler == null)
			throw new IllegalArgumentException("errorAsynWorkHandler is null");
		this.errorAsynWorkHandler = errorAsynWorkHandler;
	}

	@Override
	public int getMaxCacheWork() {
		return maxCacheWork;
	}

	public static ThreadService getService(int maxCacheWork, long addWorkWaitTime, int workThreadNum,
			int callbackThreadNum, long closeServiceWaitTime) {
		lock.lock();
		try {
			if (instance == null) {
				instance = new ThreadServiceImpl(maxCacheWork, addWorkWaitTime, workThreadNum, callbackThreadNum,
						closeServiceWaitTime);
			}
		} finally {
			lock.unlock();
		}
		return instance;
	}

}
