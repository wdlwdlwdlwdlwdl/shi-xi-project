package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;

/**
 * 查询不可合并下单的场景, 拆分下单
 */
public interface MixOrderSplitService {

    // 标记为查询拆单流程
    void setCheckSplit(CreatingOrder ord);

    // 判断是否查询拆单流程
    boolean isCheckSplit(CreatingOrder ord);

    // 记录不可下单商品
    void markDisableItem(CreatingOrder ord, ItemSkuId skuId, Integer qty, String code, String message);

    // 标记单独下单的子单
    void markAlone(SubOrder subOrder, String bizCode);

    // 获取结果
    ConfirmOrderSplitDTO getSplitResult(CreatingOrder ord);
}
