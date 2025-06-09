package com.aliyun.gts.gmall.platform.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Slf4j
@MQConsumer(
    groupId = "${trade.order.statuscancel.group}",
    topic = "${trade.order.statuschange.topic}",
    tag = "33||34||37"
)
public class OrderCancelConsumer implements ConsumeEventProcessor {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Override
    public boolean process(StandardEvent event) {
        log.info("receive event msg = " + JSONObject.toJSONString(event));
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_CANCEL_CONSUMER_LOCK_KEY, event.getId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 获取锁失败 重新消费
            return false;
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            return false;
        }
        try {
            OrderMessageDTO orderMessageDTO = convert(event);
            if(orderMessageDTO.getOrderId().equals(orderMessageDTO.getPrimaryOrderId())){
                Long primaryId = orderMessageDTO.getPrimaryOrderId();
                MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryId);
                Long pointCount = mainOrder.getOrderPrice().getPointCount();
                if (pointCount != null && pointCount > 0 && isBeforeSave(mainOrder)) {
                    PointReduceParam pointReduceParam = new PointReduceParam();
                    pointReduceParam.setAmt(mainOrder.getOrderPrice().getPointAmt());
                    pointReduceParam.setCount(pointCount);
                    pointReduceParam.setCustId(mainOrder.getCustomer().getCustId());
                    pointReduceParam.setMainOrderId(primaryId);
                    if (StepOrderUtils.isMultiStep(mainOrder)) {
                        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
                        pointReduceParam.setStepNo(stepOrder.getStepNo());
                        pointReduceParam.setAmt(stepOrder.getPrice().getPointAmt());
                        pointReduceParam.setCount(stepOrder.getPrice().getPointCount());
                    }
                    pointRepository.unlockPoint(Lists.newArrayList(pointReduceParam));
                }
            }
            return true;
        }finally {
            orderLock.unLock();
        }
    }

    private boolean isBeforeSave(MainOrder mainOrder) {
        return OrderStageEnum.codeOf(mainOrder.orderAttr().getOrderStage()) == OrderStageEnum.BEFORE_SALE;
    }

    private OrderMessageDTO convert(StandardEvent event) {
        return (OrderMessageDTO) event.getPayload().getData();
    }

}
