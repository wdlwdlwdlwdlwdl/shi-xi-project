package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/4/25 14:39
 */
@Configuration
public class EnvUtils {
    private  static String env = "prod";
    /**
     * 环境变量
     */
    @Value("${spring.application.env:}")
    public void setEnv(String env) {
        EnvUtils.env = env;
    }

    /**
     * dev
     * @return
     */
    public static Boolean isDev(){
        return "dev".equals(env);
    }

    /**
     * uat
     * @return
     */
    public static Boolean isUat(){
        return "uat".equals(env);
    }
    /**
     * 生产环境
     * @return
     */
    public static Boolean isProd(){
        return "prod".equals(env);
    }

    public static String getEnv() {
        return env;
    }
}
