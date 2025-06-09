package com.aliyun.gts.gmall.manager.front.b2bcomm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/3 19:51
 */

@Service
@Slf4j
public class BeanFactory implements ApplicationContextAware {
    public static ApplicationContext applicationContext;
    public BeanFactory() {
    }

    /**
     * 本地启动
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            T bean = applicationContext.getBean(clazz);
            if(bean == null){
                log.error("beanNotFound+"+clazz.getName());
            }
            return bean;
        } catch (Exception e) {
            log.error("beanNotFound+"+clazz.getName(),e);
            return null;
        }
    }
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanFactory.applicationContext = applicationContext;
    }
}