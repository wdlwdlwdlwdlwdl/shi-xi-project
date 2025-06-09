package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.item.api.dto.input.PointCreditMkaReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointCreditDetailDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointCreditMkaDTO;
import com.aliyun.gts.gmall.center.item.api.facade.PointCreditMkaReadFacade;
import com.aliyun.gts.gmall.center.item.common.consts.PointItemConstant;
import com.aliyun.gts.gmall.center.item.common.enums.PointCreditMkaType;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.FixedPointItem;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.FixedPointItemRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FixedPointItemRepositoryImpl implements FixedPointItemRepository {

    @Autowired
    private PointCreditMkaReadFacade pointCreditMkaReadFacade;

    @Override
    public FixedPointItem queryEnabledItem(MainOrder mainOrder, SubOrder subOrder) {
        ItemSkuId skuId = subOrder.getItemSku().getItemSkuId();
        Integer orderType = mainOrder.getOrderType();

        PointCreditMkaReq req = new PointCreditMkaReq();
        req.setBizType(PointCreditMkaType.BASIC.getCode());
        if (ExtOrderType.EVOUCHER.getCode().equals(orderType)) {
            req.setBizType(PointCreditMkaType.EVOUCHER.getCode());
        }
        req.setBizId(skuId.getItemId());
        RpcResponse<PointCreditMkaDTO> resp = RpcUtils.invokeRpc(
                () -> pointCreditMkaReadFacade.queryByBizId(req)
                , "pointCreditMkaReadFacade.queryByBizId", I18NMessageUtils.getMessage("query.points.product"), skuId);  //# "查积分商品"

        PointCreditMkaDTO data = resp.getData();
        if (data == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(data.getDetails())) {
            return null;
        }
        // data.getStatus() 当前无效, 不用过滤

        PointCreditDetailDTO detail = data.getDetails().stream()
                .filter(d -> d.getSubBizId() != null && d.getSubBizId().equals(skuId.getSkuId()))
                .findFirst().orElse(null);
        if (detail == null) {
            return null;
        }

        FixedPointItem fixed = new FixedPointItem();
        fixed.setPointCount(detail.getPointCount());
        fixed.setRealPrice(detail.getRealPrice());
        return fixed;
    }
}
