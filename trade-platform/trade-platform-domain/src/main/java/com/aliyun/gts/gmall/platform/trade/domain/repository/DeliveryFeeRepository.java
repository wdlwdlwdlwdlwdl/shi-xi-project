package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.DeliveryFee;

import java.util.List;

public interface DeliveryFeeRepository {

    TcDeliveryFeeDO queryTcDeliveryFee(long id);

    PageInfo<TcDeliveryFeeDO> queryDeliveryFeeList(DeliveryFee req);

    TcDeliveryFeeDO saveDeliveryFee(TcDeliveryFeeDO tcDeliveryFeeDO);

    TcDeliveryFeeDO updateDeliveryFee(TcDeliveryFeeDO tcDeliveryFeeDO);

    /**
     * 批量查询
     * @param req
     * @return
     */
    List<TcDeliveryFeeDO> queryDeliveryList(TcDeliveryFeeDO tcDeliveryFeeDO);

    /**
     * 校验是否存在
     * @param tcDeliveryFeeDO
     * @return
     */
    List<TcDeliveryFeeDO> checkExist(TcDeliveryFeeDO tcDeliveryFeeDO);

}
