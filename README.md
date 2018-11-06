微服务容错保护

我们在实践微服务架构时，通常会将业务拆分成一个个微服务，微服务之间通过网络进行通信，进行互相调用，造成了微服务之间存在依赖关系。我们知道由于网络原因或者自身的原因，服务并不能保证服务的100%可用，如果单个服务出现问题，调用这个服务就会出现网络延迟甚至调用失败，而调用失败又会造成用户刷新页面并再次尝试调用，再加上其它服务调用，从而增加了服务器的负载，导致服务瘫痪，最终甚至会导致整个服务“雪崩”。

Netflix为解决这个问题根据断路器模式创建了一个名为Hystrix的库。“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

![](https://upload-images.jianshu.io/upload_images/1488771-241d72d00579b0f6.png?imageMogr2/auto-orient/)

当然，在请求失败频率较低的情况下，Hystrix还是会直接把故障返回给客户端。只有当失败次数达到阈值（默认在20秒内失败5次）时，断路器打开并且不进行后续通信，而是直接返回备选(FallBack)响应。

微服务容错是为了保护消费方。

引入依赖。

```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
            <version>1.4.6.RELEASE</version>
</dependency>
```
启用断路器。

`@EnableCircuitBreaker`

然后创建FallBack。

```
@Component
public class UserServiceFallBack implements UserService {
    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }

    @Override
    public User findOne(Integer id) {
        return new User(id, "NOT FOUND", "NOT FOUND");
    }
}
```
然后让UserService具有容错能力。

`@FeignClient(name = "SERVICE-HELLO", fallback = UserServiceFallBack.class)`

然后让Feign启用Hystrix。

`feign.hystrix.enabled=true`

然后停掉服务提供。然后回调就有作用了。

```
{
	id: 1,
	username: "NOT FOUND",
	password: "NOT FOUND"
}
```

在不使用Feign时使用Hystrix。

其实Hystrix提供了两个对象来支持回退处理：HystrixCommand和HystrixObservableCommand。

断路器原理。

1. 请求封装。通过对服务调用的封装，将每个命令在一个独立的线程中进行执行。
2. 跳闸机制。当某个服务错误率超过一定阈值时，Hystrix可以自动或者手动进行服务跳闸，停止向该服务请求一段时间。
3. 资源隔离。Hystrix为每个服务创建了一个小型线程池，如果线程池满了，那么发往该服务的请求就会立即被拒绝，而不是排队等候，从而加速服务失败的判定。
4. 服务监控。Hystrix可以近乎实时地监控运行指标和配置的变化，例如对请求的成功、失败、超时以及拒绝等。
5. 回退机制。当请求失败、超时、被拒绝、或当断路器打开时，执行相应的回退逻辑。
6. 自我修复。当断路器打开一段时间后，Hystrix会处于'半开状态'，断路器会允许一个请求尝试对服务进行请求，如果该服务可以调用成功，则关闭断路器，否则将继续保持断路器打开。

Hystrix监控。

Hystrix除了实现服务容错之外，还提供了对服务请求的监控：每秒执行的请求数、成功数等。开启Hystrix的监控非常简单，一个是添加`spring-cloud-starter-hystrix`，这个在之前的示例中以及添加。二是添加`spring-boot-starter-actuator`，能够让/hystrix-stream端点可以获取到Hystrix的监控数据。

启动服务后，我们在浏览器中输入: `http://locaohost:8080/hystrix.stream`就会看到一些json数据。

如果访问不了，需要在启动类中配置`ServletRegistrationBean`。

```
@Bean
public ServletRegistrationBean getServlet() {
    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
    ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
    registrationBean.setLoadOnStartup(1);
    registrationBean.addUrlMappings("/hystrix.stream");
    registrationBean.setName("HystrixMetricsStreamServlet");
    return registrationBean;
}	
```    
Hystrix Dashboard。

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
    <version>1.4.3.RELEASE</version>
</dependency>
```
然后`@EnableHystrixDashboard`开启dashboard。

然后`http://localhost:8080/hystrix`。

然后输入`http://localhost:8080/hystrix.stream`，点击Monitor Stream。

监控的意义如下。
![](https://upload-images.jianshu.io/upload_images/1488771-1ab31087c6694797.png?imageMogr2/auto-orient/)

在Dashboard首页时，我们知道Hystrix Dashboard支持三种监控方式:

* 默认集群监控: 通过http://turbine-hostname:port/turbine.stream
开启，实现对默认集群的监控;
* 指定集群监控: 通过http://turbine-hostname:port/turbine.stream?cluster=[clusterName]开启，实现对指定clusterName集群的监控;
* 单机应用监控: 通过http://hystrix-app:port/hystrix.stream开启，实现对某个服务实例的监控。

在上面的示例中我们演示的第三种方式，至于如何继承Turbine实现对集群的监控，我们将在后续篇幅中进行讲解。


