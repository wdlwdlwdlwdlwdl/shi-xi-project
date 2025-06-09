package com.aliyun.gts.gmall.platform.trade.core.extension.price;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;

import java.util.List;

/**
 * 改价格扩展点
 */
public interface PriceAdjustExt extends IExtensionPoints {

    List<OrderChangeNotify> adjustPrice(MainOrder mainOrder, AdjustPrice adj);
}
