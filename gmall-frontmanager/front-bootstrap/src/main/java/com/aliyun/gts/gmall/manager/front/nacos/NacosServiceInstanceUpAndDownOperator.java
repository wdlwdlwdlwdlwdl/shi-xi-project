//package com.aliyun.gts.gmall.manager.front.nacos;
//
//import com.alibaba.cloud.nacos.registry.NacosRegistration;
//import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.common.lifecycle.Closeable;
//import com.alibaba.nacos.common.utils.StringUtils;
//import com.alibaba.nacos.common.utils.ThreadUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//
///**
// * nacos注册问题
// * 解决nacos 经常招不到的服务的问题
// * 是否开始配置
// */
//@Component
//public class NacosServiceInstanceUpAndDownOperator  implements ApplicationRunner, Closeable {
//
//    protected Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    /**
//     * nacos服务实例上线
//     */
//    private static final String OPERATOR_UP = "UP";
//    /**
//     * nacos服务实例下线
//     */
//    private static final String OPERATOR_DOWN = "DOWN";
//
//    @Value("${nacos.customer.status:1}")
//    private String nacosOpen;
//
//    @Autowired
//    private NacosServiceRegistry nacosServiceRegistry;
//
//    @Autowired
//    private NacosRegistration nacosRegistration;
//
//    private ScheduledExecutorService executorService;
//
//    @PostConstruct
//    public void init() {
//        if (StringUtils.isEmpty(nacosOpen)) {
//            return;
//        }
//        int poolSize = 1;
//        this.executorService = new ScheduledThreadPoolExecutor(poolSize, r -> {
//            Thread thread = new Thread(r);
//            thread.setDaemon(true);
//            thread.setName("NacosServiceInstanceUpAndDownOperator");
//            return thread;
//        });
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        if (StringUtils.isEmpty(nacosOpen)) {
//            return;
//        }
//        long delay_down = 5000L;  //下线任务延迟
//        long delay_up = 12000L;   // 上线任务延迟
//        this.executorService.schedule(new InstanceDownAndUpTask(nacosServiceRegistry, nacosRegistration, OPERATOR_DOWN), delay_down, TimeUnit.MILLISECONDS);
//        this.executorService.schedule(new InstanceDownAndUpTask(nacosServiceRegistry, nacosRegistration, OPERATOR_UP), delay_up, TimeUnit.MILLISECONDS);
//    }
//
//    @Override
//    public void shutdown() throws NacosException {
//        if (StringUtils.isEmpty(nacosOpen)) {
//            return;
//        }
//        ThreadUtils.shutdownThreadPool(executorService, logger);
//    }
//
//    /**
//     * 服务实例上下线任务
//     */
//    class InstanceDownAndUpTask implements Runnable {
//
//        private NacosServiceRegistry nacosServiceRegistry;
//
//        private NacosRegistration nacosRegistration;
//
//        //更新服务实例的状态 ：UP 、DOWN
//        private String nacosServiceInstanceOperator;
//
//        InstanceDownAndUpTask(NacosServiceRegistry nacosServiceRegistry, NacosRegistration nacosRegistration, String nacosServiceInstanceOperator) {
//            this.nacosServiceRegistry = nacosServiceRegistry;
//            this.nacosRegistration = nacosRegistration;
//            this.nacosServiceInstanceOperator = nacosServiceInstanceOperator;
//        }
//
//        @Override
//        public void run() {
//            logger.info("===更新nacos服务实例的状态to：{}===start=", nacosServiceInstanceOperator);
//            this.nacosServiceRegistry.setStatus(nacosRegistration, nacosServiceInstanceOperator);
//            logger.info("===更新nacos服务实例的状态to：{}===end=", nacosServiceInstanceOperator);
//            //上线后，关闭线程池
//            if (NacosServiceInstanceUpAndDownOperator.OPERATOR_UP.equals(nacosServiceInstanceOperator)) {
//                ThreadUtils.shutdownThreadPool(NacosServiceInstanceUpAndDownOperator.this.executorService, NacosServiceInstanceUpAndDownOperator.this.logger);
//            }
//        }
//    }
//}
