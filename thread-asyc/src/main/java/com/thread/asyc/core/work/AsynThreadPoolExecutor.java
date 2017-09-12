package com.thread.asyc.core.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsynThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Log log = LogFactory.getLog(AsynThreadPoolExecutor.class);
	private final ThreadLocal<Long> watch = new ThreadLocal();
	private AtomicLong executeWorkNum;
	private static Long asynWaringTimeMillis = 5000L;

	public AsynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler,
			AtomicLong executeWorkNum) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.executeWorkNum = executeWorkNum;
	}

	public AsynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, AtomicLong executeWorkNum) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);

		this.executeWorkNum = executeWorkNum;
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		try {
			long elasped = System.currentTimeMillis() - watch.get();
			log.debug(String.format("ThreadName:%s execute asynTask running ecasped(millis):%s ",
					Thread.currentThread().getName(), elasped));
			if (elasped > asynWaringTimeMillis) {
				log.warn(String.format("ThreadName:%s execute asynTask running ecasped(millis):%s ",
						Thread.currentThread().getName(), elasped));
			}
		} finally {
			watch.remove();
			super.afterExecute(r, t);
		}
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		executeWorkNum.incrementAndGet();
		watch.set(System.currentTimeMillis());
	}
	
	@Override
	protected void terminated() {
		super.terminated();
	}

}
