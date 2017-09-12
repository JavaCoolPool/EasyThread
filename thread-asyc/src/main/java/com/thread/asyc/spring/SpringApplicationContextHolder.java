package com.thread.asyc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContextHolder implements ApplicationContextAware {

	private static ApplicationContext context = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringApplicationContextHolder.context=applicationContext;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	/**
	 * get spring bean with beanName and Class Type
	 * @param <T>
	 * @param object
	 * @param beanName
	 * @return
	 */
	public static <T> T getBean(Class<T> object, String beanName) {
		return (T) context.getBean(beanName);
	}
	
	
}
