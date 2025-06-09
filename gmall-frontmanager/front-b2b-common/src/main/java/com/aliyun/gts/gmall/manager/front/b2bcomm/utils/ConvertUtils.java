package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/18 16:12
 */
public class ConvertUtils {

    /**
     * 列表的对象转换
     * @param inputs
     * @param convertFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> converts(List<T> inputs, Function<T, R> convertFunction) {
        if (inputs == null) {
            return null;
        }
        if (inputs.size() == 0) {
            return new ArrayList<R>();
        }
        List<R> result = new ArrayList<R>();
        for (T input : inputs) {
            R r = convertFunction.apply(input);
            result.add(r);
        }
        return result;
    }

    /**
     * 翻页
     * @param pageInfo
     * @param convertFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> PageInfo<R> convertPage(PageInfo<T> pageInfo, Function<T, R> convertFunction) {
        if (pageInfo == null) {
            return null;
        }
        PageInfo result  = new PageInfo();
        result.setTotal(pageInfo.getTotal());
        List<R> list = converts(pageInfo.getList(),convertFunction);
        result.setList(list);
        return result;
    }
}
