package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderPayAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.ReversalAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.TradeAdapter;
import com.aliyun.gts.gmall.manager.front.trade.component.OrderExtendVOBuildCompContext;
import com.aliyun.gts.gmall.manager.front.trade.constants.OrderCountKey;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendContainerVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderUtils.SubOrderButton;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderFacade;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderInvoiceFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.SubReversalDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.google.common.collect.LinkedHashMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.ORDER_CANCEL_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.ORDER_CONFIRM_NO_ITEM;

/**
 * 订单操作接口
 *
 * @author tiansong
 */
@Slf4j
@Service
public class OrderFacadeImpl implements OrderFacade {
    @Autowired
    private OrderAdapter orderAdapter;
    @Autowired
    private TradeAdapter tradeAdapter;
    @Autowired
    private ReversalAdapter reversalAdapter;
    @Autowired
    private TradeRequestConvertor  tradeRequestConvertor;
    @Autowired
    private TradeResponseConvertor tradeResponseConvertor;
    @Autowired
    private OrderExtendVOBuildCompContext orderExtendVOBuildCompContext;
    @Autowired
    private CustomerReadFacade customerReadFacade;
    @Autowired
    private OrderInvoiceFacade orderInvoiceFacade;
    @Autowired
    private PublicFileHttpUrl publicFileHttpUrl;

    @Autowired
    private OrderPayAdapter orderPayAdapter;

    @Override
    public Boolean checkConfirm(ConfirmOrderRestQuery confirmOrderRestQuery) {
        // check 购买商品的商家数量
        this.checkConfirmItem(confirmOrderRestQuery.getOrderItems());
        return Boolean.TRUE;
    }

    /**
     * 校验订单入参商品信息
     * 通过入参的商品ID 查询全部的商品信息，
     *  在匹配每个SKU是否都存在
     * @param confirmItemInfoList
     */
    private void checkConfirmItem(List<ConfirmItemInfo> confirmItemInfoList) {
        // 入参为空
        if (CollectionUtils.isEmpty(confirmItemInfoList)) {
            throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
        }
        // 获取商品ID集合
        Set<Long> itemIdSet = confirmItemInfoList.stream().map(ConfirmItemInfo::getItemId).collect(Collectors.toSet());
        // 查询商品信息
        List<ItemDTO> itemDTOList = tradeAdapter.queryItemDTOByIds(itemIdSet);
        // 没查到数据 报错
        if (CollectionUtils.isEmpty(itemDTOList)) {
            throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
        }
        // 遍历每个商品 看是否都有对应的SKU
        confirmItemInfoList.stream().forEach(confirmItemInfo -> {
            // 参数判断
            if (Objects.isNull(confirmItemInfo) || Objects.isNull(confirmItemInfo.getItemId())) {
                throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
            }
            // 入参商品ID有没有查到数据
            ItemDTO item = itemDTOList.stream()
                .filter(itemDTO -> confirmItemInfo.getItemId()
                .equals(itemDTO.getId()))
                .findFirst()
                .orElse(null);
            // 商品信息为空
            if (Objects.isNull(item)) {
                throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
            }
            // 商品SKU为空， 报错 数据不全
            if (CollectionUtils.isEmpty(item.getSkuList())) {
                throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
            }
            // 商品SKU为空， 报错 数据不全
            SkuDTO sku = item.getSkuList().stream()
                .filter(skuDTO -> confirmItemInfo.getSkuId().equals(skuDTO.getId()))
                .findFirst()
                .orElse(null);
            if (Objects.isNull(sku)) {
                throw new FrontManagerException(ORDER_CONFIRM_NO_ITEM);
            }
        });
    }

    /**
     * 订单确认
     *
     * @param confirmOrderRestQuery
     * @return
     */
    @Override
    public OrderConfirmVO confirm(ConfirmOrderRestQuery confirmOrderRestQuery) {
        // check 入参商品信息校验
        this.checkConfirmItem(confirmOrderRestQuery.getOrderItems());
        // 请求前：设置收货地址
        //this.fillAddress(confirmOrderRestQuery);
        // 调用交易接口，计算商品交易金额
        ConfirmOrderDTO confirmOrderDTO = orderAdapter.confirm(confirmOrderRestQuery);
        // 根据商品类型转换
        List<OrderExtendContainerVO> orderExtendList = orderExtendVOBuildCompContext.buildGroupExtend(confirmOrderDTO, confirmOrderRestQuery);
        // 返回结果
        OrderConfirmVO orderConfirmVO = tradeResponseConvertor.convertOrderConfirm(confirmOrderDTO);
        // 统一补充默认收货地址
        this.fillExtendList(orderExtendList, orderConfirmVO);
        // 处理价格信息(一口价的汇总价格 itemPrice * itemQty)
        this.fillSaleAmt(orderConfirmVO);
        // 优惠信息处理，拆分为：优惠券和活动两类
        this.fillPromotion(orderConfirmVO, confirmOrderDTO);
        // 补充支付方式
        // this.fillPayChannel(orderConfirmVO, confirmOrderRestQuery.getChannel());
        return orderConfirmVO;
    }

