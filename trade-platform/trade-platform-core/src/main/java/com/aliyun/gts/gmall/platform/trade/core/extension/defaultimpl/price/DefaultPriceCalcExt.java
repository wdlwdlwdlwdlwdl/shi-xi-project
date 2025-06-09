package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.price;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.input.commercial.DeliveryTypeFullInfoQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.delivery.SellerSelfDeliveryQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.warehouse.WarehouseQueryByIdsReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.DeliveryTypeFullInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.KaDeliveryTypeItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.SellerDeliveryTypeItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.delivery.SellerSelfDeliveryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseDTO;
import com.aliyun.gts.gmall.platform.item.api.enums.SellerSelfDeliverySourceTypeEnum;
import com.aliyun.gts.gmall.platform.item.api.enums.SellerSelfDeliveryStatusEnum;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.delivery.SellerSelfDeliveryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.warehouse.WarehouseReadFacade;
import com.aliyun.gts.gmall.platform.item.common.enums.SellerLineRouteDimensionEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderCheckPayModeDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CustDeliveryFeeFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.FeeRulesFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.SameCityEnum;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeService;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易金额处理扩展点
 *    用来计算交易的营销和金额数据
 */
@Slf4j
@Component
public class DefaultPriceCalcExt implements PriceCalcExt {

    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Autowired
    private WarehouseReadFacade warehouseReadFacade;
    @Autowired
    private SellerSelfDeliveryReadFacade sellerSelfDeliveryReadFacade;
    @Autowired
    private CustDeliveryFeeFacade custDeliveryFeeFacade;
    @Autowired
    private FeeRulesFacade feeRulesFacade;
    @Autowired
    private PromotionConverter promotionConverter;

    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;

    @Autowired
    private DeliveryFeeService deliveryFeeService;

    @Autowired
    private CommercialReadFacade commercialReadFacade;

    /**
     * 营销计算查询
     * @param order
     * @param from
     * @return
     */
    @Override
    public OrderPromotion queryOrderPromotions(CreatingOrder order, QueryFrom from) {
        OrderPromotion query = promotionConverter.orderToPromotionQuery(order);
        return promotionRepository.queryOrderPromotion(query, from);
    }

    /**
     * 下单营销查询 -- 新版本
     */
    @Override
    public OrderPromotion queryOrderPromotionsNew(CreatingOrder order, QueryFrom from) {
        // 参数转换
        // OrderPromotion query = promotionConverter.orderToPromotionQuery(order);
        // 分期数转换
        List<Integer> installment = order.getInstallment();
        Map<Long, SellerPromotion> sellerPromotionMap = new HashMap<>();
        for (MainOrder main : order.getMainOrders()) {
            // 营销对象   getOrDefault()安全获取键对应的值，如果键存在，返回对应的值；如果键不存在，返回指定的默认值，第二个参数是默认值
            SellerPromotion sellerPromotion = sellerPromotionMap.getOrDefault(main.getSeller().getSellerId(), new SellerPromotion());
            if (Objects.isNull(sellerPromotion.getItems())) {
                sellerPromotion.setItems(new ArrayList<>());
            }
            List<ItemPromotion> itemPromotions = new ArrayList<>();
            // 遍历每个子订单 获取优惠信息
            for (SubOrder sub : main.getSubOrders()) {
                ItemPromotion itemPromotion = new ItemPromotion();
                itemPromotion.setSkuQty(sub.getOrderQty());
                itemPromotion.setItemSku(sub.getItemSku());
                itemPromotion.setItemSkuId(new ItemSkuId(
                    sub.getItemSku().getItemId(),
                    sub.getItemSku().getSkuId(),
                    main.getSeller().getSellerId(),
                    sub.getItemSku().getSkuQuoteId()
                ));
                itemPromotion.setOriginPrice(sub.getItemSku().getItemPrice().getOriginPrice());
                List<PayModeItemPrice> payModeItemPrices = new ArrayList<>();
                // 设置转换价格
                for (LoanPeriodDTO loanPeriodDTO : sub.getItemSku().getPriceList()) {
                    if (installment.stream().anyMatch(install -> install.equals(loanPeriodDTO.getType()))) {
                        PayModeItemPrice payModeItemPrice = new PayModeItemPrice();
                        payModeItemPrice.setSkuOrigPrice(loanPeriodDTO.getValue());
                        payModeItemPrice.setSellerId(main.getSeller().getSellerId());
                        payModeItemPrice.setPayMethod(String.format("installment_%s", loanPeriodDTO.getType()));
                        payModeItemPrices.add(payModeItemPrice);
                    }
                }
                itemPromotion.setPayModePrices(payModeItemPrices);
                itemPromotions.add(itemPromotion);
            }
            // 商品信息
            sellerPromotion.getItems().addAll(itemPromotions);
            // 卖家
            sellerPromotion.setSellerId(main.getSeller().getSellerId());
            sellerPromotionMap.put(main.getSeller().getSellerId(), sellerPromotion);
        }
        // 以卖家为单位请求营销
        OrderPromotion orderPromotion = new OrderPromotion();
        List<SellerPromotion> sellers = new ArrayList<>();
        for (Long sellerId : sellerPromotionMap.keySet()) {
            sellers.add(sellerPromotionMap.get(sellerId));
        }
        orderPromotion.setSellers(sellers);
        orderPromotion.setCustId(order.getCustId());
        orderPromotion.setOriginPayMode(order.getPayMode());
        //查询营销
        return promotionRepository.queryOrderPromotionNew(orderPromotion, from);
    }

    @Override
    public void afterOrderPromotions(CreatingOrder order, QueryFrom from) {

    }

    @Override
    public OrderPromotion queryCartPromotions(Cart cart) {
        OrderPromotion orderPromotion = promotionConverter.cartToPromotionQueryBySeller(cart);
        return promotionRepository.queryOrderPromotion(orderPromotion, QueryFrom.CART);
    }

    /**
     * 购物车营销查询  新版本(选中价格计算)
     * @param cart
     * 2025年2月13日09:51:05
     */
    @Override
    public OrderPromotion queryCartPromotionsNew(Cart cart) {
        OrderPromotion orderPromotion = promotionConverter.cartToPromotionQueryBySeller(cart);
        orderPromotion.setOriginPayMode(cart.getPayMode());
        return promotionRepository.queryOrderPromotionNew(orderPromotion, QueryFrom.CART);
    }

    @Override
    public List<ItemPromotion> queryCartItemPromotions(Cart cart) {
        List<ItemSku> itemList = cart.getGroups().stream()
            .flatMap(group -> group.getCartItems().stream())
            .filter(item -> !item.isItemNotFound() && item.getItemSku() != null)
            .map(item -> item.getItemSku())
            .collect(Collectors.toList());

        ItemPromotionQuery query = ItemPromotionQuery
            .builder()
            .list(itemList)
            .custId(cart.getCustId())
            .channel(cart.getChannel())
            .promotionSource(cart.getPromotionSource())
            .build();
        return promotionRepository.queryItemPromotion(query);
    }

