package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Function小公举
 *
 * @author 俊贤
 * @date 2021/02/09
 */
public class FunctionUtils {

    /**
     * 偏函数，只能固定第一个参数
     *
     * @param f     函数
     * @param first 固定的参数
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     */
    public static <T, U, R> Function<U, R> partial(BiFunction<T, U, R> f, T first) {
        return u -> f.apply(first, u);
    }

    /**
     * 偏函数，只能固定前两个参数
     *
     * @param f      函数
     * @param first  固定的参数
     * @param second 第二个固定的参数
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     */
    public static <T, U, V, R> Function<V, R> doublePartial(TriFunction<T, U, V, R> f, T first, U second) {
        return v -> f.apply(first, second, v);
    }
}