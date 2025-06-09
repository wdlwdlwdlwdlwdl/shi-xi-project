package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderRollCouponService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType.NONE;

@Service
public class OrderRollCouponServiceImpl implements OrderRollCouponService {

    @Autowired
    private OrderPayReadFacade orderPayReadFacade;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    /**
     * 订单退券
     * @param mainOrder
     */
    public void orderRollCoupon(MainOrder mainOrder) {
        // 不存在 直接退
        if (Objects.isNull(mainOrder.getPayCartId())) {
            return;
        }
        // 查询下所有的主单号码, 遍历每个
        List<TcOrderDO> tcOrderDOList = tcOrderRepository.queryMainOrderByCartId(mainOrder.getPayCartId());
        if (CollectionUtils.isEmpty(tcOrderDOList)){
            return;
        }
        List<PromDivideDTO> allProm = new ArrayList<>();
        List<MainOrder> mainOrderList = new ArrayList<>();
        for (TcOrderDO tcOrderDO : tcOrderDOList) {
            if (Objects.isNull(tcOrderDO) || Objects.isNull(tcOrderDO.getPrimaryOrderId())) {
                continue;
            }
            // 当前订单
            if (tcOrderDO.getPrimaryOrderId().equals(mainOrder.getPrimaryOrderId())) {
                List<PromDivideDTO> usedProms = converCouponProp(mainOrder);
                if (CollectionUtils.isNotEmpty(usedProms)) {
                    allProm.addAll(usedProms);
                    mainOrderList.add(mainOrder);
                    continue;
                }
            }
            // 查询没单
            MainOrder payOrder = orderQueryAbility.getMainOrder(tcOrderDO.getPrimaryOrderId());
            if (Objects.isNull(payOrder)) {
                continue;
            }
            // 是否使用券
            List<PromDivideDTO> couponProp = converCouponProp(payOrder);
            if (CollectionUtils.isEmpty(couponProp)) {
                continue;
            }
            // 使用券了 只能是取消订单
            if(OrderStatusEnum.CANCELLED.getCode().equals(payOrder.getPrimaryOrderStatus()) ||
                OrderStatusEnum.CANCEL_FAILED.getCode().equals(payOrder.getPrimaryOrderStatus()) ||
                OrderStatusEnum.REFUND_FAILED.getCode().equals(payOrder.getPrimaryOrderStatus()) ||
                OrderStatusEnum.REFUND_FULL_SUCCESS.getCode().equals(payOrder.getPrimaryOrderStatus())) {
                allProm.addAll(couponProp);
                mainOrderList.add(payOrder);
                continue;
            }
            // 结束，说明存用券 但是没有退单的子单
            return;
        }
        // 没用券 不退
        if (CollectionUtils.isEmpty(allProm)) {
            return;
        }
        // 一个个退券
        for (MainOrder couponOrder : mainOrderList) {
            //一单单的退券
            promotionRepository.orderRollbackUserAssets(couponOrder);
        }
    }

    /**
     * 券对象转换
     * @param mainOrder
     * @return
     */
    private List<PromDivideDTO> converCouponProp(MainOrder mainOrder) {
        List<PromDivideDTO> usedProms = new ArrayList<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            if (Objects.isNull(subOrder) ||
                Objects.isNull(subOrder.getPromotions()) ||
                CollectionUtils.isEmpty(subOrder.getPromotions().getItemDivideDetails())) {
                continue;
            }
            for (ItemDivideDetail itemDivideDetail : subOrder.getPromotions().getItemDivideDetails()) {
                PromDivideDTO promDivideDTO = new PromDivideDTO();
                if (Objects.nonNull(itemDivideDetail.getAssetType()) && itemDivideDetail.getAssetType() > NONE.getCode()) {
                    promDivideDTO.setAssetType(itemDivideDetail.getAssetType());
                    promDivideDTO.setAssetsId(itemDivideDetail.getAssetsId());
                    promDivideDTO.setCampId(itemDivideDetail.getCampId());
                    promDivideDTO.setLevel(itemDivideDetail.getLevel());
                    promDivideDTO.setToolCode(itemDivideDetail.getToolCode());
                    promDivideDTO.setName(itemDivideDetail.getName());
                    promDivideDTO.setReduce(itemDivideDetail.getReduce());
                    usedProms.add(promDivideDTO);
                }
            }
        }
        return usedProms;
    }
}