    /**
     * 下单价格计算（营销之后的，包括运费、积分、IC原价 的汇总、分摊等）
     * @param order
     * @param from
     * 2024-12-12 09:50:17
     */
    @Override
    public void calcOrderPrices(CreatingOrder order, QueryFrom from) {
        if (order.getFreightFeeFree()) {
            //如果免费，则不再算运费
            order.getOrderPrice().setFreightAmt(0L);
        } else {
            // 运费查询、分摊 (运费按营销优惠价格分摊)
            processFreight(order, from);
        }
        /**
         * 计算积分分摊 (积分按金本位总价分摊)
         * TODO 暂时注释 后面用银行积分 计算抵扣金额 ， 不用商城积分 因此商城积分 不做抵扣 计算 2024-12-12 15:34:32
         */
        // processPoint(order);
        fillNotUsePoint(order);
        // 原价汇总
        processOriginPrice(order);
    }

    /**
     * 按积分金额分摊, 按分摊后的积分金额换算成原子积分个数
     * 通常积分汇率设置端要求 1分钱 = 整数的原子积分个数
     * 当 1分钱 != 整数的原子积分个数 时，下单积分个数按子订单取整后的积分个数累加
     */
    protected void processPoint(CreatingOrder order) {
        PointExchange pointExchange = pointExchangeRepository.getExchangeRate();
        OrderPrice orderPrice = order.getOrderPrice();
        // 不支持积分的情况
        if (!pointExchange.isSupportPoint()) {
            fillNotUsePoint(order);
            return;
        }
        // 户的积分金额
        Long custId = order.getCustomer().getCustId();
        long custPointCount = NumUtils.getNullZero(pointRepository.getUserPoint(custId));
        if (custPointCount < 0) {
            custPointCount = 0;
        }
        long custPointAmt = pointExchange.exCountToAmt(custPointCount);
        // request 指定使用的金额金额
        long requestPointCount = NumUtils.getNullZero(orderPrice.getPointCount());
        long requestPointAmt = pointExchange.exCountToAmt(requestPointCount);
        // 订单最大可用积分金额
        long orderTotalAmt = NumUtils.getNullZero(orderPrice.getOrderPromotionAmt()) + NumUtils.getNullZero(orderPrice.getFreightAmt());
        long orderMaxPointAmt = pointExchange.calcMaxDeductAmt(orderTotalAmt);
        // 实际使用的积分金额
        long realPointAmt = Math.min(custPointAmt, Math.min(requestPointAmt, orderMaxPointAmt));
        long orderPointCount = 0L;
        long orderMaxPointCount = 0L;
        // 积分金额分摊
        Divider mainDiv = new Divider(realPointAmt, orderTotalAmt, order.getMainOrders().size());
        for (MainOrder main : order.getMainOrders()) {
            OrderPrice mp = main.getOrderPrice();
            long mainTotalAmt = NumUtils.getNullZero(mp.getOrderPromotionAmt()) + NumUtils.getNullZero(mp.getFreightAmt());
            long mainPointAmt = mainDiv.next(mainTotalAmt);
            mp.setPointAmt(mainPointAmt);
            mp.setOrderRealAmt(mainTotalAmt - mainPointAmt);
            long mainPointCount = 0L;
            List<SubPrice> allSubPrice = main.getAllSubPrice();
            long factorSum = getOrderFactorSum(main);
            Divider subDiv = new Divider(mainPointAmt, factorSum, allSubPrice.size());
            for (SubPrice subPrice : allSubPrice) {
                OrderPrice sp = subPrice.getOrderPrice();
                long subTotalAmt = NumUtils.getNullZero(sp.getOrderPromotionAmt()) + getOrderFreightAmt(subPrice);
                long subPointAmt = subDiv.next(subTotalAmt);
                sp.setPointAmt(subPointAmt);
                sp.setOrderRealAmt(subTotalAmt - subPointAmt);
                // 积分数量
                long subPointCount = pointExchange.exAmtToCount(subPointAmt);
                sp.setPointCount(subPointCount);
                mainPointCount += subPointCount;
                // 最大数量
                long subMaxPointAmt = pointExchange.calcMaxDeductAmt(subTotalAmt);
                long subMaxCount = pointExchange.exAmtToCount(subMaxPointAmt);
                orderMaxPointCount += subMaxCount;
            }
            mp.setPointCount(mainPointCount);
            orderPointCount += mainPointCount;
            if (realPointAmt > 0) { // 用了积分的 才记录积分汇率
                main.orderAttr().setPointExchange(pointExchange);
            }
        }
        orderPrice.setPointAmt(realPointAmt);
        orderPrice.setOrderRealAmt(orderTotalAmt - realPointAmt);
        orderPrice.setPointCount(orderPointCount);
        // 最大可用积分数量, 返回给C端用
        orderPrice.putExtend(TradeExtendKeyConstants.MAX_AVAILABLE_POINT, String.valueOf(Math.min(orderMaxPointCount, custPointCount)));
    }

    /**
     * 不支持积分的 --- 哈利克项目不使用商城积分计算
     *  根据商品计算金额
     * @param order
     */
    protected void fillNotUsePoint(CreatingOrder order) {
        OrderPrice orderPrice = order.getOrderPrice();
        // 积分 全部都是0
        orderPrice.setPointAmt(0L);
        orderPrice.setPointCount(0L);
        // 计算 总金额
        long orderTotalAmt = NumUtils.getNullZero(orderPrice.getOrderPromotionAmt());
        orderPrice.setOrderRealAmt(orderTotalAmt);
        orderPrice.setOrderRealItemAmt(NumUtils.getNullZero(orderPrice.getOrderPromotionAmt()));
        PayModeCode payModeCode = PayModeCode.codeOf(order.getPayMode());
        //是否存在分期或者贷款
        boolean isLoanOrInstallment = buildInstallmentzPrice(payModeCode,order,orderPrice);
        // 每个订单计算
        for (MainOrder mainOrder : order.getMainOrders()) {
            // 每个订单的金额  总金额
            OrderPrice orderPriceMain = mainOrder.getOrderPrice();
            long mainTotalAmt = NumUtils.getNullZero(orderPriceMain.getOrderPromotionAmt());
            orderPriceMain.setOrderRealAmt(mainTotalAmt);
            orderPriceMain.setOrderRealItemAmt(NumUtils.getNullZero(orderPriceMain.getOrderPromotionAmt()));
            // 积分 全部都是0
            orderPriceMain.setPointAmt(0L);
            orderPriceMain.setPointCount(0L);
            //主订单分期每月价格。
            if (isLoanOrInstallment) {
               Long monthPromotionAmount =  Math.round((double)mainTotalAmt/payModeCode.getLoanNumber());
               orderPriceMain.setMonthPromotionAmount(monthPromotionAmount);
            }
            // 每个子订单的金额  总金额
            for (SubPrice subPrice : mainOrder.getAllSubPrice()) {
                OrderPrice orderPriceSubOrder = subPrice.getOrderPrice();
                // 计算 总金额
                long subTotalAmt = NumUtils.getNullZero(orderPriceSubOrder.getOrderPromotionAmt()) + NumUtils.getNullZero(orderPriceSubOrder.getFreightAmt());
                orderPriceSubOrder.setOrderRealAmt(subTotalAmt);
                orderPriceSubOrder.setOrderRealItemAmt(NumUtils.getNullZero(orderPriceSubOrder.getOrderPromotionAmt()));
                // 积分 全部都是0
                orderPriceSubOrder.setPointAmt(0L);
                orderPriceSubOrder.setPointCount(0L);
            }
        }
        orderPrice.putExtend(TradeExtendKeyConstants.MAX_AVAILABLE_POINT, String.valueOf(0));
    }