    /**
     * 填充用户收货地址信息
     * @param confirmOrderRestQuery
     */
    private void fillAddress(ConfirmOrderRestQuery confirmOrderRestQuery) {
//        if (confirmOrderRestQuery.getAddressVO() == null) {
//            // 设置默认收货地址
//            CustomerAddressDTO defaultAddress = tradeAdapter.queryDefaultAddress(confirmOrderRestQuery.getCustId());
//            confirmOrderRestQuery.setAddressVO(
//                defaultAddress == null ?
//                this.getGmallDefault() :
//                tradeRequestConvertor.convertAddress(defaultAddress));
//            return;
//        }
//        // 校正补充收货地址完整信息
//        CustomerAddressDTO defaultAddress = tradeAdapter.queryAddressById(confirmOrderRestQuery.getCustId(), confirmOrderRestQuery.getAddressVO().getId());
//        // 设置收获地址
//        confirmOrderRestQuery.setAddressVO(tradeResponseConvertor.convertAddress(defaultAddress));
    }


    private void fillExtendList(List<OrderExtendContainerVO> orderExtendList, OrderConfirmVO orderConfirmVO) {
        Map<Long, OrderExtendContainerVO> orderExtendMap = new HashMap<>();
        for (OrderExtendContainerVO containerVO : orderExtendList) {
            orderExtendMap.put(containerVO.getPrimaryOrderId(), containerVO);
        }
        for (OrderGroupVO orderGroupVO : orderConfirmVO.getOrderGroups()) {
            OrderExtendContainerVO containerVO = orderExtendMap.get(orderGroupVO.getPrimaryOrderId());
            orderGroupVO.setExtend(containerVO.getMainOrderExtend());
            orderGroupVO.setErrorMsg(containerVO.getErrorMsg());
            orderGroupVO.setSuccess(containerVO.isSuccess());
        }
    }

    /**
     * 销售金额填充
     * @param orderConfirmVO
     */
    private void fillSaleAmt(OrderConfirmVO orderConfirmVO) {
        AtomicReference<Long> orderSaleAmt = new AtomicReference<>(0L);
        orderConfirmVO.getOrderGroups().forEach(orderGroupVO -> {
            AtomicReference<Long> groupSaleAmt = new AtomicReference<>(0L);
            orderGroupVO.getOrderItems().forEach(orderItemVO -> {
                Long itemSaleAmt = orderItemVO.getItemPrice() * orderItemVO.getItemQty();
                orderItemVO.setSaleAmt(itemSaleAmt);
                groupSaleAmt.updateAndGet(v -> v + itemSaleAmt);
            });
            orderGroupVO.setSaleAmt(groupSaleAmt.get());
            orderSaleAmt.updateAndGet(v -> v + groupSaleAmt.get());
        });
        orderConfirmVO.setSaleAmt(orderSaleAmt.get());
    }


    private void fillPromotion(OrderConfirmVO orderConfirmVO, ConfirmOrderDTO confirmOrderDTO) {
        // 跨店优惠信息,拆分为活动和优惠券
        this.splitCrossPromotion(orderConfirmVO, confirmOrderDTO);
        // 店铺优惠信息,拆分为活动和优惠券
        this.splitShopPromotion(orderConfirmVO, confirmOrderDTO);
    }

    private void splitShopPromotion(OrderConfirmVO orderConfirmVO, ConfirmOrderDTO confirmOrderDTO) {
        if (CollectionUtils.isEmpty(confirmOrderDTO.getOrderGroups())) {
            return;
        }
        // list to map
        Map<Long, OrderGroupVO> orderGroupVOMap = orderConfirmVO
            .getOrderGroups()
            .stream()
            .collect(Collectors.toMap(OrderGroupVO::getSellerId, orderGroupVO -> orderGroupVO, (k1, k2) -> k1));
        confirmOrderDTO.getOrderGroups().forEach(orderGroupDTO -> {
            OrderGroupVO orderGroupVO = orderGroupVOMap.get(orderGroupDTO.getSellerId());
            if (CollectionUtils.isEmpty(orderGroupDTO.getPromotionOptions()) || orderGroupVO == null) {
                // no seller or no promotion
                return;
            }
            // init list
            orderGroupVO.setCouponList(Lists.newArrayList());
            orderGroupVO.setCampaignList(Lists.newArrayList());
            // split seller promotion
            orderGroupDTO.getPromotionOptions().forEach(promotionOptionDTO -> {
                PromotionOptionVO promotionOptionVO = tradeResponseConvertor.convertPromotion(promotionOptionDTO);
                promotionOptionVO.setSellerId(orderGroupDTO.getSellerId());
                if (promotionOptionDTO.getIsCoupon()) {
                    orderGroupVO.getCouponList().add(promotionOptionVO);
                    return;
                }
                orderGroupVO.getCampaignList().add(promotionOptionVO);
            });
        });
    }

