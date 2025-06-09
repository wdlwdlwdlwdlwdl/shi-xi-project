package com.aliyun.gts.gmall.manager.front.b2bcomm.configuration;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/18 18:38
 */
@ComponentScan("com.aliyun.gts.gmall.framework.boot")
public class DubboConfiguration {
    protected Integer TEN_SECONDS_MILL    = 10000;
    protected Integer THIRTY_SECONDS_MILL = 30000;
    protected Integer SIXTY_SECONDS_MILL  = 60000;

    private String version = "1.0.0";

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    /**
     * @param interfaceClass
     * @param directUrl
     * @param <T>
     * @return
     */
    public <T> T consumer(Class<T> interfaceClass, String directUrl) {
        return serviceSubscriber.consumer(interfaceClass).directUrl(directUrl).
            version(getVersion()).subscribe();
    }

    /**
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> T consumer(Class<T> interfaceClass) {
        return serviceSubscriber.consumer(interfaceClass).
            version(getVersion()).subscribe();
    }

    public String getVersion() {
        return version;
    }

    /**
     * @param interfaceClass
     * @param directUrl
     * @param <T>
     * @return
     */
    public <T> T consumer(Class<T> interfaceClass, String directUrl,String group) {
        return serviceSubscriber.consumer(interfaceClass).directUrl(directUrl).
                version(getVersion())
                .group(group)
                .subscribe();
    }
}
