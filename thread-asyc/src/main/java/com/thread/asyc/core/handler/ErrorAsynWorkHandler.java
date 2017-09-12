package com.thread.asyc.core.handler;

import com.thread.asyc.core.work.AsynWork;

public abstract class ErrorAsynWorkHandler implements AsynHandler  {

	@Override
	public void process() {
		
	}

	 public abstract void addErrorWork(AsynWork asynWork,Throwable throwable);
}
