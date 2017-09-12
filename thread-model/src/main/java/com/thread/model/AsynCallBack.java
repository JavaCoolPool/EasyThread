package com.thread.model;

import java.io.Serializable;

public abstract class AsynCallBack<T> implements Runnable,Serializable {

	private static final long serialVersionUID = -4124531128122529103L;
	
	protected T result;

	@Override
	public void run() {
		callback(result);
	}

	public final void setInokeResult(T result) {
		// set method inoke result
		this.result = result;
	}

	/**
	 * execute callback
	 */
	public abstract void callback(T t);

}