    /**
     * 跨店优惠拆分为活动和优惠券列表
     *
     * @param orderConfirmVO
     * @param confirmOrderDTO
     */
    private void splitCrossPromotion(OrderConfirmVO orderConfirmVO, ConfirmOrderDTO confirmOrderDTO) {
        if (CollectionUtils.isEmpty(confirmOrderDTO.getPromotionOptions())) {
            return;
        }
        // init list
        orderConfirmVO.setCouponList(Lists.newArrayList());
        orderConfirmVO.setCampaignList(Lists.newArrayList());
        // foreach split
        confirmOrderDTO.getPromotionOptions().forEach(promotionOptionDTO -> {
            PromotionOptionVO promotionOptionVO = tradeResponseConvertor.convertPromotion(promotionOptionDTO);
            if (promotionOptionDTO.getIsCoupon()) {
                orderConfirmVO.getCouponList().add(promotionOptionVO);
                return;
            }
            orderConfirmVO.getCampaignList().add(promotionOptionVO);
        });
    }

//    private void fillPayChannel(OrderConfirmVO orderConfirmVO, String channel) {
//        boolean isWechat = ChannelEnum.WX_MINI.getCode().equals(channel);
//        // 支付渠道列表
//        List<PayChannelVO> payChannelVOList = Lists.newArrayList(isWechat ? 1 : OnlinePayChannelEnum.values().length);
//        if (isWechat) {
//            OnlinePayChannelEnum onlinePayChannelEnum = OnlinePayChannelEnum.WECHAT;
//            this.addPayChannel(payChannelVOList, onlinePayChannelEnum);
//        }else if(ChannelEnum.ALI_MINI.getCode().equals(channel)){
//            OnlinePayChannelEnum onlinePayChannelEnum = OnlinePayChannelEnum.ALIPAY;
//            this.addPayChannel(payChannelVOList, onlinePayChannelEnum);
//        } else {
//            Arrays.stream(OnlinePayChannelEnum.values()).forEach(onlinePayChannelEnum -> {
//                this.addPayChannel(payChannelVOList, onlinePayChannelEnum);
//            });
//        }
//        boolean containsB2BItem = false;
//        List<OrderGroupVO>  orderGroupVOS = orderConfirmVO.getOrderGroups();
//        for(OrderGroupVO orderGroupVO : orderGroupVOS){
//            containsB2BItem = orderGroupVO.getOrderItems().stream().filter(item->{return
//                item.getItemType().equals(ItemType.B2B.getType());}).findAny().orElse(null) != null;
//            if(containsB2BItem){
//                break;
//            }
//        }
//        if(containsB2BItem){
//            RpcResponse<CustomerDTO> customerResponse = customerReadFacade.
//                query(CustomerByIdQuery.of(orderConfirmVO.getCustId() , null));
//            if(customerResponse.getData().isB2b()){
//                this.addPayChannel(payChannelVOList, B2BPayChannelEnum.CPT);
//                this.addPayChannel(payChannelVOList, B2BPayChannelEnum.ACCOUNT_PERIOD);
//            }
//        }
//        orderConfirmVO.setPayChannelList(payChannelVOList);
//        // 默认支付渠道
//        orderConfirmVO.setPayChannelSelected(payChannelVOList.get(0).getPayChannel());
//    }

//    private void addPayChannel(List<PayChannelVO> payChannelVOList, PayChannelInterface payChannelInterface) {
//        PayChannelVO payChannelVO = new PayChannelVO();
//        payChannelVO.setPayChannel(payChannelInterface.getCode());
//        payChannelVO.setPayChannelName(payChannelInterface.getName());
//        String logUrl = publicFileHttpUrl.httpToOssOrMinio(payChannelInterface.getLogo());
//        logUrl = publicFileHttpUrl.getFileHttpUrl(logUrl,true);
//        payChannelVO.setPayChannelLogo(logUrl);
//        payChannelVOList.add(payChannelVO);
//    }


//    private AddressVO getGmallDefault() {
//        AddressVO addressVO = new AddressVO();
//        addressVO.setProvinceId(BizConst.ADDRESS_PROVINCE_ID);
//        addressVO.setCityId(BizConst.ADDRESS_CITY_ID);
//        addressVO.setAreaId(BizConst.ADDRESS_AREA_ID);
//        return addressVO;
//    }

