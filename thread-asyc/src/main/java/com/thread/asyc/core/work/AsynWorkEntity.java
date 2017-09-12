package com.thread.asyc.core.work;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
	
import com.thread.asyc.core.WorkWeight;
import com.thread.asyc.core.callback.AsynCallBack;
import com.thread.util.BeanUtils;

public class AsynWorkEntity implements AsynWork,Serializable {

	 private final static Map<String, Method> methodCacheMap = new ConcurrentHashMap<String, Method>();
	    private Object                           target;
	    private String                           method;
	    private Object[]                         params;
	    private AsynCallBack                     asynCallBack;
	    private WorkWeight                       workWeight     = WorkWeight.MIDDLE;
	    
	    
	    public AsynWorkEntity(Object target, String method) {
	    			this(target,method,null);
		}

		public AsynWorkEntity(Object target, String method,Object[]  params) {
	        this(target,method,params,null,null);
	    }

	    public AsynWorkEntity(Object target, String method, Object[] params, AsynCallBack asynCallBack, WorkWeight workWeight) {
	        if(target==null||method==null){
	            throw new IllegalArgumentException("target or method  is null");
	        }
	        this.target = target;
	        this.method = method;
	        this.params = params;
	        this.asynCallBack = asynCallBack;
	        if(workWeight!=null){
	            this.workWeight = workWeight;
	        }
	    }
	
	@Override
	public AsynCallBack getAsynCallBack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getThreadName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsynCallBack call() throws Exception {
		  if (target == null)
	            throw new RuntimeException("target object is null");

	        Class clazz = target.getClass();

	        String methodKey = BeanUtils.getClassMethodKey(clazz, params, method);
	        
	        Method targetMethod = methodCacheMap.get(methodKey);
	        
	        if (targetMethod == null) {
	            targetMethod = BeanUtils.getTargetMethod(clazz, params, method);
	            if (targetMethod != null) {
	                methodCacheMap.put(methodKey, targetMethod);
	            }
	        }

	        if (targetMethod == null) {
	            throw new IllegalArgumentException("target method is null");
	        }

	        Object result = targetMethod.invoke(target, params);
	        if (asynCallBack != null) {//if call back is not null
	            asynCallBack.setInokeResult(result);
	        }
	        return asynCallBack;
	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
