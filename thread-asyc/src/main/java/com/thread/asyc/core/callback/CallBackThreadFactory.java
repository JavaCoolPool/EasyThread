package com.thread.asyc.core.callback;

import java.util.concurrent.ThreadFactory;

public class CallBackThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		Thread thread=new Thread();
		return thread;
	}
}