    private boolean buildInstallmentzPrice(PayModeCode payModeCode, CreatingOrder order,OrderPrice orderPrice) {
        boolean isLoanOrInstallment = false;
        if (Objects.nonNull(payModeCode)) {
            List<OrderCheckPayModeDTO> installPriceList = new ArrayList<>();
            if (PayModeCode.isInstallment(payModeCode)) {
                isLoanOrInstallment = true;
                installPriceList = order.getInstallPriceList();
            }
            else if(PayModeCode.isInstallment(payModeCode)) {
                isLoanOrInstallment = true;
                installPriceList = order.getLoanPriceList();
            }
            if(CollectionUtils.isNotEmpty(installPriceList))
            {
                OrderCheckPayModeDTO orderCheckPayModeDTO = installPriceList.stream().filter(e -> e.getPayMode().equals(order.getPayMode())).findFirst().orElse(null);
                if (Objects.nonNull(orderCheckPayModeDTO)) {
                    orderPrice.setMonthAmount(orderCheckPayModeDTO.getMonthAmount());
                    orderPrice.setMonthPromotionAmount(orderCheckPayModeDTO.getMonthPromotiomAmount());
                }
            }
        }
        return isLoanOrInstallment;
    }

    /**
     * 查询、分摊运费
     * @param order
     * @param from
     */
    protected void processFreight(CreatingOrder order, QueryFrom from) {
        long total = 0L;
        // 每个订单逐个计算
        for (MainOrder main : order.getMainOrders()) {
            // 计算运费
            long freight = processFreight(main, order, from);
            main.setFreightAmount(freight);
            total += freight;
        }
        order.getOrderPrice().setFreightAmt(total);
    }

    /**
     * 查询、分摊运费 每个订单运费计算
     * @param main
     * @param order
     * @param from
     * @return long
     */
    protected long processFreight(MainOrder main, CreatingOrder order, QueryFrom from) {
        boolean allowEmptyFreight = from != QueryFrom.CREATE_ORDER;
        Long freight = 0L;
        if(from == QueryFrom.CONFIRM_ORDER){
            freight = calculateFreight(main, order);
        }
        if (freight == null) {
            if (allowEmptyFreight) {
                order.addNonblockFail(ErrorUtils.getNonblockFail(main.getSeller().getSellerId(), OrderErrorCode.RECEIVER_NOT_SUPPORT_BY_ITEM));
                freight = 0L;
            } else {
                throw new GmallException(OrderErrorCode.RECEIVER_NOT_SUPPORT_BY_ITEM);
            }
        }
        main.getOrderPrice().setFreightAmt(freight);
        // 运费不分摊到子单
        //Divider div = new Divider(freight, main.getOrderPrice().getOrderPromotionAmt(), main.getSubOrders().size());
        for (SubOrder sub : main.getSubOrders()) {
            //Long divFreight = div.next(sub.getOrderPrice().getOrderPromotionAmt());
            sub.getOrderPrice().setFreightAmt(0L);
        }
        main.orderAttr().getFreightPrice().setFreightAmt(freight);
        return freight;
    }

    /**
     * 配送运费计算 ， 每个单子单独计算
     * @param main
     * @param order
     * @return
     */
    private Long calculateFreight(MainOrder main, CreatingOrder order) {
        Long freight = 0L;
        //交际仓库
        List<Long> warehouseIdList =  main.getSubOrders()
            .stream()
            .map(SubOrder::getItemSku)
            .map(ItemSku::getWarehouseIdList)
            .filter(Objects::nonNull)  // 过滤掉空集合
            .reduce((a, b) -> {
                //用 retainAll 来保留两个集合的交集，并将结果传递给下一个迭代。
                List<Long> aTemp = new ArrayList<>(a);
                List<Long> bTemp = new ArrayList<>(b);
                aTemp.retainAll(bTemp);
                return aTemp;
            }).orElse(new ArrayList<>());
        WarehouseQueryByIdsReq warehouseQueryByIdsReq = new WarehouseQueryByIdsReq();
        warehouseQueryByIdsReq.setIds(new HashSet<>(warehouseIdList));
        RpcResponse<List<WarehouseDTO>> warehouseResult = warehouseReadFacade.queryByIds(warehouseQueryByIdsReq);
        List<WarehouseDTO> warehouseResp = warehouseResult.getData();
        List<String> cityCodeList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(warehouseResp)){
            cityCodeList = warehouseResp.stream().map(WarehouseDTO::getCityCode).collect(Collectors.toList());
        }
        List<String> selfCodeList = new ArrayList<>();
        selfCodeList.add("7");
        selfCodeList.add("8");
        if(selfCodeList.contains(main.getLogisticsType())){
            if(StringUtils.equals(main.getLogisticsType(),"8")){
                freight = 0L;
            }else {
                SellerSelfDeliveryQueryReq req = new SellerSelfDeliveryQueryReq();
                req.setDepartureCityCodeList(cityCodeList);
                if(Objects.isNull(main.getReceiver()))
                {
                    freight = 0L;
                }
                else {
                    req.setDestinationCityCode(main.getReceiver().getCityCode());
                    List<Long> sellerIds = new ArrayList<>();
                    sellerIds.add(main.getSeller().getSellerId());
                    req.setSellerIds(sellerIds);
                    req.setCategoryId(main.getSubOrders().get(0).getItemSku().getCategoryId());
                    RpcResponse<List<SellerSelfDeliveryDTO>> result = sellerSelfDeliveryReadFacade.queryForMinDeliveryPeriod(req);
                    if (null != result && CollectionUtils.isNotEmpty(result.getData())) {
                        SellerSelfDeliveryDTO deliveryInfo = result.getData().get(0);
                        if (order.getOrderPrice().getOrderPromotionAmt() > deliveryInfo.getFreeDeliveryThreshold()) {
                            freight = 0L;
                        } else {
                            freight = result.getData().get(0).getCostOfDelivery();
                        }
                    }
                }
            }

        }else{
            CustDeliveryFeeRpcReq custDeliveryFeeRpcReq = new CustDeliveryFeeRpcReq();
            int deliveryRoute = 2;
            if(Objects.nonNull(main.getReceiver()) && Objects.nonNull(main.getReceiver().getCityCode())&& cityCodeList.contains(main.getReceiver().getCityCode())){
                deliveryRoute = 1;
            }
            custDeliveryFeeRpcReq.setDeliveryRoute(deliveryRoute);
            custDeliveryFeeRpcReq.setDeliveryType(StringUtils.isBlank(main.getLogisticsType())|| "null".equals(main.getLogisticsType()) ? null : Integer.valueOf(main.getLogisticsType()));
            RpcResponse<CustDeliveryFeeDTO> hmFreightResult = custDeliveryFeeFacade.custDeliveryFeeDetail(custDeliveryFeeRpcReq);
            if(hmFreightResult!=null && hmFreightResult.getData()!=null){
                freight = hmFreightResult.getData().getFee();
            }else {
                freight = 0L;
            }
        }

