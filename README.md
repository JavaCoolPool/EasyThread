# EasyThread
基于ThreadPoolExcutor异步交易框架,兼容Spring3.0+,业务逻辑仅需要在for循环中调用一下即可使用异步线程池
业务系统依赖jar包后，业务系统spring配置注册<bean id="threadService" class="com.thread.asyc.spring.AsynServiceFactoryBean">

# DEMO:payService接口中有一个payment(Pay pay)支付接口，此接口因为调用银行网关容易等待超时,假如有1000笔交易，这个时候用线程池效率高很多。

@Autowired
private  threadService threadService

@Autowired
private  PayService payService

Pay pay=new Pay();
pay.setBankCard("1029301")


threadService.addThread(payService, "payment", new Object[]{pay} )
