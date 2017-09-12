package com.thread.asyc.core.work;

import java.io.Serializable;

import com.thread.asyc.core.callback.AsynCallBack;

public interface AsynWork extends Serializable {

	/**
	 * get asyn work callbakck
	 * @return
	 */
	public AsynCallBack getAsynCallBack();
	
	/**
	 * get this thread work name
	 * @return
	 */
    public String getThreadName();
    
    /**
     * call target method
     * @return
     */
    public AsynCallBack call() throws Exception;
    
    /**
     * get asyn work weight
     * @return
     */
    public int getWeight();
}