    @Override
    public OrderCreateResultVO createOrder(CreateOrderRestCommand createOrderRestCommand) {

        List<PrimaryOrderVO> primaryOrderVOList = orderAdapter.createOrder(createOrderRestCommand);
        // 合并支付排除0元订单
        List<String> primaryOrderList = new ArrayList<>();
        int index = 0;
        for (PrimaryOrderVO order : primaryOrderVOList) {
            if (index++ == 0 || order.getPayTotalAmt() > 0L) {
                primaryOrderList.add(order.toMergeParam());
            }
        }
        return OrderCreateResultVO.builder()
            .primaryOrderId(primaryOrderVOList.get(0).getPrimaryOrderId())
            .primaryOrderList(primaryOrderList)
            .build();
    }

    /**
     * 订单确认 新版本
     * @anthor shfen
     * @param confirmOrderRestQuery
     * @return
     */
    @Override
    public OrderConfirmVO confirmNew(ConfirmOrderRestQuery confirmOrderRestQuery) {
        // check 入参商品信息校验
        this.checkConfirmItem(confirmOrderRestQuery.getOrderItems());
        if (!PayModeCode.EPAY.getCode().equals(confirmOrderRestQuery.getPayMode())) {
            confirmOrderRestQuery.setPayMode(
                String.format("%s_%s", confirmOrderRestQuery.getPayMode(), confirmOrderRestQuery.getLoanCycle())
            );
        }
        // 请求前：设置收货地址
        // 调用交易接口，计算商品交易金额
        ConfirmOrderDTO confirmOrderDTO = orderAdapter.confirmNew(confirmOrderRestQuery);
        // 根据商品类型转换数据 每个类型都有自己的实现类
        List<OrderExtendContainerVO> orderExtendList = orderExtendVOBuildCompContext.buildGroupExtend(confirmOrderDTO, confirmOrderRestQuery);
        // 返回结果数据解析
        OrderConfirmVO orderConfirmVO = tradeResponseConvertor.convertOrderConfirm(confirmOrderDTO);
        // 扩展数据填充
        this.fillExtendList(orderExtendList, orderConfirmVO);
        // 处理价格信息(一口价的汇总价格 itemPrice * itemQty)
        this.fillSaleAmt(orderConfirmVO);
        // 优惠信息处理，拆分为：优惠券和活动两类
        this.fillPromotion(orderConfirmVO, confirmOrderDTO);

        //checkout页面 物流方式排序
        List<OrderGroupVO> orderGroups=orderConfirmVO.getOrderGroups();
        for(OrderGroupVO orderGroupVO:orderGroups){
            List<String> supportDeliveryList=orderGroupVO.getSupportDeliveryList();
            String selfService=DeliveryTypeEnum.SELF_SERVICE.getScript();
            String pvz=DeliveryTypeEnum.PVZ.getScript();
            String postamat=DeliveryTypeEnum.POSTAMAT.getScript();
            String warehoursePickup=DeliveryTypeEnum.WAREHOURSE_PICK_UP.getScript();
            // 定义排序规则
            List<String> priorityOrder = Arrays.asList(selfService, pvz, postamat, warehoursePickup);
            // 使用 Comparator 对 supportDeliveryList 进行排序
            supportDeliveryList.sort(Comparator.comparingInt(priorityOrder::indexOf));
        }
        return orderConfirmVO;
    }

    /**
     * 订单确认 新版本
     * @anthor shfeng
     * @param createCheckOutOrderRestCommand
     * @return OrderConfirmVO
     */
    @Override
    public CheckOutOrderResultVO checkOutOrderNew(CreateCheckOutOrderRestCommand createCheckOutOrderRestCommand) {
        CheckOutOrderResultDTO checkOutOrderResultDTO= orderAdapter.checkOutOrderNew(createCheckOutOrderRestCommand);
        return tradeResponseConvertor.convertCheckOutOrderResultVO(checkOutOrderResultDTO);
    }

    /**
     * 创建订单 新版本
     * @anthor shfeng
     * @param createOrderRestCommand
     * @return
     */
    @Override
    public OrderCreateResultVO createOrderNew(CreateOrderRestCommand createOrderRestCommand) {
        List<PrimaryOrderVO> primaryOrderVOList = orderAdapter.createOrderNew(createOrderRestCommand);
        // 合并支付排除0元订单
        List<String> primaryOrderList = new ArrayList<>();
        int index = 0;
        for (PrimaryOrderVO order : primaryOrderVOList) {
            if (index++ == 0 || order.getPayTotalAmt() > 0L) {
                primaryOrderList.add(order.toMergeParam());
            }
        }
        return OrderCreateResultVO.builder()
            .primaryOrderId(primaryOrderVOList.get(0).getPrimaryOrderId())
            .primaryOrderList(primaryOrderList)
            .build();
    }


