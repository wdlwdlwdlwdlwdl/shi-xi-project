package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.entity.point.FixedPointItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;

public interface FixedPointItemRepository {

    FixedPointItem queryEnabledItem(MainOrder mainOrder, SubOrder subOrder);
}
