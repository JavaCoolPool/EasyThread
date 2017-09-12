package com.thread.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BeanUtils {
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}

		if (obj instanceof String) {
			return ((String) obj).trim().length() == 0;
		}

		if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		}

		if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		}

		return false;
	}

	public static boolean isEmpty(Object[] array) {
		if (array == null || array.length == 0) {
			return true;
		}

		return false;
	}

	  public static <T> List<T> intersect(List<T> ls, List<T> ls2) { 
		   if(ls==null || ls2 ==null){
			  return null;
		   }
		    List<T> list = new ArrayList<T>(ls.size()); 
		    list.addAll(ls); 
		    list.retainAll(ls2); 
	        return list; 
	    }

	    public static <T>  List<T>  union(List<T> ls, List<T> ls2) { 
	      if(ls == null) {
	    	  return ls2;
	      }else if(ls2 == null){
	    	  return ls;
	      }else{
	    	  List<T> list = new ArrayList<T>(ls.size()); 
			   list.addAll(ls);  
			   list.addAll(ls2); 
		       return list; 
	      }
	    }

	    public static <T> List<T>  diff(List<T> ls, List<T> ls2) {
	    	if(ls == null){
	    		return null;
	    	}else if(ls2 == null){
	    		return ls;
	    	}else{
	    		List<T> list = new ArrayList<T>(ls.size()); 
	  		    list.addAll(ls);  
	  		    list.removeAll(ls2); 
	  	        return list; 
	    	}
	    } 
	    
	    public static <T>  List<T>  filterExclude(List<T> ls,String filterProperty,String filterValue) { 
		      if(CollectionUtils.isEmpty(ls)){
		    	  return ls;
		      }
			  Iterator<T> it = ls.iterator();
			  while(it.hasNext()){
			  	try {
					T bean = it.next();
					if(isEmpty(bean)){
						continue;
					}
					Object value = PropertyUtils.getNestedProperty(bean, filterProperty);
					if(value!=null && filterValue.equals(value)){
						it.remove();
					}
				} catch (Exception e) {
					logger.error( " filterExclude exception ", e);
					throw new RuntimeException( " filterExclude exception ", e);
				}  
			  }
			  return ls;
		    }
	    
	    
	    public static <T>  List<T>  filterIn(List<T> ls,String filterProperty,String filterValue) {
		      if(CollectionUtils.isEmpty(ls)){
		    	  return ls;
		      }
		      List<T> list = new ArrayList<T>();
		      Iterator<T> it = ls.iterator();
			  while(it.hasNext()){
			  	try {
					T bean = it.next();
					if(isEmpty(bean)){
						continue;
					}
					Object value = PropertyUtils.getNestedProperty(bean, filterProperty);
					if((StringUtils.isBlank(filterValue) && isEmpty(value))
							|| StringUtils.isNotBlank(filterValue) && filterValue.equals(value)){
						list.add(bean);
					}
				} catch (Exception e) {
					logger.error( " filterIn exception ", e);
					throw new RuntimeException( " filterIn exception ", e);
				}
			  }
			return list;
		}

	    public static <T,V>  List<T>  filterIn(List<T> ls,String filterProperty,V... filterValues) {
		      if(CollectionUtils.isEmpty(ls)){
		    	  return ls;
		      }
		      List<T> list = new ArrayList<T>();
		      Iterator<T> it = ls.iterator();
			  while(it.hasNext()){
			  	try {
					T bean = it.next();
					if(isEmpty(bean)){
						continue;
					}
					Object value = PropertyUtils.getNestedProperty(bean, filterProperty);
					for(V filterValue:filterValues){
						if((isEmpty(filterValue) && isEmpty(value))
								|| !isEmpty(filterValue) && filterValue.equals(value)){
							list.add(bean);
						}
					}
				} catch (Exception e) {
					logger.error( " filterIn exception ", e);
					throw new RuntimeException( " filterIn exception ", e);
				}
			  }
			return list;
		}


	public static <T>  List<T>  filterNull(List<T> ls) {
		if (CollectionUtils.isEmpty(ls)) {
			return ls;
		}
		List<T> list =new CopyOnWriteArrayList(ls);
		for(T t:list){
			if (t == null || isEmpty(t)) {
				list.remove(t);
			}
		}

		return list;
	}
	    
	public static <T>  List<T>  filterNullOrZero(List<T> ls,String... filterProperties) {
	    	if(CollectionUtils.isEmpty(ls)){
	    		return ls;
	    	}
			List<T> list =new CopyOnWriteArrayList(ls);
			for(T bean:list){
				try {
					if (filterProperties == null) {
						break;
					}
					boolean flag = true;
					for (String property : filterProperties) {
						Object value = PropertyUtils.getNestedProperty(bean, property);
						if (value == null || (value instanceof BigDecimal && NumberUtils.isEqualsToZero((BigDecimal) value))) {
							flag = flag && true;
						} else {
							flag = flag && false;
						}
					}
					if (flag) {
						list.remove(bean);
					}
				}catch (Exception e) {
					logger.error( " filter exception ", e);
					throw new RuntimeException( " filter exception ", e);
				}
			}
	    	return list;
	    }

	public static <T>  T getTopBeanPropertyList(final List<T> beanList,String propertyname,boolean asc){
		if(CollectionUtils.isEmpty(beanList)){
			return null;
		}
		sortBy(beanList,propertyname,asc);
		return beanList.get(0);
	}

	public static <T> List<T> sortBy(final List<T> beanList,String propertyname,boolean asc){
		if(CollectionUtils.isEmpty(beanList)){
			return null;
		}
		Collections.sort(beanList,new Comparator<T>(){
			@Override
			public int compare(T o1, T o2) {
				try {
					Object value1 = PropertyUtils.getNestedProperty(o1, propertyname);
					Object value2 = PropertyUtils.getNestedProperty(o2, propertyname);
					if(value1 instanceof Comparable &&  value2 instanceof Comparable){
						if(asc){
							return ((Comparable)value1).compareTo((Comparable)value2);
						}else {
							return ((Comparable)value2).compareTo((Comparable)value1);
						}
					}
					return 0;
				} catch (Exception e) {
					logger.error(" sortBy exception ", e);
					throw new RuntimeException(" sortBy exception ", e);
				}
			}
		});
		return beanList;
	}

	public static BigDecimal getSumBeanPropertyList(final Collection beanList,String propertyname){
		BigDecimal sum = BigDecimal.ZERO;
		List<BigDecimal>  list = getBeanPropertyList(beanList,BigDecimal.class,propertyname,false);
		if(CollectionUtils.isEmpty(list)){
			return sum;
		}
		for(BigDecimal t:list){
			sum = sum.add(t==null?BigDecimal.ZERO:t);
		}
		return sum;
	}

		public static <T> List<T> getBeanPropertyList(final Collection beanList, Class<T> clazz, String propertyname, boolean unique) {
			return getBeanPropertyList(beanList, propertyname, unique);
		}

		public static <T>  Map  beanListToMap(final Collection<T> beanList, String keyproperty) {
			Map result = new HashMap();
			for (Object bean : beanList) {
				try {
					Object key = PropertyUtils.getNestedProperty(bean, keyproperty);
					if (key != null)
						result.put(key, bean);
				} catch (Exception e) {
					logger.error( " getBeanPropertyList exception ", e);
					throw new RuntimeException( " getBeanPropertyList  exception ", e);

				}
			}
			return result;
		}
		
		public static <T> List<T> getBeanPropertyList(final Collection beanList, String propertyname, boolean unique) {
			List<T> result = new ArrayList<T>();
			if(CollectionUtils.isEmpty(beanList)){
				return result;
			}
			for (Object bean : beanList) {
				try {
					T pv = (T) PropertyUtils.getProperty(bean, propertyname);
					if (pv != null && (!unique || !result.contains(pv)))
						result.add(pv);
				} catch (Exception e) {
					logger.error( " getBeanPropertyList exception ", e);
					throw new RuntimeException( " getBeanPropertyList exception ", e);
				}
			}
			return result;
		}
		

		/**
		 * 根据property的值将beanList分组
		 * 
		 * @param beanList
		 * @param property
		 * @return
		 */
		public static Map groupBeanList(final Collection beanList, String property) {
			return groupBeanList(beanList, property, null);
		}

		/**
		 * 根据property的值将beanList分组, null作为单独一组，key 为nullKey
		 * @param beanList
		 * @param property
		 * @param nullKey
		 * @return
		 */
		public static Map groupBeanList(final Collection beanList, String property, Object nullKey) {
			Map<Object, List> result = new LinkedHashMap<Object, List>();
			for (Object bean : beanList) {
				try {
					Object keyvalue = PropertyUtils.getNestedProperty(bean, property);
					if (keyvalue == null)
						keyvalue = nullKey;
					if (keyvalue != null) {
						List tmpList = result.get(keyvalue);
						if (tmpList == null) {
							tmpList = new ArrayList();
							result.put(keyvalue, tmpList);
						}
						tmpList.add(bean);
					}
				} catch (Exception e) {
					logger.error( " groupBeanList exception ", e);
					throw new RuntimeException( " groupBeanList exception ", e);
				}
			}
			return result;
		}
		
		public static   Map<String,BigDecimal> merge(final Map<String,BigDecimal> a,final Map<String,BigDecimal> b){
			Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
			if(a!=null && b!=null){
				List<String> aKeys = new ArrayList<String>(a.keySet());
				List<String> bKeys = new ArrayList<String>(b.keySet());
				List<String> keys = intersect(aKeys, bKeys);
				for(String key:keys){
					BigDecimal aValue = a.get(key);
					BigDecimal bValue = b.get(key);
					BigDecimal value = (aValue!=null? aValue:BigDecimal.ZERO).add(bValue!=null?bValue:BigDecimal.ZERO);   
					map.put(key, value);
				}
			}else if(a==null){
				return b;
			}else{
				return a;
			}
			return map;
		}
		
		public static <T> boolean domainEq(T source,T target){
		       boolean rv = true;
		       if(source==null && target==null){
		    	   return true;
		       }else if(source==null || target==null){
	    		   logger.info(String.format(" one of the value is null [source:%s,target:%s]",source,target));
	    		   return false;
		       }
		       List<String> ignores = Arrays.asList("class","bytes","empty","id","createdAt","updatedAt","createdBy","updatedBy");  
		       PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(source.getClass());
		       for(PropertyDescriptor pd:pds){
		       try {
		     	   	   if(ignores.contains(pd.getName())){ //忽略
		    	   		   continue;
		    	   	   }
			    	   Object sourceValue = PropertyUtils.getProperty(source,pd.getName());
			    	   Object targetValue = PropertyUtils.getProperty(target,pd.getName());
			    	   if(sourceValue==null && targetValue==null){
			    		   return true;
			    	   }else  if(sourceValue==null || targetValue==null){
			    		   logger.info(String.format(" one of the value is null [property:%s,source:%s,target:%s]",pd.getName(),sourceValue,targetValue));
			    		   return false;
			    	   }
			    	   if(isDefineWrapClass(pd.getPropertyType())){
			    		   rv =  rv  && domainEq(sourceValue,targetValue);
			    	   }else{
			    		   if(List.class.isAssignableFrom(pd.getPropertyType())){
			    			   List sourceList = ((List)sourceValue);
			    			   List targetList = ((List)targetValue);
			    			   if(sourceList.size()==targetList.size()){
			    				   for(int i=0;i<sourceList.size();i++){
			    					   rv =  rv && domainEq(sourceList.get(i),targetList.get(i));
			        			   }
			    			   }else{
					    		   logger.info(String.format(" list size not equals [property:%s,source:%s,target:%s]",pd.getName(),sourceList.size(),targetList.size()));
			    				   return false;
			    			   }
			    			 
			    		   }else{
			    			   boolean compareValue = sourceValue.equals(targetValue);
			    			   if(!compareValue){
					    		   logger.warn(String.format(" value compare not equals [property:%s,source:%s,target:%s,compareValue:%s]",pd.getName(),sourceValue,targetValue,compareValue));
			    			   }
			    			   rv =  rv  && compareValue;
			    		   }
			    	   }
		 
				} catch (Throwable e) {
					logger.error( " domainEq exception ", e);
				   throw new RuntimeException( " domainEq exception ", e);

			   }
		       }  
		       return rv;
			}

			public  static <T> Map<String,Object>  bean2Map(T t){
				Map<String,Object> map ;
				try {
					map = PropertyUtils.describe(t);
				} catch (Exception e) {
					logger.error(String.format(" bean2Map Exception [t:%s]", JSON.toJSONString(t)), e);
					throw new RuntimeException(String.format(" bean2Map Exception [t:%s]", JSON.toJSONString(t)), e);
				}
				return map;
			}

			public static <T> boolean domainEq(T source,T target,String... properties){
				if(source==null && target==null){
					return true;
				}else if(source==null || target==null){
					logger.info(String.format(" one of the value is null [source:%s,target:%s]",source,target));
					return false;
				}
				Map<String,Object> sourceMap = bean2Map(source);
				Map<String,Object> targetMap = bean2Map(target);
				for(String property:properties){
					Object sourceValue = sourceMap.get(property);
					Object targetValue = targetMap.get(property);
					if(sourceValue==null && targetValue==null){
						continue;
					}else if(sourceValue==null || targetValue==null){
						return false;
					}else if(sourceValue instanceof Comparable){
						if(((Comparable)sourceValue).compareTo(targetValue)!=0){
							return false;
						}
					}else if(!sourceValue.equals(targetValue)){
						return false;
					}
				}
				return true;
			}

	public static void copyProperties(Object dest, Object orig) {
		try {
			PropertyUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			logger.error( " copyProperties exception ", e);
			throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest),JSON.toJSONString(orig)), e);
		}
	}

	public static void copyProperties(Object dest, Object orig,boolean ignoreNull) {
		try {
			if(ignoreNull){
				org.springframework.beans.BeanUtils.copyProperties(orig,dest,getNullPropertyNames(orig));
			}else{
				org.springframework.beans.BeanUtils.copyProperties(orig,dest);
			}
		} catch (Exception e) {
			logger.error( " copyProperties ignoreNull exception ", e);
			throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest),JSON.toJSONString(orig)), e);
		}
	}

	public static void copyProperties(Object dest, Object orig,String... ignoreProperties) {
		try {
			org.springframework.beans.BeanUtils.copyProperties(orig,dest,ignoreProperties);
		} catch (Exception e) {
			logger.error( " copyProperties ignoreNull exception ", e);
			throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest),JSON.toJSONString(orig)), e);
		}
	}

	public static String[] getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if(isEmpty(srcValue)){
				emptyNames.add(pd.getName());
			}
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
		  
	    //基本类型与包装类判断,String不是基本类型包装类
		  public static boolean isWrapClass(Class clz) { 
		        try { 
		           return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
	 	        } catch (Exception e) { 
					logger.error( " isWrapClass exception ", e);
		            return false; 
		        } 
		    } 
		  
	    //如何判断一个类型是Java本身的类型，还是用户自定义的类型
		  public static boolean isDefineWrapClass(Class clz) { 
			  try { 
				  return clz == null || clz.getClassLoader() != null;    
			  } catch (Exception e) { 
					logger.error( " isDefineWrapClass exception ", e);
				  return false; 
			  } 
		  }
		
		public static <T> Map beanListToMap(final Collection<T> beanList, String keyproperty, String valueproperty, boolean ignoreNull) {
			Map result = new HashMap();
			if(CollectionUtils.isEmpty(beanList)){
				return result;
			}
			for (Object bean : beanList) {
				try {
					Object key = PropertyUtils.getNestedProperty(bean, keyproperty);
					Object value = PropertyUtils.getNestedProperty(bean, valueproperty);
					if (key == null)
						continue;
					if (value != null)
						result.put(key, value);
					else if (!ignoreNull)
						result.put(key, value);
				} catch (Exception e) {
				}
			}
			return result;
		}

	/**
	 * 交集 补集 根据K
	 * @param map1
	 * @param map2
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K,V> Map<DiffType,Map<K,V>>  diffKey(final Map<K,V> map1, final Map<K,V> map2){
		HashMap<DiffType,Map<K,V>> hashMap = Maps.newHashMap();
		if(MapUtils.isEmpty(map1)){
			hashMap.put(DiffType.LEFT,new HashMap<K, V>());
			hashMap.put(DiffType.INTERSECTION,new HashMap<K, V>());
			hashMap.put(DiffType.RIGHT,map2);
			return hashMap;
		}else if(MapUtils.isEmpty(map2)){
			hashMap.put(DiffType.LEFT,map1);
			hashMap.put(DiffType.INTERSECTION,new HashMap<K, V>());
			hashMap.put(DiffType.RIGHT,new HashMap<K, V>());
			return hashMap;
		}

		MapDifference differenceMap = Maps.difference(map1, map2);
		Map entriesDiffering = differenceMap.entriesDiffering();
		Map entriesOnlyOnLeft = differenceMap.entriesOnlyOnLeft();
		Map entriesOnlyOnRight = differenceMap.entriesOnlyOnRight();
		Map entriesInCommon = differenceMap.entriesInCommon();
		hashMap.put(DiffType.DIFFERING,entriesDiffering);
	 	hashMap.put(DiffType.LEFT,entriesOnlyOnLeft);
		hashMap.put(DiffType.RIGHT,entriesOnlyOnRight);
		hashMap.put(DiffType.INTERSECTION,entriesInCommon);

		return hashMap;

	}

	/**
	 *
	 * get target method
	 *
	 * @param clazz
	 * @param pararm
	 * @param methodName
	 * @return
	 */
	public static Method getTargetMethod(Class clazz, Object[] pararm,
										 String methodName) {
 		if(pararm==null || pararm.length==0){
			return ReflectionUtils.findMethod(clazz,methodName);
		}
		List<Class<?>> paramClasses = new ArrayList<Class<?>>();
		for(Object p: pararm){
			paramClasses.add(p.getClass());
		}
		Method  method= ReflectionUtils.findMethod(clazz,methodName,paramClasses.toArray(new Class<?>[paramClasses.size()]));
		if(method!=null){
			return method;
		}
		return getTargetMethodByParam(clazz,pararm,methodName);
	}

	public static Method getTargetMethodByParam(Class clazz, Object[] pararm,
										 String methodName) {
		List<Method> mList = new ArrayList<Method>();

		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {//add methodName some item
				method.setAccessible(true);
				mList.add(method);
			}
		}

		if (mList.size() == 0)
			return null;

		if (mList.size() == 1)
			return mList.get(0);

		Method result = null;
		for (Method m : mList) {
			Class[] classes = m.getParameterTypes();
			if (classes.length == 0 && (pararm == null || pararm.length == 0))
				return m;
			if (pararm == null || pararm.length == 0) {
				return null;
			}
			if (classes.length != pararm.length) {
				continue;
			}
			boolean flag = true;
			for (int i = 0; i < classes.length; i++) {
				Class clzss = classes[i];
				Class paramClzss = pararm[i].getClass();
				if (!clzss.toString().equals(paramClzss.toString())) {
					flag =false;
					break;
				}
			}

			if(flag){
				result = m;
				break;
			}
		}

		return result;

	}

	/**
	 *get method key
	 *
	 * @param clazz
	 * @param pararm
	 * @param methodName
	 * @return
	 */
	public static String getClassMethodKey(Class clazz, Object[] pararm,
										   String methodName) {

		StringBuilder sb = new StringBuilder();
		sb.append(clazz.toString());
		sb.append(".").append(methodName);
		if (pararm != null && pararm.length > 0) {
			for (Object obj : pararm) {
				sb.append("-").append(obj.getClass().toString());
			}
		}
		return sb.toString();

	}

	public  static <T> void checkNull(String message,Class<T> clazz,T t,String... properties) {
		Assert.notNull(t,String.format(message+" check [className=%s] is null",clazz.getSimpleName()));
		for(String property:properties) {
			try {
				Object value = PropertyUtils.getNestedProperty(t, property);
			} catch (Exception e) {
				logger.error(" checkNull getNestedProperty  exception ", e);
				throw new RuntimeException( " checkNull getNestedProperty  exception ", e);
			}
		}
	}

	public  static <T> String[] checkNull(T t,Class<T> clazz,String... properties) {
		if(isEmpty(t)){
			return new String[]{clazz.getSimpleName()};
		}
		return ReflectionUtil.hasFieldsEmpty(t,properties);
	}


	public  static <T> boolean checkIsNull(T t,Class<T> clazz,String... properties) {
		if(isEmpty(t)){
			return true;
		}
		return !isEmpty(ReflectionUtil.hasFieldsEmpty(t,properties));
	}

	public  static <T> T getMin(List<T> list,String propertyValue) {
		if(StringUtils.isBlank(propertyValue) || CollectionUtils.isEmpty(list)){
			logger.warn(" getMin propertyValue or ts is null ");
			return null;
		}
		sortBy(list,propertyValue,true);
		return list.get(0);
	}

	public static  <K> List<List<K>> splitList(List<K> param,int batchSize) {
		List<List<K>>list=new ArrayList<List<K>>();
		if(CollectionUtils.isEmpty(param)){
			return  list;
		}
		int minIndex=0,size=param.size();
		int maxIndex=batchSize<size?batchSize:size;
		while (minIndex<size){
			List<K> tmpList= new ArrayList<K>();
			tmpList.addAll(param.subList(minIndex,maxIndex));
			list.add(tmpList);
			minIndex=maxIndex;
			maxIndex=maxIndex+batchSize<size?maxIndex+batchSize:size;
		}
		return list;
	}

}
