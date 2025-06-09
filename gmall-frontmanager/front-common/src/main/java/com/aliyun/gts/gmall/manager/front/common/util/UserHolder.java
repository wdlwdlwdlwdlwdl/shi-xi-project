package com.aliyun.gts.gmall.manager.front.common.util;

import com.aliyun.gts.gmall.manager.biz.output.CustDTO;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:15
 */
public class UserHolder {
    private static final ThreadLocal<CustDTO> HOLDER = new ThreadLocal<>();

    public static void set(CustDTO custDTO) {
        HOLDER.set(custDTO);
    }

    /**
     * 受保护构造函数
     */
    protected UserHolder() {
        throw new UnsupportedOperationException();
    }

    public static <T extends CustDTO> T getUser() {
        return (T) HOLDER.get();
    }

    public static void clearUser() {
        HOLDER.remove();
    }

}
