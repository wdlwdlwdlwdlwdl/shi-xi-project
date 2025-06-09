package com.aliyun.gts.gmall.center.trade.core.converter;

import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderTagPrefix;
import com.aliyun.gts.gmall.center.trade.core.enums.SaleTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * description:
 *
 * @author hu.zhiyong
 * @date 2022/09/27 21:09
 **/
@Mapper(componentModel = "spring")
public interface OrderFinanceFlowSyncConverter {

    /**
     * description: 获取销售类型
     *
     * @param mainOrder mainOrder
     * @return com.aliyun.gts.gmall.center.trade.core.enums.SaleTypeEnum
     */
    default SaleTypeEnum getSaleType(MainOrder mainOrder) {
        OrderAttrDO orderAttr = mainOrder.getOrderAttr();
        if (null == orderAttr) {
            return null;
        }
        List<String> tags = orderAttr.getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return null;
        }
        String directType = OrderTagPrefix.SALE_TYPE + SaleTypeEnum.DIRECT.getCode();
        if (tags.contains(directType)) {
            return SaleTypeEnum.DIRECT;
        }
        String proxyType = OrderTagPrefix.SALE_TYPE + SaleTypeEnum.CONSIGNMENT.getCode();
        if (tags.contains(proxyType)) {
            return SaleTypeEnum.CONSIGNMENT;
        }
        String saleType = mainOrder.getSubOrders().get(0).getOrderAttr().getExtras().get(OrderFeatureKey.SALE_TYPE);
        return SaleTypeEnum.buildSaleType(saleType);
    }
}
