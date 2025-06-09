package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherTemplate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;

import java.util.List;
import java.util.Map;

public interface EvoucherCreateService {

    /**
     * 从商品获取电子凭证模版
     */
    EvoucherTemplate getEvTemplate(ItemSku itemSku);

    /**
     * 电子凭证模版记录到订单
     */
    void addEvFeature(Map<String, String> features, EvoucherTemplate template);

    /**
     * 从订单扩展取电子凭证模版
     */
    EvoucherTemplate getFromEvFeature(SubOrder subOrder);

    /**
     * 从订单获取电子凭证模版, 生成电子凭证实例
     */
    List<EvoucherInstance> createEvInstance(SubOrder subOrder, MainOrder mainOrder);

    /**
     * 完成发放, 保存DB, 订单状态推进到已发货, 并创建相关定时任务
     */
    void saveEv(MainOrder order, List<EvoucherInstance> evList);
}