    @Override
    public OrderMainVO getDetail(PrimaryOrderRestQuery primaryOrderRestQuery) {
        MainOrderDetailDTO orderDetailDTO = orderAdapter.getDetail(primaryOrderRestQuery);
        if (Objects.isNull(orderDetailDTO)) {
            throw new FrontManagerException(TradeFrontResponseCode.ORDER_DETAIL_ERROR);
        }
        // 扩展属性
        OrderExtendContainerVO extendContainerVO = orderExtendVOBuildCompContext.buildExtend(orderDetailDTO);
        // VO转换
        OrderMainVO orderMainVO = tradeResponseConvertor.convertOrderDetail(orderDetailDTO, extendContainerVO);
        // 子订单按钮, 售后申请入口、加购入口
        Map<Long, SubOrderButton> showReversals = this.getSubOrderButtons(primaryOrderRestQuery, orderDetailDTO);
        for (OrderSubVO sub : orderMainVO.getSubOrderList()) {
            SubOrderButton subOrderButton = showReversals.get(sub.getOrderId());
            if (subOrderButton == null) {
                subOrderButton = new SubOrderButton();
            }
            sub.setShowReversal(subOrderButton.isShowReversal());
            sub.setShowReversalRefund(subOrderButton.isShowReversalRefund());
            sub.setShowReversalReturnItem(subOrderButton.isShowReversalReturnItem());
            sub.setShowAddCart(subOrderButton.isShowAddCart());
            sub.setShowApplyCancel(subOrderButton.isShowApplyCancel());
        }
        //填充商品总价 总数量
        this.fillItemQuantity(orderMainVO);
        // 填充商品总价：一口价*数量
        this.fillSaleAmt(orderMainVO);
        // 售后进行中的子订单，补充售后单ID
        this.fillReversalId(orderMainVO);
        // 营销信息
        this.fillDividePromotions(orderMainVO);
        // 预售商品金额计算
        this.fillStepOrder(orderDetailDTO, orderMainVO);
        return orderMainVO;
    }


    @Override
    public List<OrderMainVO> getDetailNew(PrimaryOrderRestQuery primaryOrderRestQuery) {
        List<MainOrderDetailDTO> orderDetailDTOList = orderAdapter.getDetailNew(primaryOrderRestQuery);
        List<OrderMainVO> orderMainVOList=new ArrayList<>();
        for(MainOrderDetailDTO orderDetailDTO:orderDetailDTOList){
            orderMainVOList.add(fillMainOrderDetailDTOList(orderDetailDTO,primaryOrderRestQuery));
        }
        return orderMainVOList;
    }


    private OrderMainVO fillMainOrderDetailDTOList(MainOrderDetailDTO orderDetailDTO,PrimaryOrderRestQuery primaryOrderRestQuery) {
        if (Objects.isNull(orderDetailDTO)) {
            throw new FrontManagerException(TradeFrontResponseCode.ORDER_DETAIL_ERROR);
        }
        // 扩展属性
        OrderExtendContainerVO extendContainerVO = orderExtendVOBuildCompContext.buildExtend(orderDetailDTO);
        // VO转换
        OrderMainVO orderMainVO = tradeResponseConvertor.convertOrderDetail(orderDetailDTO, extendContainerVO);
        // 子订单按钮, 售后申请入口、加购入口
        Map<Long, SubOrderButton> showReversals = this.getSubOrderButtons(primaryOrderRestQuery, orderDetailDTO);
        for (OrderSubVO sub : orderMainVO.getSubOrderList()) {
            SubOrderButton subOrderButton = showReversals.get(sub.getOrderId());
            if (subOrderButton == null) {
                subOrderButton = new SubOrderButton();
            }
            sub.setShowReversal(subOrderButton.isShowReversal());
            sub.setShowReversalRefund(subOrderButton.isShowReversalRefund());
            sub.setShowReversalReturnItem(subOrderButton.isShowReversalReturnItem());
            sub.setShowAddCart(subOrderButton.isShowAddCart());
            sub.setShowApplyCancel(subOrderButton.isShowApplyCancel());
        }
        //填充商品总价 z总数量
        this.fillItemQuantity(orderMainVO);
        // 填充商品总价：一口价*数量
        this.fillSaleAmt(orderMainVO);
        // 售后进行中的子订单，补充售后单ID
        this.fillReversalId(orderMainVO);
        // 营销信息
        this.fillDividePromotions(orderMainVO);
        return orderMainVO;
    }


    /**
     * 填充商品总价 总数量
     * @param orderMainVO
     */
    private void fillItemQuantity(OrderMainVO orderMainVO){
        //totalItemQuantity
        List<OrderSubVO> subOrderList = orderMainVO.getSubOrderList();
        Integer totalItemQuantity = 0;
        for(OrderSubVO orderSubVO:subOrderList){
            totalItemQuantity += orderSubVO.getItemQuantity();
        }
        orderMainVO.setTotalItemQuantity(totalItemQuantity);
    }

