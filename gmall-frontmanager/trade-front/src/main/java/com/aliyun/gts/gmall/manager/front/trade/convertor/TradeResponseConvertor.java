package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.center.trade.api.util.OrderDisplayUtils;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderMethodEnum;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.component.OrderExtendVOBuildCompContext;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartGroupVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.ItemDeliveryVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.CombineItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.ItemPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.OrderItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.EvoucherInfoVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendContainerVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.StepExtendVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayChannelVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayRenderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalSubOrderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalSubVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.B2BPayChannelEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OnlinePayChannelEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.PayChannelInterface;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.CartPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.PrimaryOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayChannelInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.SubReversalDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAddressDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 交易相关转换， rpc to rest
 *
 * @author tiansong
 */
@Mapper(componentModel = "spring", uses = {OrderExtendVOBuildCompContext.class},
        imports = {OrderTaskDTO.class, OrderStatusEnum.class})
public abstract class TradeResponseConvertor {

    @Autowired
    OrderExtendVOBuildCompContext orderExtendVOBuildCompContext;
    // ------------------------------- cart ---------------------------------------

    /**
     * 购物车查询结果转换
     *
     * @param cartGroupDTOList
     * @return
     */
    public abstract List<CartGroupVO> convertCart(List<CartGroupDTO> cartGroupDTOList);

    // 商品物流信息对象
    public abstract  ItemDeliveryVO toItemDeliveryDTO(ItemDeliveryDTO itemDelivery);


    /**
     * 购物车商品转换
     *
     * @param cartItemDTO
     * @return
     */
    @Mappings({
        @Mapping(target = "itemPic", expression = "java(cartItemDTO.getSkuPic() == null ? cartItemDTO.getItemPic() : cartItemDTO.getSkuPic())"),
        @Mapping(target = "depositPrice", source = "itemDivideDetails", qualifiedByName = "parseDepositPrice"),
    })

    public abstract CartItemVO convertCartItemVO(CartItemDTO cartItemDTO);

    @Named("parseDepositPrice")
    protected Long parseDepositPrice(List<ItemDividePromotionDTO> itemDivideDetails) {
        if (CollectionUtils.isEmpty(itemDivideDetails)) {
            return null;
        }
        for (ItemDividePromotionDTO d : itemDivideDetails) {
            if (!PromotionToolCodes.YUSHOU.equals(d.getToolCode())) {
                continue;
            }
            if (d.getExtras() == null) {
                continue;
            }
            Object pred = d.getExtras().get("pred");
            if (pred == null) {
                continue;
            }
            if (pred instanceof Number) {
                return ((Number) pred).longValue();
            }
            if (pred instanceof String) {
                return Long.parseLong(pred.toString());
            }
        }
        return null;
    }

    /**
     * 购物车价格计算结果转换
     *
     * @param cartPriceDTO
     * @return
     */
    public abstract CartPriceVO convertCartPrice(CartPriceDTO cartPriceDTO);

    @Mappings({
            @Mapping(target = "firstPayPrice", source = "itemDivideDetails", qualifiedByName = "parseDepositPrice"),
    })
    protected abstract ItemPriceVO convertItemPriceVO(ItemPriceDTO item);

    // ------------------------------- pay ---------------------------------------

    /**
     * 收银台展示结果转换
     *
     * @param payRenderRpcResp
     * @return
     */
    @Mapping(source = "orderUsedPointAmount", target = "pointAmount")
    @Mapping(source = "orderUsedPointCount", target = "pointCount")
    @Mapping(source = "supportPayChannel" , target = "payChannelList")
    @Mapping(target = "accountPeriod" , expression = "java(getAccountPeriodMemo(payRenderRpcResp))")
    public abstract PayRenderVO convertPayRender(PayRenderRpcResp payRenderRpcResp);

    protected String getAccountPeriodMemo(PayRenderRpcResp payRenderRpcResp){
        Map<String, String>  extra = payRenderRpcResp.getExtra();
        if(extra != null){
            String memo = extra.get(CreatingOrderParamConstants.ACCOUNT_PERIOD_MEMO);
            if(StringUtils.isNotBlank(memo)){
                return memo;
            }
        }
        return "";
    }

