package com.aliyun.gts.gmall.manager.front.common.util;

import org.springframework.beans.BeanUtils;

/**
 * bean copy 工具类
 *
 * @author tiansong
 */
public interface GmallBeanUtils {

    /**
     * 兼容性更好的copy工具
     * @param source      源对象实例
     * @param targetClass 目标对象类型
     * @param <S>         源对象泛型
     * @param <T>         目标对象类型
     * @return 目标对象实例
     */
    default <S, T> T copyProperties(S source, Class<T> targetClass) {
        T target = null;
        try {
            target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }
}