    /**
     * 填充订单信息
     *
     * @param orderMainVO
     */
    private void fillOrderInvoice(OrderMainVO orderMainVO) {
        if (Objects.isNull(orderMainVO.getExtras()) ||
            orderMainVO.getExtras().isEmpty() ||
            !orderMainVO.getExtras().containsKey(BizConst.INVOICE_ID)) {
            return;
        }
        Long invoiceId = Long.valueOf(orderMainVO.getExtras().get(BizConst.INVOICE_ID).toString());
        OrderInvoiceInfoCommand command = new OrderInvoiceInfoCommand();
        command.setId(invoiceId);
        try {
            InvoiceDetailVO invoiceInfo = orderInvoiceFacade.getInvoiceInfo(command,orderMainVO.getCustId());
            orderMainVO.setInvoiceDetailVO(invoiceInfo);
        } catch (Exception e) {
            log.error("不存在发票详情信息");
        }

    }


    private Map<Long, SubOrderButton> getSubOrderButtons(PrimaryOrderRestQuery primaryOrderRestQuery, MainOrderDetailDTO mainOrder) {
        Map<Long, SubOrderButton> map = new HashMap<>();
        ReversalCheckRestQuery reversalCheckRestQuery = new ReversalCheckRestQuery();
        reversalCheckRestQuery.setCustId(primaryOrderRestQuery.getCustId());
        reversalCheckRestQuery.setChannel(primaryOrderRestQuery.getChannel());
        reversalCheckRestQuery.setPrimaryOrderId(primaryOrderRestQuery.getPrimaryOrderId());
        reversalCheckRestQuery.setReversalType(ReversalTypeEnum.REFUND_ONLY.getCode());
        List<ReversalSubOrderDTO> reversalSubOrderDTOList = null ;
        try {
            // 退货申请入口，弱依赖该接口
            reversalSubOrderDTOList = reversalAdapter.checkOrder(reversalCheckRestQuery).getSubOrders();
        } catch (Exception e) {
        }
        if (CollectionUtils.isEmpty(reversalSubOrderDTOList)) {
            return map;
        }
        Map<Long/*subOrderId*/, ReversalSubOrderDTO> reversalSubOrderDTOMap = reversalSubOrderDTOList.stream().
                collect(Collectors.toMap(ReversalSubOrderDTO::getOrderId, v -> v, (k1, k2) -> k1));
        mainOrder.getSubOrderList().forEach(sub -> {
            ReversalSubOrderDTO reversal = reversalSubOrderDTOMap.get(sub.getOrderId());
            SubOrderButton b = OrderUtils.getSubOrderButton(mainOrder, sub, reversal);
            map.put(sub.getOrderId(), b);
        });
        return map;
    }

    /**
     * 填充50个
     * @param orderMainVO
     */
    private void fillReversalId(OrderMainVO orderMainVO) {
        // 查询子订单是否有售后中状态
        long reversalCount = orderMainVO.getSubOrderList()
            .stream()
            .filter(orderSubVO -> OrderStatusEnum.REVERSAL_DOING.getCode().equals(orderSubVO.getOrderStatus()))
            .count();
        if (reversalCount == 0L) {
            return;
        }
        // 查询主订单下所有售后单:售后进行中的; 单主订单不会超过50个子订单，每个子订单不超过1条进行中的售后单
        ReversalRestQuery reversalRestQuery = new ReversalRestQuery();
        reversalRestQuery.setCustId(orderMainVO.getCustId());
        reversalRestQuery.setPrimaryOrderId(orderMainVO.getOrderId());
        reversalRestQuery.setReversalStatus(OrderUtils.getReversalWaitStatus());
        reversalRestQuery.setIncludeOrderInfo(Boolean.FALSE);
        reversalRestQuery.setPage(new PageParam(BizConst.PAGE_NO, 50));
        PageInfo<MainReversalDTO> pageInfo = reversalAdapter.queryList(reversalRestQuery);
        if (pageInfo == null || CollectionUtils.isEmpty(pageInfo.getList())) {
            return;
        }
        Map<Long/*I18NMessageUtils.getMessage("sub.order")*/, Long/*主售后单*/> subOrderIdMap = collectReversalId(pageInfo.getList());  //# 子订单
        // 填充子订单的主售后单ID
        orderMainVO.getSubOrderList()
            .stream()
            .forEach(orderSubVO -> orderSubVO.setPrimaryReversalId(subOrderIdMap.get(orderSubVO.getOrderId())));
    }

