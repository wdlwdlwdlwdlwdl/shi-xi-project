package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.SubOrderVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.aliyun.gts.gmall.center.trade.api.util.OrderDisplayUtils.getStatusName;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class OrderConverter {

    @Autowired
    PublicFileHttpUrl publicFileHttpUrl;
    @Autowired
    protected MultiLangConverter multiLangConverter;

    public SubOrderVO build(SubOrderDTO sub, MainOrderDTO mainOrderDTO) {
        SubOrderVO subOrderVO = new SubOrderVO();
        BeanUtils.copyProperties(sub, subOrderVO);
        OrderPriceVO subOrderPriceVO = new OrderPriceVO();
        BeanUtils.copyProperties(sub.getPrice(), subOrderPriceVO);
        subOrderVO.setOrderStatusName(getStatusName(mainOrderDTO, sub.getOrderStatus()));
        subOrderVO.setPrice(subOrderPriceVO);
        subOrderVO.getPrice().setItemTotalAmt(getItemTotalAmt(sub));
        subOrderVO.setItemTitle(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(sub.getItemTitle())));
        subOrderVO.setSkuDesc(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(sub.getSkuDesc())));
        return subOrderVO;
    }


    private Long getItemTotalAmt(SubOrderDTO subOrder) {
        long amt = subOrder.getPrice().getItemPrice() * subOrder.getItemQuantity();
        // 调价后的商品总价
        if (subOrder.getPrice().getAdjustPromotionAmt() != null) {
            amt += subOrder.getPrice().getAdjustPromotionAmt();
        }
        return amt;
    }

}
