package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPointService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderPointServiceImpl implements OrderPointService {

    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;

    @Override
    public void lockPoint(CreatingOrder order) {
        pointRepository.lockPoint(getReduceParam(order));
    }

    @Override
    public void unlockPoint(CreatingOrder order) {
        pointRepository.unlockPoint(getReduceParam(order));
    }

    private List<PointReduceParam> getReduceParam(CreatingOrder order) {
        return order.getMainOrders().stream()
                .map(main -> {
                    PointReduceParam p = new PointReduceParam();
                    p.setCustId(main.getCustomer().getCustId());
                    p.setMainOrderId(main.getPrimaryOrderId());
                    p.setAmt(main.getOrderPrice().getPointAmt());
                    p.setCount(main.getOrderPrice().getPointCount());
                    if (StepOrderUtils.isMultiStep(main)) {
                        StepOrder stepOrder = main.getCurrentStepOrder();
                        p.setStepNo(stepOrder.getStepNo());
                        p.setAmt(stepOrder.getPrice().getPointAmt());
                        p.setCount(stepOrder.getPrice().getPointCount());
                    }
                    return p;
                }).collect(Collectors.toList());
    }
}