    private Map<Long/*I18NMessageUtils.getMessage("sub.order")*/, Long/*主售后单*/> collectReversalId(List<MainReversalDTO> list){  //# 子订单
        Map<Long,Long> maps = new HashMap();
        if(CollectionUtils.isEmpty(list)){
            return maps;
        }
        //判断
        List<Integer> status = OrderUtils.getReversalWaitStatus();
        for(MainReversalDTO reversalDTO :list){
            if(status.contains(reversalDTO.getReversalStatus())) {
                for (SubReversalDTO sub : reversalDTO.getSubReversals()) {
                    maps.put(sub.getOrderId(), sub.getPrimaryReversalId());
                }
            }
        }
        return maps;
    }

    /**
     * 填充金额
     * @param orderMainVO
     */
    private void fillSaleAmt(OrderMainVO orderMainVO) {
        long sumPrice = orderMainVO.getSubOrderList().stream()
            .mapToLong(orderSubVO -> orderSubVO.getItemPrice() * orderSubVO.getItemQuantity())
            .sum();
        // 调价后的商品总价
        if (Objects.nonNull(orderMainVO.getAdjustPromotionAmt())) {
            sumPrice += orderMainVO.getAdjustPromotionAmt();
        }
        orderMainVO.setSaleAmt(sumPrice);
    }
    
    /**
     * 解析满赠活动
     * @param mainVO
     * @return
     */
    public void fillDividePromotions(OrderMainVO mainVO){
        // 满赠计算
        List<PromotionOptionVO> divides = new ArrayList<>();
        for(OrderSubVO sub : mainVO.getSubOrderList()){
            if(sub.getItemDividePromotions() != null){
                divides.addAll(sub.getItemDividePromotions());
            }
        }
        // 排除券以外的总优惠金额
        Long campReduceAmt = 0L;
        // 使用的优惠券集合
        // 去重 求和
        LinkedHashMultimap<String, PromotionOptionVO> multimap = LinkedHashMultimap.create();
        Map<String, PromotionOptionVO> result = new HashMap<>();
        for(PromotionOptionVO dto : divides){
            // 券的
            if (AssetsType.COUPON.getCode().equals(dto.getAssetType())) {
                multimap.put(dto.getOptionId(), dto);
            } else {
                // 不算券的
                if (Objects.nonNull(dto.getReduceFee())){
                    campReduceAmt += dto.getReduceFee();
                }
            }
            if(result.get(dto.getOptionId()) != null){
                continue;
            }
            if(!"manzeng".equals(dto.getToolCode())){
                continue;
            }
            result.put(dto.getOptionId(), dto);
        }
        List<PromotionOptionVO> list = result.values().stream().collect(Collectors.toList());
        mainVO.setCampaignList(list);
        // 优惠总金额 不算券
        mainVO.setCampReduceAmt(campReduceAmt);
        // 使用的优惠券
        List<PromotionOptionVO> usedCoupon = new ArrayList<>();
        if(Boolean.FALSE.equals(multimap.isEmpty())) {
            // 遍历每个券 求和
            for (String optionId : multimap.keySet()) {
                if (StringUtils.isEmpty(optionId)) {
                    continue;
                }
                Set<PromotionOptionVO> promotionOptionSet = multimap.get(optionId);
                if (CollectionUtils.isEmpty(promotionOptionSet)) {
                    continue;
                }
                // 遍历
                PromotionOptionVO coupon = new PromotionOptionVO();
                coupon.setReduceFee(0L);
                for (PromotionOptionVO promotionOptionVO : promotionOptionSet) {
                    coupon.setPromotionName(promotionOptionVO.getPromotionName());
                    coupon.setToolCode(promotionOptionVO.getToolCode());
                    coupon.setOptionId(promotionOptionVO.getOptionId());
                    coupon.setReduceFee(coupon.getReduceFee() + promotionOptionVO.getReduceFee());
                }
                usedCoupon.add(coupon);
            }
        }
        mainVO.setCouponList(usedCoupon);
    }

    /**
     * 补充分步骤订单的数据
     * @param orderDetailDTO
     * @param orderMainVO
     */
    private void fillStepOrder(MainOrderDetailDTO orderDetailDTO, OrderMainVO orderMainVO) {
        if (CollectionUtils.isEmpty(orderDetailDTO.getBizCodes()) ||
            Boolean.FALSE.equals(orderDetailDTO.getBizCodes().contains(ExtBizCode.PRE_SALE)) ||
            CollectionUtils.isEmpty(orderDetailDTO.getStepOrders())) {
            return;
        }
        for (StepOrderDTO stepOrderDTO : orderDetailDTO.getStepOrders()) {
            if (Objects.nonNull(stepOrderDTO.getStepNo()) && stepOrderDTO.getStepNo() == 1) {
                orderMainVO.setStepFirstAmt(stepOrderDTO.getRealAmt());
            }
            if (Objects.nonNull(stepOrderDTO.getStepNo()) && stepOrderDTO.getStepNo() == 2) {
                orderMainVO.setStepLastAmt(stepOrderDTO.getRealAmt());
            }
        }
    }

