package com.aliyun.gts.gmall.center.trade.core.converter;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcItemBuyLimitDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class BuyOrdsConverter {

    public abstract TcItemBuyLimitDO toTcItemBuyLimitDO(BuyOrdsLimit limit);
}
