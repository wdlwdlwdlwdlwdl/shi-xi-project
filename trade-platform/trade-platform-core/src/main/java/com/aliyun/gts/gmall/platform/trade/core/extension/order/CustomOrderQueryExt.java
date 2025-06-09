package com.aliyun.gts.gmall.platform.trade.core.extension.order;


import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 订单自定义查询扩展能力，用于在查询订单基础信息的同时、可以查询自定义的关联数据、并集成到订单领域模型中
 *
 * @author xinchen
 */
public interface CustomOrderQueryExt extends IExtensionPoints {


    @AbilityExtension(
        code = "ENRICH_MAINORDER",
        name = "主订单信息扩展查询",
        description = "主订单信息扩展查询"
    )
    TradeBizResult enrichMainOrder(MainOrder mainOrder);

}