    /**
     * 支付结果转换
     *
     * @param orderPayRpcResp
     * @return
     */
    public abstract OrderPayVO convertToPay(OrderPayRpcResp orderPayRpcResp);

    // ------------------------------- order ---------------------------------------

    /**
     * 订单详情转换
     *
     * @param orderDetailDTO
     * @param extendContainerVO
     * @return
     */
    @Mapping(source = "orderDetailDTO.price.orderTotalAmt", target = "totalAmt")
    @Mapping(source = "orderDetailDTO.price.orderRealAmt", target = "realAmt")
    @Mapping(source = "orderDetailDTO.price.pointAmt", target = "pointAmt")
    @Mapping(source = "orderDetailDTO.price.pointCount", target = "pointCount")
    @Mapping(source = "orderDetailDTO.price.freightAmt", target = "freightAmt")
    @Mapping(source = "orderDetailDTO.price.adjustRealAmt", target = "adjustRealAmt")
    @Mapping(source = "orderDetailDTO.price.adjustPointCount", target = "adjustPointCount")
    @Mapping(source = "orderDetailDTO.price.adjustPointAmt", target = "adjustPointAmt")
    @Mapping(source = "orderDetailDTO.price.adjustFreightAmt", target = "adjustFreightAmt")
    @Mapping(source = "orderDetailDTO.price.adjustPromotionAmt", target = "adjustPromotionAmt")
    @Mapping(source = "orderDetailDTO.price.orderPromotionAmt", target = "promotionAmt")
    @Mapping(expression = "java(convertSubOrderList(orderDetailDTO, extendContainerVO))", target = "subOrderList")
    @Mapping(expression = "java(getOrderStatusName(orderDetailDTO))", target = "orderStatusName")
    @Mapping(expression = "java(getPayName(orderDetailDTO))", target = "payTypeStr")
    @Mapping(source = "extendContainerVO.mainOrderExtend", target = "extend")
    @Mapping(source = "orderDetailDTO.sellerPhone", target = "sellerPhone")
    @Mapping(source = "orderDetailDTO.sellerLogo", target = "sellerLogo")
    @Mapping(source = "orderDetailDTO.apply", target = "apply")
    @Mapping(source = "orderDetailDTO.remark", target = "remark")
    @Mapping(source = "orderDetailDTO.bankCardNbr", target = "bankCardNbr")
    @Mapping(source = "orderDetailDTO", target = "orderButtons")
//    @Mapping(expression = "java(getHelpOrderVO(orderDetailDTO))", target="helpOrderInfo")
    @Mapping(target = "autoConfirmReceiveTaskMillis",
            expression = "java(getOrderTaskMillis(orderDetailDTO, OrderTaskDTO.TASK_TYPE_SYS_CONFIRM, OrderStatusEnum.ORDER_SENDED.getCode()))")
    @Mapping(target = "autoCloseUnpaidTaskMillis",
            expression = "java(getOrderTaskMillis(orderDetailDTO, OrderTaskDTO.TASK_TYPE_CLOSE_UNPAID, OrderStatusEnum.ORDER_WAIT_PAY.getCode()))")
    public abstract OrderMainVO convertOrderDetail(MainOrderDetailDTO orderDetailDTO, OrderExtendContainerVO extendContainerVO);

    protected Long getOrderTaskMillis(MainOrderDetailDTO mainOrder, String taskType, Integer checkStatus) {
        List<OrderTaskDTO> orderTasks = mainOrder.getOrderTasks();
        if (CollectionUtils.isEmpty(orderTasks)) {
            return null;
        }
        if (!checkStatus.equals(mainOrder.getOrderStatus())) {
            return null;
        }
        long time = orderTasks.stream()
                .filter(t -> StringUtils.equals(t.getTaskType(), taskType))
                .filter(t -> !Integer.valueOf(OrderTaskDTO.STATUS_FINISH).equals(t.getTaskStatus()))
                .filter(t -> !Integer.valueOf(OrderTaskDTO.STATUS_DELETE).equals(t.getTaskStatus()))
                .map(OrderTaskDTO::getScheduleTime)
                .mapToLong(Date::getTime)
                .min().orElse(0L);
        return time - System.currentTimeMillis();
    }

