package com.thread.asyc.core.work;

import java.util.concurrent.ThreadFactory;

public class AsynThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		
	    Thread thread = new Thread(r);
        thread.setDaemon(false);
		
        return thread;
	}

}
