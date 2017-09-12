package com.thread.model;

import java.io.Serializable;

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