    public abstract ReversalSubOrderVO convertReversalSubOrder(ReversalSubOrderDTO subOrderDTO);
    /**
     * 组合商品信息
     * @param subOrderDTO
     * @return
     */
    public List<CombineItemVO> reversalCombineItem(ReversalSubOrderDTO subOrderDTO) {
        Map<String, String> feature = subOrderDTO.getFeature();
        if (feature == null) {
            return null;
        }
        String str = feature.get(ItemConstants.COMBINE_ITEM);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSONArray.parseArray(str, CombineItemVO.class);
    }

    protected OrderButtonsVO getButtons(MainOrderDTO mainOrder) {
        return OrderUtils.getOrderButtons(mainOrder);
    }

    @Named("getShowTags")
    protected List<String> getShowTags(SubOrderDTO subOrder) {
        return OrderUtils.getShowTags(subOrder);
    }

    public String getOrderStatusName(MainOrderDTO main) {
        if (main == null) {
            return null;
        }
        return OrderDisplayUtils.getStatusName(main, main.getOrderStatus());
    }

    public String getPayName(MainOrderDTO main) {
        if (main == null) {
            return null;
        }
        return OrderDisplayUtils.getPayName(Integer.valueOf(main.getPayChannel()));
    }

//    public HelpOrderVO getHelpOrderVO(MainOrderDTO mainOrderDTO) {
//        List<String> tags = mainOrderDTO.getTags();
//        Map<String, String> extraMap = Optional.ofNullable(mainOrderDTO.getExtras()).orElse(new HashMap<>());
//        if(Objects.nonNull(tags) && tags.contains("HELP_ORDER")) {
//            String operatorName = extraMap.getOrDefault(OrderFeatureKey.HELP_ORDER_NAME, "");
//            HelpOrderVO helpOrderVO = new HelpOrderVO(operatorName);
//            return helpOrderVO;
//        }
//        return null;
//    }

    public List<OrderSubVO> convertSubOrderList(MainOrderDetailDTO main, OrderExtendContainerVO extendContainerVO) {
        List<SubOrderDetailDTO> subOrderDTOS = main.getSubDetailOrderList();
        List<OrderSubVO> subVOList = new ArrayList<>();
        for (SubOrderDTO subOrderDTO : subOrderDTOS) {
            OrderSubVO subVO = convertSubOrder(subOrderDTO);
            subVO.setOrderStatusName(OrderDisplayUtils.getStatusName(main, subOrderDTO.getOrderStatus()));
            subVO.setExtend(extendContainerVO.getSubOrderExtendMap().get(subOrderDTO.getOrderId()));
            subVOList.add(subVO);
        }
        return subVOList;
    }

    @Mapping(source = "name", target = "promotionName")
    @Mapping(source = "campId", target = "optionId")
    @Mapping(source = "reduce", target = "reduceFee")
    public abstract PromotionOptionVO convertPromotionOption(ItemDividePromotionDTO dto);
    /**
     * 订单列表转换
     */
    public List<OrderMainVO> convertOrderList(List<MainOrderDTO> sourceList, Map<Long, OrderExtendContainerVO> map) {
        if (sourceList == null) {
            return null;
        }
        List<OrderMainVO> targetList = new ArrayList<>();
        convertOrderList(targetList, sourceList);
        if (map == null || map.isEmpty()) {
            return targetList;
        }
        for (OrderMainVO target : targetList) {
            OrderExtendContainerVO ext = map.get(target.getOrderId());
            if (ext != null) {
                target.setExtend(ext.getMainOrderExtend());
            }

        }
        return targetList;
    }


    protected abstract void convertOrderList(@MappingTarget List<OrderMainVO> targetList, List<MainOrderDTO> sourceList);