    /**
     * 订单列表查询
     * @param orderListRestQuery
     * @return
     */
    @Override
    public PageInfo<OrderMainVO> getList(OrderListRestQuery orderListRestQuery) {
        PageInfo<MainOrderDTO> pageInfo = orderAdapter.getList(orderListRestQuery);
        if (Objects.isNull(pageInfo) || pageInfo.isEmpty()) {
            return PageInfo.empty();
        }
        // 扩展属性
        Map<Long, OrderExtendContainerVO> map = orderExtendVOBuildCompContext.buildExtendList(pageInfo.getList());
        // 结果
        return new PageInfo(pageInfo.getTotal(), tradeResponseConvertor.convertOrderList(pageInfo.getList(), map));
    }

    @Override
    public Boolean cancelOrder(PrimaryOrderRestCommand primaryOrderRestCommand) {
        try {
            orderAdapter.cancelOrder(primaryOrderRestCommand);
        }catch (Exception e) {
            throw new FrontManagerException(ORDER_CANCEL_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean delOrder(PrimaryOrderRestCommand primaryOrderRestCommand) {
        orderAdapter.delOrder(primaryOrderRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public Boolean confirmReceipt(PrimaryOrderRestCommand primaryOrderRestCommand) {
        orderAdapter.confirmReceipt(primaryOrderRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public Boolean confirmStepOrder(StepOrderRestCommand stepOrderRestCommand) {
        orderAdapter.confirmStepOrder(stepOrderRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public Boolean addEvaluation(AddEvaluationRestCommand addEvaluationRestCommand) {
        PrimaryOrderRestQuery primaryOrderRestQuery = new PrimaryOrderRestQuery();
        primaryOrderRestQuery.setCustId(addEvaluationRestCommand.getCustId());
        primaryOrderRestQuery.setChannel(addEvaluationRestCommand.getChannel());
        primaryOrderRestQuery.setPrimaryOrderId(addEvaluationRestCommand.getPrimaryOrderId());
        primaryOrderRestQuery.checkInput();
        // 根据订单信息，重新设置sellerId / custName / itemTitle
        return tradeAdapter.addEvaluation(addEvaluationRestCommand);
    }

    @Override
    public OrderCountByStatusVO count(CountOrderRestQuery query) {
        Map<Integer, Integer> map = orderAdapter.count(query);

        OrderCountByStatusVO r = new OrderCountByStatusVO();
        r.setWaitPayCnt(map.get(OrderCountKey.waitPayKey));
        r.setWaitRecptCnt(map.get(OrderCountKey.waitReceptKey));
        r.setReversalDoingCnt(map.get(OrderCountKey.reversalDoingKey));
        r.setWaitEvaluateCnt(map.get(OrderCountKey.waitEvaluateKey));
        return r;
    }

    @Override
    public List<LogisticsDetailVO> queryLogisticsList(PrimaryOrderRestQuery primaryOrderRestQuery) {
        return tradeResponseConvertor.convertLogisticsList(
            tradeAdapter.queryLogisticsList(
                primaryOrderRestQuery.getCustId(),
                primaryOrderRestQuery.getPrimaryOrderId()
            )
        );
    }

    @Override
    public List<OrderOperateFlowVO> getFlowList(PrimaryOrderRestQuery primaryOrderRestQuery) {
        List<TcOrderOperateFlowDTO> flowList = tradeAdapter.queryFlowList(primaryOrderRestQuery.getPrimaryOrderId());
        List<TcOrderOperateFlowDTO> filteredList = flowList.stream()
                .filter(dto -> dto != null && !OrderStatusEnum.CREATED.getCode().equals(dto.getToOrderStatus()))
                .collect(Collectors.toList());//过滤订单创建状态
        return tradeResponseConvertor.convertFlowList(filteredList);
    }

    /**
     * 记录用户的
     * @param orderPickCommand
     * @return
     */
    @Override
    public Boolean saveUserLogisticsPick(OrderPickCommand orderPickCommand) {
       return tradeAdapter.saveUserLogisticsPick(orderPickCommand);
    }

    @Override
    public Boolean cancelOrders(PrimaryOrderRestCommand primaryOrderRestCommand) {
        try {
            List<Long> primaryOrderIds = new ArrayList<>();
            primaryOrderIds.add(primaryOrderRestCommand.getPrimaryOrderId());
            RpcResponse<Boolean> result = orderPayAdapter.cancelOrders(primaryOrderIds);
            if (!result.isSuccess()) {
                log.error("cancelOrder error, result is {}", result);
                return false;
            }
            return result.getData();
        } catch (Exception e) {
            log.error("cancelOrder error", e);
            return false;
        }
    }
}