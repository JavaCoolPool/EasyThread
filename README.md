# EasyThread
基于ThreadPoolExcutor异步交易框架,兼容Spring3.0+,业务逻辑仅需要在for循环中调用一下即可使用异步线程池
业务系统依赖jar包后，业务系统spring配置注册<bean id="threadService" class="com.thread.asyc.spring.AsynServiceFactoryBean">