    /**
     * 主订单转换
     *
     * @param mainOrderDTO
     * @return
     */
    @Mapping(source = "price.orderTotalAmt", target = "totalAmt")
    @Mapping(source = "price.orderRealAmt", target = "realAmt")
    @Mapping(source = "price.pointAmt", target = "pointAmt")
    @Mapping(source = "price.pointCount", target = "pointCount")
    @Mapping(source = "price.freightAmt", target = "freightAmt")
    @Mapping(source = "price.adjustRealAmt", target = "adjustRealAmt")
    @Mapping(source = "price.adjustPointCount", target = "adjustPointCount")
    @Mapping(source = "price.adjustPointAmt", target = "adjustPointAmt")
    @Mapping(source = "price.adjustFreightAmt", target = "adjustFreightAmt")
    @Mapping(source = "price.adjustPromotionAmt", target = "adjustPromotionAmt")
    @Mapping(source = "price.orderPromotionAmt", target = "promotionAmt")
    @Mapping(source = "mainOrderDTO", target = "orderButtons")
    @Mapping(expression = "java(getOrderStatusName(mainOrderDTO))", target = "orderStatusName")
    public abstract OrderMainVO convertMainOrder(MainOrderDTO mainOrderDTO);

    /**
     * 子订单转换
     *
     * @param subOrderDTO
     * @return
     */
    @Mapping(source = "price.orderTotalAmt", target = "totalAmt")
    @Mapping(source = "price.orderRealAmt", target = "realAmt")
    @Mapping(source = "price.pointAmt", target = "pointAmt")
    @Mapping(source = "price.pointCount", target = "pointCount")
    @Mapping(source = "price.freightAmt", target = "freightAmt")
    @Mapping(source = "price.adjustRealAmt", target = "adjustRealAmt")
    @Mapping(source = "price.adjustPointCount", target = "adjustPointCount")
    @Mapping(source = "price.adjustPointAmt", target = "adjustPointAmt")
    @Mapping(source = "price.adjustFreightAmt", target = "adjustFreightAmt")
    @Mapping(source = "price.adjustPromotionAmt", target = "adjustPromotionAmt")
    @Mapping(source = "price.orderPromotionAmt", target = "promotionAmt")
    @Mapping(source = "price.originPrice", target = "itemOriginPrice")
    @Mapping(source = "price.itemPrice", target = "itemPrice")
    @Mapping(target = "itemPic",
        expression = "java(subOrderDTO.getSkuPic() == null ? subOrderDTO.getItemPic() : subOrderDTO.getSkuPic())")
    @Mapping(source = "subOrderDTO", target = "showTags", qualifiedByName = "getShowTags")
    @Mapping(target = "combineItems", expression = "java(combineItemConvert(subOrderDTO.getFeature()))")
    public abstract OrderSubVO convertSubOrder(SubOrderDTO subOrderDTO);

    ///**
    // * 订单确认页转换
    // *
    // * @param confirmOrderDTO
    // * @param containerVO
    // * @return
    // */
    //@Mappings({
    //    @Mapping(source = "containerVO.mainOrderExtend", target = "extend"),
    //})
    //public abstract OrderConfirmVO convertOrderConfirm(ConfirmOrderDTO confirmOrderDTO,
    //    OrderExtendContainerVO containerVO);


    public abstract OrderConfirmVO convertOrderConfirm(ConfirmOrderDTO confirmOrderDTO);


    /**
     * 创建临时单结果转换
     *
     * @param checkOutOrderResultDTO
     * @return
     */
    public abstract CheckOutOrderResultVO convertCheckOutOrderResultVO(CheckOutOrderResultDTO checkOutOrderResultDTO);

    /**
     * 同上
     *
     * @param orderItemDTO
     * @return
     */
    @Mapping(source = "orderQty", target = "itemQty")
    @Mapping(target = "picUrl",
        expression = "java(orderItemDTO.getSkuPic() == null ? orderItemDTO.getPicUrl() : orderItemDTO.getSkuPic())")
    @Mapping(target = "combineItems", expression = "java(combineItemConvert(orderItemDTO.getFeature()))")
    public abstract OrderItemVO convertOrderConfirmItem(OrderItemDTO orderItemDTO);

