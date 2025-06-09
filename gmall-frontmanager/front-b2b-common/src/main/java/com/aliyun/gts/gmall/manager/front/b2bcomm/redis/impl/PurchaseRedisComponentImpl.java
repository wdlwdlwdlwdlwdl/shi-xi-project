package com.aliyun.gts.gmall.manager.front.b2bcomm.redis.impl;

import com.aliyun.gts.gmall.manager.front.b2bcomm.redis.PurchaseRedisComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRedisComponentImpl implements PurchaseRedisComponent {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    String preFix = "purchase_admin_";

    @Override
    public void putApproveId(Long yourId , String flowId , String appCode){
        stringRedisTemplate.opsForValue().set(buildKey(yourId , appCode ) , flowId);
    }

    @Override
    public String getApproveId(Long yourId , String appCode){
        return stringRedisTemplate.opsForValue().get(buildKey(yourId , appCode));
    }

    private String buildKey(Long yourId , String appCode){
        return preFix + appCode + "_" + yourId;
    }

}
