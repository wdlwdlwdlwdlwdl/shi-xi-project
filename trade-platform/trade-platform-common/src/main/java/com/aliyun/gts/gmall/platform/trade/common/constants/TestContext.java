package com.aliyun.gts.gmall.platform.trade.common.constants;

public class TestContext {

    ThreadLocal<Integer> local = new ThreadLocal<>();

    public void set(Integer code){
        local.set(code);
    }

    public Integer get(){
        return local.get();
    }

}