    protected List<CombineItemVO> combineItemConvert(Map<String,String> feature) {
        if (feature == null) {
            return null;
        }
        String str = feature.get(ItemConstants.COMBINE_ITEM);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSONArray.parseArray(str, CombineItemVO.class);
    }
    /**
     * 订单创建结果转换
     *
     * @param primaryOrderResultDTOList
     * @return
     */
    public abstract List<PrimaryOrderVO> convertOrderCreate(List<PrimaryOrderResultDTO> primaryOrderResultDTOList);

    /**
     * 优惠信息转换
     *
     * @param promotionOptionDTO
     * @return
     */
    public abstract PromotionOptionVO convertPromotion(PromotionOptionDTO promotionOptionDTO);

    /**
     * 物流详情查询转换
     *
     * @param logisticsDetailDTOList
     * @return
     */
    public abstract List<LogisticsDetailVO> convertLogisticsList(List<LogisticsDetailDTO> logisticsDetailDTOList);

    /**
     * 操作流转换
     *
     * @param
     * @return
     */
    public abstract List<OrderOperateFlowVO> convertFlowList(List<TcOrderOperateFlowDTO> flowList);
    // ------------------------------- reversal ---------------------------------------

    /**
     * 售后单列表转换
     *
     * @param mainReversalDTOList
     * @return
     */
    public abstract List<ReversalOrderVO> convertReversalList(List<MainReversalDTO> mainReversalDTOList);

    /**
     * 售后单详情转换
     *
     * @param mainReversalDTO
     * @return
     */
    @Mapping(source = "orderInfo.sellerName", target = "sellerName")
    public abstract ReversalDetailVO convertReversalDetail(MainReversalDTO mainReversalDTO);

    /**
     * 售后退款页面
     * @param sub
     * @return
     */
    @Mapping(target = "combineItemRev", expression = "java(combineItemConvert(sub.getReversalFeatures().getFeature()))")
    public abstract ReversalSubVO subReversalDTOToReversalSubVO(SubReversalDTO sub);
    /**
     * 用户收货地址转换
     *
     * @param customerAddressDTO
     * @return
     */
    public abstract AddressVO convertAddress(CustomerAddressDTO customerAddressDTO);

    /**
     * 卖家地址转化
     *
     * @param sellerAddressDTO
     * @return
     */
    public abstract AddressVO convertSellerAddress(SellerAddressDTO sellerAddressDTO);

    /**
     * 收货地址转换
     *
     * @param receiverDTO
     * @return
     */
    @Mapping(source = "receiverId", target = "id")
    @Mapping(source = "receiverName", target = "name")
    @Mapping(source = "provinceCode", target = "provinceId")
    @Mapping(source = "cityCode", target = "cityId")
    @Mapping(source = "districtCode", target = "areaId")
    @Mapping(source = "deliveryAddr", target = "addressDetail")
    public abstract AddressVO convertAddress(ReceiverDTO receiverDTO);

    public abstract EvoucherInfoVO convertEvoucher(EvoucherDTO evoucherDTO);

    protected List<PayChannelVO> convert(List<PayChannelInfo> list){
        List<PayChannelVO> voList = new ArrayList<>();
        for (PayChannelInfo payChannelInfo : list){
            PayChannelInterface payChannelInterface = OnlinePayChannelEnum.getByCode(payChannelInfo.getPayChannel());
            if(payChannelInterface == null){
                payChannelInterface = B2BPayChannelEnum.getByCode(payChannelInfo.getPayChannel());
            }
            if(payChannelInterface != null){
                PayChannelVO payChannelVO = new PayChannelVO();
                payChannelVO.setPayChannel(payChannelInterface.getCode());
                payChannelVO.setPayChannelName(payChannelInterface.getName());
                payChannelVO.setPayChannelLogo(payChannelInterface.getLogo());
                voList.add(payChannelVO);
            }
        }
        return voList;
    }

    public abstract PageInfo<CancelReasonVO> convertToVO(PageInfo<CancelReasonDTO> page);
}
