package com.thread.asyc.core;

public enum WorkWeight {
		LOW(1),
	    MIDDLE(5),
	    HIGH(9);

	    private int value;

	    WorkWeight(int value) {
	        this.value = value;
	    }
	    
	    public int getValue(){
	        return value;
	    }
}
