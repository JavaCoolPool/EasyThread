package com.thread.asyc.core.callback;

import java.io.Serializable;

public abstract class AsynCallBack<T> implements Runnable, Serializable {

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
