package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

/**
 * 三个参数的函数哦~~~~用于描述三个入参的转换函数
 *
 * @param <T>
 * @param <U>
 * @param <V>
 * @param <R>
 * @author GTS
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    /**
     * 方法调用哦~~
     *
     * @param t
     * @param u
     * @param v
     * @return
     */
    R apply(T t, U u, V v);
}