package com.aliyun.gts.gmall.platform.trade.api.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcLogisticsWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by auto-generated on 2021/02/04.
 */

@Service
@Slf4j
public class TcLogisticsWriteFacadeImpl implements TcLogisticsWriteFacade {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LogisticsService logisticsService;

    @Override
    public RpcResponse  updateLogistics(LogisticsRpcReq req){
        // 加锁 单一订单 加锁处理 不能同事处理两个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, req.getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(
                CommonConstant.ORDER_TIME_OUT,
                CommonConstant.ORDER_MAX_TIME_OUT,
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        try {
            TcLogisticsRpcReq rpc = new TcLogisticsRpcReq();
            BeanUtils.copyProperties(req, rpc);
            TradeBizResult result = logisticsService.updateLogistics(rpc);
            if(result.isSuccess()){
                return RpcResponse.ok(null);
            }
            return RpcResponse.fail(result.getFail());
        }
        finally {
            orderLock.unLock();
        }
    }
}
