package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CustDeliveryFee;

import java.util.List;

public interface CustDeliveryFeeRepository {

    TcCustDeliveryFeeDO queryTcCustDeliveryFee(TcCustDeliveryFeeDO tcDeliveryFeeDO);

    PageInfo<TcCustDeliveryFeeDO> queryCustDeliveryFeePage(CustDeliveryFee req);

    List<TcCustDeliveryFeeDO> queryCustDeliveryFeeList(CustDeliveryFee req);

    TcCustDeliveryFeeDO saveCustDeliveryFee(TcCustDeliveryFeeDO tcDeliveryFeeDO);

    TcCustDeliveryFeeDO updateCustDeliveryFee(TcCustDeliveryFeeDO tcDeliveryFeeDO);
}