        return freight;
    }

    /**
     * 原价求和 ，所有的商品 单价* 数量 得到原价总和
     * @param order
     */
    protected void processOriginPrice(CreatingOrder order) {
        // 全部金额
        long totalPrice = 0L;
        for (MainOrder main : order.getMainOrders()) {
            // 每个主单的金额
            long mainPrice = 0L;
            for (SubOrder sub : main.getSubOrders()) {
                if (Objects.isNull(sub.getItemSku()) || Objects.isNull(sub.getItemSku().getItemPrice()) || Objects.isNull(sub.getItemSku().getItemPrice().getOriginPrice())) {
                    sub.getOrderPrice().setItemOriginAmt(0L);
                } else {
                    Long salePrice = sub.getItemSku().getItemPrice().getOriginPrice();
                    int qty = sub.getOrderQty();
                    long subPrice = salePrice * qty;
                    mainPrice += subPrice;
                    totalPrice += subPrice;
                    sub.getOrderPrice().setItemOriginAmt(subPrice);
                }

            }
            main.getOrderPrice().setItemOriginAmt(mainPrice);
        }
        order.getOrderPrice().setItemOriginAmt(totalPrice);
    }

    /**
     * 获取订单分摊的总的权重  一般就是订单优惠后金额+邮费
     * @param main
     * @return
     */
    protected long getOrderFactorSum(MainOrder main){
        return NumUtils.getNullZero(main.getOrderPrice().getOrderPromotionAmt()) +
            NumUtils.getNullZero(main.getOrderPrice().getFreightAmt());
    }

    /**
     * 获取子订单上的邮费
     * @param sp
     * @return
     */
    protected long getOrderFreightAmt(SubPrice sp){
        return NumUtils.getNullZero(sp.getOrderPrice().getFreightAmt());
    }

    /**
     * 金额计算
     *    商品金额计算
     *    优惠金额计算
     *    运费计算
     *        发货地点来自 商品拆单时候商品的仓储地址
     *        HM物流
     *        送货上门
     *        银行网点
     *        快递柜
     *              收费 按照主单收费 所有子单金额的和，实付金额
     *              同城  大于4000 配置文件
     *              跨城市 大于10000 配置文件
     *        自有物流
     *              收费 按照主单收费 所有子单金额的和，实付金额
     *              城市到城市的路径  从拆单里面算出来的 仓库出发城市  到收货地点城市之间的数据查询
     *              查询到数据后返回配置的金额 和订单金额算是否保存 ，包邮 0运费 不包邮 收配置的运维
     *        上门自提 不要运费
     *        每一个单是一个运费计算 如果一个主单多个商品 求和后算是否达到免费的标准
     * @param
     * @return
     */
    @Override
    public void calcOrderPriceNew(CreatingOrder order, QueryFrom from) {
        //// 获取计算运费配置,如果配置用新接口
        if (Objects.nonNull(tradeLimitConfiguration.getCalSwitch()) && tradeLimitConfiguration.getCalSwitch().equals(1)) {
            calcOrderPriceV2New(order, from);
            return;
        }
        // 计算每个订单的主单和子单的金额 并带上营销金额
        fillNotUsePoint(order);
        // 原价汇总
        processOriginPrice(order);
        //计算折扣价格汇总
        processDisCountPrice(order);

        // 运费计算
        //如果免费，则不再算运费 ---- 只有赠品免邮场景
        if (order.getFreightFeeFree()) {
            order.getOrderPrice().setFreightAmt(0L);
            return;
        }
        Long freight = 0L;
        Long totalDeliveryMerchantFee = 0L;
        // 判断是否达到免邮标准，达到了 直接0运费
        RpcResponse<FeeRulesDTO> feeRulesDTORpcResponse = feeRulesFacade.queryfeeRules();
        // 运费查询、分摊 (运费按营销优惠价格分摊)
        for (MainOrder mainOrders : order.getMainOrders()) {
            /**
             * 主单物流方式 为空 没有运费 数据不完善 ，为空
             * 数据不完善的场景只会出现在 订单确认 下单接口前置会check
             */
            if (Objects.isNull(mainOrders.getReceiver()) ||
                Objects.isNull(mainOrders.getDeliveryType()) ||
                Objects.isNull(mainOrders.getReceiver().getReceiverId())) {
                settingZeroFreight(mainOrders);
                continue;
            }
            // 自提单自提 没有运费
            if (DeliveryTypeEnum.WAREHOURSE_PICK_UP.getCode().equals(mainOrders.getDeliveryType())) {
                settingZeroFreight(mainOrders);
                continue;
            }
            /**
             * HM物流 计算逻辑
             * 送货上门 PVZ POSTMART
             */
            if (DeliveryTypeEnum.isHM(mainOrders.getDeliveryType())) {
                // 订单运费金额 ，每个子单求和
                Long orderFreightAmount = 0L;
                // 发货点是否同城
                boolean isSameCity = CollectionUtils.isEmpty(mainOrders.getWarehourseStockList()) ?
                    Boolean.FALSE :
                    mainOrders.getWarehourseStockList()
                        .stream()
                        .anyMatch(skuQuoteWarehourseStockDTO -> mainOrders.getCityCode().equals(skuQuoteWarehourseStockDTO.getStockCityCode()));
                if (Objects.nonNull(feeRulesDTORpcResponse) &&
                    Boolean.TRUE.equals(feeRulesDTORpcResponse.isSuccess()) &&
                    Objects.nonNull(feeRulesDTORpcResponse.getData())) {
                    // 运费商家计算
                    Long deliveryMerchantFee = queryDeliveryMerchantFee(mainOrders, feeRulesDTORpcResponse.getData(), isSameCity);
                    //计算商家运费佣金
                    totalDeliveryMerchantFee += deliveryMerchantFee;
                    // 同城且大于设置金额包邮
                    if (Boolean.TRUE.equals(isSameCity) &&
                        mainOrders.getOrderPrice().getOrderPromotionAmt() > feeRulesDTORpcResponse.getData().getCustAmount()) {
                        settingZeroFreight(mainOrders);
                        continue;
                    }
                    // 非同城且大于设置金额包邮
                    if (Boolean.FALSE.equals(isSameCity) &&
                        mainOrders.getOrderPrice().getOrderPromotionAmt() > feeRulesDTORpcResponse.getData().getInterCustAmount()) {
                        settingZeroFreight(mainOrders);
                        continue;
                    }
                }
                //不包邮的场景计算  其实很少 计算每个子单物流运费 求和即可
                for (SubOrder subOrder : mainOrders.getSubOrders()) {
                    CustDeliveryFeeRpcReq custDeliveryFeeRpcReq = new CustDeliveryFeeRpcReq();
                    // 物流方式
                    custDeliveryFeeRpcReq.setDeliveryType(subOrder.getDeliveryType());
                    Integer deliveryRoute=isSameCity ? SameCityEnum.IS_SAME.getCode() : SameCityEnum.NOT_SAME.getCode();
                    custDeliveryFeeRpcReq.setDeliveryRoute(deliveryRoute);
                    custDeliveryFeeRpcReq.setActive(CommonConstant.FEE_ACTIVE);
                    // 是否同城物流
                    RpcResponse<CustDeliveryFeeDTO> custDeliveryFeeDetail = custDeliveryFeeFacade.custDeliveryFeeDetail(custDeliveryFeeRpcReq);
                    // 默认为0
                    subOrder.setFreightAmount(0L);
                    if (Boolean.TRUE.equals(custDeliveryFeeDetail.isSuccess()) &&
                        Objects.nonNull(custDeliveryFeeDetail.getData())) {
                        //子单运费金额
                        subOrder.setFreightAmount(custDeliveryFeeDetail.getData().getFee());
                        //主单运费取一个
                        orderFreightAmount = custDeliveryFeeDetail.getData().getFee();
                    }
                }
                // 订单运费金额
                mainOrders.setFreightAmount(orderFreightAmount);
                if (Objects.nonNull(mainOrders.getOrderPrice())) {
                    mainOrders.getOrderPrice().setFreightAmt(orderFreightAmount);
                    long orderTotalAmt = NumUtils.getNullZero(mainOrders.getOrderPrice().getOrderRealAmt());
                    mainOrders.getOrderPrice().setOrderRealAmt(orderTotalAmt + orderFreightAmount);

                }
                // 运费总金额
                freight += orderFreightAmount;
            }
            // 自有物流计算
            if (DeliveryTypeEnum.SELF_SERVICE.getCode().equals(mainOrders.getDeliveryType())) {
                freight =  countSelfDeliveryFees(mainOrders,freight, SellerSelfDeliverySourceTypeEnum.SELLER.getCode());
            }
            else if (DeliveryTypeEnum.SELLER_KA.getCode().equals(mainOrders.getDeliveryType())) {
                freight =  countSelfDeliveryFees(mainOrders,freight, SellerSelfDeliverySourceTypeEnum.ADMIN.getCode());
            }
        }
        // 运费总和
        order.getOrderPrice().setFreightAmt(freight);
        Long original = NumUtils.getNullZero(order.getOrderPrice().getOrderRealAmt());
        order.getOrderPrice().setOrderRealAmt(original + freight);
        order.getOrderPrice().setDeliveryMerchantFee(totalDeliveryMerchantFee);
    }


    /**
     * 计算卖家门槛金额
     * @param mainOrders
     * @param isSameCity
     * @return
     */
    private Long  queryDeliveryMerchantFee(MainOrder mainOrders, FeeRulesDTO  feeRulesDTO ,boolean isSameCity) {
        //不包邮的场景计算  其实很少 计算每个子单物流运费 求和即可
        Long deliveryMerchantFee = 0L;
        // 是否收取 邮费门槛
        Long merchantAmount = isSameCity ? feeRulesDTO.getMerchantAmount() : feeRulesDTO.getInterMerchantAmount();
        // 实付金额大于门槛
        if (mainOrders.getOrderPrice().getOrderPromotionAmt() > merchantAmount) {
            return deliveryMerchantFee;
        }
        for (SubOrder subOrder : mainOrders.getSubOrders()) {
            Integer deliveryRoute = isSameCity ? SameCityEnum.IS_SAME.getCode() : SameCityEnum.NOT_SAME.getCode();
            DeliveryFeeQueryRpcReq req = new DeliveryFeeQueryRpcReq();
            // 同城 物流方式 卖家 分类四个条件
            req.setDeliveryRoute(deliveryRoute);
            req.setDeliveryType(subOrder.getDeliveryType());
            req.setMerchantCode(String.valueOf(subOrder.getSellerId()));
            req.setCategoryId(String.valueOf(subOrder.getItemSku().getCategoryId()));
            Long subDeliveryFee = deliveryFeeService.deliveryMerchantFee(req);
            subOrder.setDeliveryMerchantFee(subDeliveryFee);
            subOrder.getOrderPrice().setDeliveryMerchantFee(subDeliveryFee);
            deliveryMerchantFee = subDeliveryFee;
        }
        mainOrders.getOrderPrice().setDeliveryMerchantFee(deliveryMerchantFee);
        mainOrders.setDeliveryMerchantFee(deliveryMerchantFee);
        return deliveryMerchantFee;
    }

    /**
     * 自有物流
     * @param mainOrders
     * @param freight
     * @param sellerSelfDeliverySourceType
     * @return
     */
    private Long countSelfDeliveryFees(MainOrder mainOrders, Long freight, String sellerSelfDeliverySourceType) {
        // 订单运费金额 ，每个子单阈值最大的运费
        Long orderFreightAmount = 0L;
        SellerSelfDeliveryQueryReq sellerSelfDeliveryQueryReq = new SellerSelfDeliveryQueryReq();
        // 发货城市集合
        List<String> departureCityCodeList = CollectionUtils.isEmpty(mainOrders.getWarehourseStockList()) ?
            new ArrayList<>() :
            mainOrders.getWarehourseStockList()
                .stream()
                .map(SkuQuoteWarehourseStockDTO::getStockCityCode)
                .collect(Collectors.toList());
        // 卖家ID
        List<Long> sellerIds = new ArrayList<>();
        sellerIds.add(mainOrders.getSeller().getSellerId());
        sellerSelfDeliveryQueryReq.setSellerIds(sellerIds);
        // 启用的
        sellerSelfDeliveryQueryReq.setStatus(SellerSelfDeliveryStatusEnum.ENABLE.getCode());
        sellerSelfDeliveryQueryReq.setSourceType(sellerSelfDeliverySourceType);
        int size = mainOrders.getSubOrders().size();
        if (size == 1) {
            orderFreightAmount = countSingleOrderMinFreight(mainOrders,mainOrders.getSubOrders().get(0), sellerSelfDeliveryQueryReq,departureCityCodeList);
        }
        else{
            orderFreightAmount = countBatchOrderMaxThreHoldFreight(mainOrders, sellerSelfDeliveryQueryReq,departureCityCodeList);
        }
        // 运费总金额
        freight += orderFreightAmount;
        mainOrders.setFreightAmount(orderFreightAmount);
        if (mainOrders.getOrderPrice() != null) {
            mainOrders.getOrderPrice().setFreightAmt(orderFreightAmount);
            long orderTotalAmt = NumUtils.getNullZero(mainOrders.getOrderPrice().getOrderRealAmt());
            mainOrders.getOrderPrice().setOrderRealAmt(orderTotalAmt + orderFreightAmount);
        }
        return freight;
    }

    /**
     * 批量子单运费计算
     * @param mainOrder
     * @param sellerSelfDeliveryQueryReq
     * @param departureCityCodeList
     * @return
     */
    private Long countBatchOrderMaxThreHoldFreight(
        MainOrder mainOrder,
        SellerSelfDeliveryQueryReq
        sellerSelfDeliveryQueryReq,
        List<String> departureCityCodeList) {
        // 取最大的运费门栏金额
        Long freeDeliveryThreshold = 0L;
        Long orderFreightAmount = 0L;
        // 计算每个子单物流运费
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            // 收货城市
            sellerSelfDeliveryQueryReq.setDestinationCityCode(subOrder.getCityCode());
            // 商品分类
            sellerSelfDeliveryQueryReq.setCategoryId(subOrder.getItemSku().getCategoryId());
            for(String departureCityCode : departureCityCodeList) {
                List<String> singleDepartureCityCodeList = new ArrayList<>();
                singleDepartureCityCodeList.add(departureCityCode);
                sellerSelfDeliveryQueryReq.setDepartureCityCodeList(singleDepartureCityCodeList);
                RpcResponse<List<SellerSelfDeliveryDTO>> rpcResponse = sellerSelfDeliveryReadFacade.queryForMinDeliveryPeriod(sellerSelfDeliveryQueryReq);
                if (Objects.nonNull(rpcResponse) &&
                    Boolean.TRUE.equals(rpcResponse.isSuccess()) &&
                    CollectionUtils.isNotEmpty(rpcResponse.getData())) {
                    // 只会返回最便宜的物流费用
                    List<SellerSelfDeliveryDTO> sellerSelfDeliveryDTOS = rpcResponse.getData();
                    for (SellerSelfDeliveryDTO sellerSelfDelivery : sellerSelfDeliveryDTOS) {
                        //先给每个子单加上运费，后续如果阈值不是最大，赋值为0
                        subOrder.setFreightAmount(sellerSelfDelivery.getCostOfDelivery());
                        if (sellerSelfDelivery.getFreeDeliveryThreshold() > freeDeliveryThreshold) {
                            //子订单取阈值下最大
                            freeDeliveryThreshold = sellerSelfDelivery.getFreeDeliveryThreshold();
                            //主单取阈值最大的运费
                            orderFreightAmount = sellerSelfDelivery.getCostOfDelivery();
                        } else if (freeDeliveryThreshold.equals(sellerSelfDelivery.getFreeDeliveryThreshold())) {
                            if (sellerSelfDelivery.getCostOfDelivery() > orderFreightAmount) {
                                //相同运费阈值，取运费最大的
                                orderFreightAmount = sellerSelfDelivery.getCostOfDelivery();
                            }
                        }
                    }
                }
            }
        }
        if (mainOrder.getOrderPrice().getOrderPromotionAmt() > freeDeliveryThreshold) {
            settingZeroFreight(mainOrder);
            //货品营销价总额超过满邮标准，主单运费设置为0
            orderFreightAmount = 0L;
        }
        return orderFreightAmount;
    }


    /**
     * 单个订单运费
     * @param mainOrder
     * @param subOrder
     * @param sellerSelfDeliveryQueryReq
     * @param departureCityCodeList
     * @return
     */
    private Long countSingleOrderMinFreight(
        MainOrder mainOrder,
        SubOrder subOrder,
        SellerSelfDeliveryQueryReq sellerSelfDeliveryQueryReq,
        List<String> departureCityCodeList) {
        // 取最大的运费门栏金额
        Long freeDeliveryThreshold = 0L;
        Long orderFreightAmount = 0L;
        sellerSelfDeliveryQueryReq.setDepartureCityCodeList(departureCityCodeList);
        // 收货城市
        sellerSelfDeliveryQueryReq.setDestinationCityCode(subOrder.getCityCode());
        // 商品分类
        sellerSelfDeliveryQueryReq.setCategoryId(subOrder.getItemSku().getCategoryId());
        RpcResponse<List<SellerSelfDeliveryDTO>> rpcResponse = sellerSelfDeliveryReadFacade.queryForMinDeliveryPeriod(sellerSelfDeliveryQueryReq);
        if (Objects.nonNull(rpcResponse) &&
            Boolean.TRUE.equals(rpcResponse.isSuccess()) &&
            CollectionUtils.isNotEmpty(rpcResponse.getData())) {
            // 只会返回最便宜的物流费用
            List<SellerSelfDeliveryDTO> sellerSelfDeliveryDTOS = rpcResponse.getData();
            for (SellerSelfDeliveryDTO sellerSelfDelivery : sellerSelfDeliveryDTOS) {
                freeDeliveryThreshold = freeDeliveryThreshold >
                    sellerSelfDelivery.getFreeDeliveryThreshold() ?
                    freeDeliveryThreshold : sellerSelfDelivery.getFreeDeliveryThreshold();
                //先给每个子单加上运费，后续如果阈值不是最大，赋值为0
                subOrder.setFreightAmount(sellerSelfDelivery.getCostOfDelivery());
                orderFreightAmount = sellerSelfDelivery.getCostOfDelivery();
            }
        }
        if (mainOrder.getOrderPrice().getOrderPromotionAmt() > freeDeliveryThreshold) {
            settingZeroFreight(mainOrder);
            //货品营销价总额超过满邮标准，主单运费设置为0
            orderFreightAmount = 0L;
        }
        return orderFreightAmount;
    }

    /**
     * 计算折扣后的的价格
     * @param order
     */
    protected void processDisCountPrice(CreatingOrder order) {
        // 全部金额-优惠后支付金额= 折扣金额
        long discountPrice = NumUtils.getNullZero(order.getOrderPrice().getItemOriginAmt()) -
            NumUtils.getNullZero(order.getOrderPrice().getOrderPromotionAmt());
        // 折扣金额
        order.getOrderPrice().setItemDisCountAmt(discountPrice);
        for (MainOrder main : order.getMainOrders()) {
            // 每个主单的金额-优惠后支付金额= 折扣金额
            long mainDiscountPrice = NumUtils.getNullZero(main.getOrderPrice().getItemOriginAmt()) -
                NumUtils.getNullZero(main.getOrderPrice().getOrderPromotionAmt());
            // 折扣金额
            main.getOrderPrice().setItemDisCountAmt(mainDiscountPrice);
            for (SubOrder sub : main.getSubOrders()) {
                // 每个子单的金额-优惠后支付金额= 折扣金额
                long subDiscountPrice = NumUtils.getNullZero(sub.getOrderPrice().getItemOriginAmt()) -
                    NumUtils.getNullZero(sub.getOrderPrice().getOrderPromotionAmt());
                // 折扣金额
                sub.getOrderPrice().setItemDisCountAmt(subDiscountPrice);
            }
        }
    }

    /**
     * 设置金额为0
     * @param mainOrders
     */
    private void settingZeroFreight(MainOrder mainOrders) {
        mainOrders.setFreightAmount(0L);
        if (Objects.nonNull(mainOrders.getOrderPrice())) {
            mainOrders.getOrderPrice().setFreightAmt(0L);
        }
        mainOrders.getSubOrders().stream().forEach(subOrder -> {
            subOrder.setFreightAmount(0L);
            if (Objects.nonNull(subOrder.getOrderPrice())) {
                subOrder.getOrderPrice().setFreightAmt(0L);
            }
        });
    }


    /**
     * 金额计算
     * 商品金额计算
     * 优惠金额计算
     * 运费计算
     * 发货地点来自 商品拆单时候商品的仓储地址
     * HM物流
     * 送货上门
     * 银行网点
     * 快递柜
     * 收费 按照主单收费 所有子单金额的和，实付金额
     * 同城  大于4000 配置文件
     * 跨城市 大于10000 配置文件
     * 自有物流
     * 收费 按照主单收费 所有子单金额的和，实付金额
     * 城市到城市的路径  从拆单里面算出来的 仓库出发城市  到收货地点城市之间的数据查询
     * 查询到数据后返回配置的金额 和订单金额算是否保存 ，包邮 0运费 不包邮 收配置的运维
     * 上门自提 不要运费
     * 每一个单是一个运费计算 如果一个主单多个商品 求和后算是否达到免费的标准
     *
     * @param
     * @return
     */
    @Override
    public void calcOrderPriceV2New(CreatingOrder order, QueryFrom from) {
        // 计算每个订单的主单和子单的金额 并带上营销金额
        fillNotUsePoint(order);
        // 原价汇总
        processOriginPrice(order);
        //计算折扣价格汇总
        processDisCountPrice(order);

        // 运费计算
        //如果免费，则不再算运费 ---- 只有赠品免邮场景
        if (order.getFreightFeeFree()) {
            order.getOrderPrice().setFreightAmt(0L);
            return;
        }
        Long freight = 0L;
        Long totalDeliveryMerchantFee = 0L;
        // 判断是否达到免邮标准，达到了 直接0运费
        RpcResponse<FeeRulesDTO> feeRulesDTORpcResponse = feeRulesFacade.queryfeeRules();
        // 运费查询、分摊 (运费按营销优惠价格分摊)
        for (MainOrder mainOrders : order.getMainOrders()) {
            /**
             * 主单物流方式 为空 没有运费 数据不完善 ，为空
             * 数据不完善的场景只会出现在 订单确认 下单接口前置会check
             */
            if (Objects.isNull(mainOrders.getReceiver()) ||
                    Objects.isNull(mainOrders.getDeliveryType()) ||
                    Objects.isNull(mainOrders.getReceiver().getReceiverId())) {
                settingZeroFreight(mainOrders);
                continue;
            }
            // 自提单自提 没有运费
            if (DeliveryTypeEnum.WAREHOURSE_PICK_UP.getCode().equals(mainOrders.getDeliveryType())) {
                settingZeroFreight(mainOrders);
                continue;
            }
            /**
             * HM物流 计算逻辑
             * 送货上门 PVZ POSTMART
             */
            //HM物流：海外仓
            if (DeliveryTypeEnum.isHM(mainOrders.getDeliveryType())) {
                // 订单运费金额 ，每个子单求和
                Long orderFreightAmount = 0L;
                // 发货点是否同城
                boolean isSameCity = CollectionUtils.isEmpty(mainOrders.getWarehourseStockList()) ?
                        Boolean.FALSE :
                        mainOrders.getWarehourseStockList()
                                .stream()
                                .anyMatch(skuQuoteWarehourseStockDTO -> mainOrders.getCityCode().equals(skuQuoteWarehourseStockDTO.getStockCityCode()));
                if (Objects.nonNull(feeRulesDTORpcResponse) &&
                        Boolean.TRUE.equals(feeRulesDTORpcResponse.isSuccess()) &&
                        Objects.nonNull(feeRulesDTORpcResponse.getData())) {
                    // 运费商家计算
                    Long deliveryMerchantFee = queryDeliveryMerchantFee(mainOrders, feeRulesDTORpcResponse.getData(), isSameCity);
                    //计算商家运费佣金
                    totalDeliveryMerchantFee += deliveryMerchantFee;
                    // 同城且大于设置金额包邮
                    if (Boolean.TRUE.equals(isSameCity) &&
                            mainOrders.getOrderPrice().getOrderPromotionAmt() > feeRulesDTORpcResponse.getData().getCustAmount()) {
                        settingZeroFreight(mainOrders);
                        continue;
                    }
                    // 非同城且大于设置金额包邮
                    if (Boolean.FALSE.equals(isSameCity) &&
                            mainOrders.getOrderPrice().getOrderPromotionAmt() > feeRulesDTORpcResponse.getData().getInterCustAmount()) {
                        settingZeroFreight(mainOrders);
                        continue;
                    }
                }

                //不包邮的场景计算  其实很少 计算每个子单物流运费
                for (SubOrder subOrder : mainOrders.getSubOrders()) {
                    // 是否同城物流
                    DeliveryTypeFullInfoDTO deliveryTypeFullInfoDTO = queryDeliveryTypeFullInfoDTO(subOrder, SellerLineRouteDimensionEnum.CHEAPEST.getCode());
                    // 默认为0
                    long shipFee = 0L;
                    subOrder.setFreightAmount(shipFee);
                    if (Objects.nonNull(deliveryTypeFullInfoDTO) && Objects.nonNull(deliveryTypeFullInfoDTO.getHmDeliveryDto())) {

                        if (DeliveryTypeEnum.PVZ.getCode().equals(mainOrders.getDeliveryType())) {
                            shipFee = deliveryTypeFullInfoDTO.getHmDeliveryDto().getPvzShippingFee();
                        }
                        else if (DeliveryTypeEnum.POSTAMAT.getCode().equals(mainOrders.getDeliveryType())) {
                            shipFee = deliveryTypeFullInfoDTO.getHmDeliveryDto().getPostmantShippingFee();
                        }
                        else {
                            //子单运费金额
                            shipFee = deliveryTypeFullInfoDTO.getHmDeliveryDto().getShippingFee();
                        }
                        if(shipFee > 0L) {
                            subOrder.setFreightAmount(shipFee);
                            //主单运费取一个
                            orderFreightAmount = shipFee;
                        }
                    }
                }
                // 订单运费金额
                mainOrders.setFreightAmount(orderFreightAmount);
                if (Objects.nonNull(mainOrders.getOrderPrice())) {
                    mainOrders.getOrderPrice().setFreightAmt(orderFreightAmount);
                    long orderTotalAmt = NumUtils.getNullZero(mainOrders.getOrderPrice().getOrderRealAmt());
                    mainOrders.getOrderPrice().setOrderRealAmt(orderTotalAmt + orderFreightAmount);

                }
                // 运费总金额
                freight += orderFreightAmount;
            }
            // 自有物流计算
            if (DeliveryTypeEnum.SELLER_KA.getCode().equals(mainOrders.getDeliveryType()) || DeliveryTypeEnum.SELF_SERVICE.getCode().equals(mainOrders.getDeliveryType())) {
                // 自有物流计算
                freight = countSelfDeliveryFeesV2(mainOrders, freight);
            }
        }
        // 运费总和
        order.getOrderPrice().setFreightAmt(freight);
        Long original = NumUtils.getNullZero(order.getOrderPrice().getOrderRealAmt());
        order.getOrderPrice().setOrderRealAmt(original + freight);
        order.getOrderPrice().setDeliveryMerchantFee(totalDeliveryMerchantFee);
    }

    DeliveryTypeFullInfoDTO queryDeliveryTypeFullInfoDTO(SubOrder subOrder, Integer sellerLineRouteDimension) {
        // step4 查询商品支出的物流方式
        DeliveryTypeFullInfoQueryReq deliveryTypeFullInfoQueryReq = new DeliveryTypeFullInfoQueryReq();
        deliveryTypeFullInfoQueryReq.setSkuId(subOrder.getItemSku().getSkuId());
        List<Long> sellerIds = new ArrayList<>();
        sellerIds.add(subOrder.getSellerId());
        deliveryTypeFullInfoQueryReq.setSellerIds(sellerIds);
        deliveryTypeFullInfoQueryReq.setCityCode(subOrder.getCityCode());
        deliveryTypeFullInfoQueryReq.setCategoryId(subOrder.getItemSku().getCategoryId());
        List<Long> skuQuoteIds = new ArrayList<>();
        skuQuoteIds.add(subOrder.getSkuQuoteId());
        deliveryTypeFullInfoQueryReq.setSkuQuoteIds(skuQuoteIds);
        deliveryTypeFullInfoQueryReq.setQueryTimeliness(true);
        deliveryTypeFullInfoQueryReq.setSellerLineRouteDimension(sellerLineRouteDimension);
        RpcResponse<List<DeliveryTypeFullInfoDTO>> deliveryTypeResult = commercialReadFacade.queryDeliveryTypeFullInfo(deliveryTypeFullInfoQueryReq);
        // 解析结果 获取支持的物流方式和运费
        log.info("commercialReadFacade.queryDeliveryTypeFullInfo deliveryTypeResult:{}", deliveryTypeResult);
        if (Objects.nonNull(deliveryTypeResult) &&
                deliveryTypeResult.isSuccess() &&
                CollectionUtils.isNotEmpty(deliveryTypeResult.getData())) {
            return deliveryTypeResult.getData().get(0);
        }
        return null;
    }


    /**
     * 自有物流
     *
     * @param mainOrder
     * @param freight
     * @return
     */
    private Long countSelfDeliveryFeesV2(MainOrder mainOrder, Long freight) {
        // 订单运费金额 ，每个子单阈值最大的运费
        Long orderFreightAmount = 0L;
        int size = mainOrder.getSubOrders().size();
        SubOrder subOrder = mainOrder.getSubOrders().get(0);
        if (size == 1) {
            orderFreightAmount = getSubOrderShipFee(subOrder, SellerLineRouteDimensionEnum.CHEAPEST.getCode(), freight,  mainOrder);
            subOrder.setFreightAmount(orderFreightAmount);
        } else {
            orderFreightAmount = countBatchOrderMaxThreHoldFreightV2(mainOrder);
        }
        // 运费总金额
        freight += orderFreightAmount;
        mainOrder.setFreightAmount(orderFreightAmount);
        if (mainOrder.getOrderPrice() != null) {
            mainOrder.getOrderPrice().setFreightAmt(orderFreightAmount);
            long orderTotalAmt = NumUtils.getNullZero(mainOrder.getOrderPrice().getOrderRealAmt());
            mainOrder.getOrderPrice().setOrderRealAmt(orderTotalAmt + orderFreightAmount);
        }
        return freight;
    }

    private Long getSubOrderShipFee(SubOrder subOrder, int code, Long orderFreightAmount, MainOrder mainOrder) {
        DeliveryTypeFullInfoDTO deliveryTypeFullInfoDTO = queryDeliveryTypeFullInfoDTO(subOrder, code);
        // 默认为0
        subOrder.setFreightAmount(0L);
        Long threadShreld =0L;
        if (Objects.nonNull(deliveryTypeFullInfoDTO)) {
            if (DeliveryTypeEnum.SELF_SERVICE.getCode().equals(mainOrder.getDeliveryType())) {
                if (Objects.nonNull(deliveryTypeFullInfoDTO.getSellerDeliveryDto())) {
                    long shipFee = deliveryTypeFullInfoDTO.getSellerDeliveryDto().getShippingFee();
                    if (shipFee > 0L) {
                        orderFreightAmount = shipFee;
                        threadShreld = deliveryTypeFullInfoDTO.getSellerDeliveryDto().getShippingFreeThreshold();
                    }
                }
            } else if (DeliveryTypeEnum.SELLER_KA.getCode().equals(mainOrder.getDeliveryType())) {
                if (Objects.nonNull(deliveryTypeFullInfoDTO.getKaDeliveryDto())) {
                    long shipFee = deliveryTypeFullInfoDTO.getKaDeliveryDto().getShippingFee();
                    if (shipFee > 0L) {
                        orderFreightAmount = shipFee;
                        threadShreld = deliveryTypeFullInfoDTO.getKaDeliveryDto().getShippingFreeThreshold();
                    }
                }
                ;
            }
        }
        if (mainOrder.getOrderPrice().getOrderPromotionAmt() > threadShreld) {
            settingZeroFreight(mainOrder);
            //货品营销价总额超过满邮标准，主单运费设置为0
            orderFreightAmount = 0L;
        }

        return orderFreightAmount;
    }


    /**
     * 批量子单运费计算
     *
     * @param mainOrder
     * @return
     */
    private Long countBatchOrderMaxThreHoldFreightV2(
            MainOrder mainOrder) {
        // 取最大的运费门栏金额
        Long freeDeliveryThreshold = 0L;
        Long orderFreightAmount = 0L;
        // 计算每个子单物流运费
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            subOrder.setFreightAmount(0L);
            DeliveryTypeFullInfoDTO deliveryTypeFullInfoDTO = queryDeliveryTypeFullInfoDTO(subOrder, SellerLineRouteDimensionEnum.FREE_THRESHOLD.getCode());
            if (Objects.nonNull(deliveryTypeFullInfoDTO)) {
                if (DeliveryTypeEnum.SELLER_KA.getCode().equals(mainOrder.getDeliveryType())) {
                    if (Objects.nonNull(deliveryTypeFullInfoDTO.getKaDeliveryDto())) {
                        KaDeliveryTypeItemDTO kaDeliveryDto = deliveryTypeFullInfoDTO.getKaDeliveryDto();
                        long shipFee = kaDeliveryDto.getShippingFee();
                        if (shipFee > 0L) {
                            //先给每个子单加上运费，后续如果阈值不是最大，赋值为0
                            subOrder.setFreightAmount(shipFee);
                            if (kaDeliveryDto.getShippingFreeThreshold() > freeDeliveryThreshold) {
                                //子订单取阈值下最大
                                freeDeliveryThreshold = kaDeliveryDto.getShippingFreeThreshold();
                                //主单取阈值最大的运费
                                orderFreightAmount = shipFee;
                            } else if (freeDeliveryThreshold.equals(kaDeliveryDto.getShippingFreeThreshold())) {
                                if (kaDeliveryDto.getShippingFee() > orderFreightAmount) {
                                    //相同运费阈值，取运费最大的
                                    orderFreightAmount = shipFee;
                                }
                            }
                        }
                    }
                } else if (DeliveryTypeEnum.SELF_SERVICE.getCode().equals(mainOrder.getDeliveryType())) {
                    if (Objects.nonNull(deliveryTypeFullInfoDTO.getSellerDeliveryDto())) {
                        SellerDeliveryTypeItemDTO sellerSelfDelivery = deliveryTypeFullInfoDTO.getSellerDeliveryDto();
                        if (Objects.nonNull(sellerSelfDelivery)) {
                            long shipFee = sellerSelfDelivery.getShippingFee();
                            if (shipFee > 0L) {
                                //                        //先给每个子单加上运费，后续如果阈值不是最大，赋值为0
                                subOrder.setFreightAmount(shipFee);
                                if (sellerSelfDelivery.getShippingFreeThreshold() > freeDeliveryThreshold) {
                                    //子订单取阈值下最大
                                    freeDeliveryThreshold = sellerSelfDelivery.getShippingFreeThreshold();
                                    //主单取阈值最大的运费
                                    orderFreightAmount = shipFee;
                                } else if (freeDeliveryThreshold.equals(sellerSelfDelivery.getShippingFreeThreshold())) {
                                    if (shipFee > orderFreightAmount) {
                                        //相同运费阈值，取运费最大的
                                        orderFreightAmount = shipFee;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (mainOrder.getOrderPrice().getOrderPromotionAmt() > freeDeliveryThreshold) {
            settingZeroFreight(mainOrder);
            //货品营销价总额超过满邮标准，主单运费设置为0
            orderFreightAmount = 0L;
        }
        return orderFreightAmount;
    }

}