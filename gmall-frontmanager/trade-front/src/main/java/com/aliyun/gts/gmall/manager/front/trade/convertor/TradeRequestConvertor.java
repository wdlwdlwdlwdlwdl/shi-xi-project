package com.aliyun.gts.gmall.manager.front.trade.convertor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import com.aliyun.gts.gmall.manager.front.common.util.IpHolder;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ItemSkuIdWithQty;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ReversalSubOrder;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.SubOrderEvaluation;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CalCartPriceRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CheckAddCartRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.OrderListRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.AddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.utils.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderStatusInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.common.constants.EvaluationApproveStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.testng.collections.Maps;

/**
 * 交易相关的转换, rest to rpc
 *
 * @author tiansong
 */
@Mapper(componentModel = "spring")
public interface TradeRequestConvertor {
    /****************************** cart start *************************/
    /**
     * 添加购物车
     *
     * @param addCartRestCommand
     * @return
     */
    AddCartRpcReq convertAddCart(AddCartRestCommand addCartRestCommand);

    /**
     * 添加购物车校验
     *
     * @param checkAddCartRestQuery
     * @return
     */
    CheckAddCartRpcReq convertCheckAddCart(CheckAddCartRestQuery checkAddCartRestQuery);

    /**
     * 删除购物车商品
     *
     * @param delCartRestCommand
     * @return
     */
    DeleteCartRpcReq convertDelCart(DelCartRestCommand delCartRestCommand);

    /**
     * 修改购物车商品
     *
     * @param modifyCartRestCommand
     * @return
     */
    ModifyCartRpcReq convertModifyCart(ModifyCartRestCommand modifyCartRestCommand);

    @Mappings({
            @Mapping(target = "skuId", source = "afterSkuId")
    })
    CartSingleQueryRpcReq convertSingleQuery(ModifyCartRestCommand request);

    CartSingleQueryRpcReq convertSingleQuery(ModifyCartItemQtyRestCommand request);

    /**
     * 修改购物车内商品数量
     *
     * @param command
     * @return
     */
    @Mappings({
            @Mapping(source = "skuId", target = "skuId")
    })
    ModifyCartRpcReq convertModifyQtyCart(ModifyCartItemQtyRestCommand command);

    /**
     * 购物车计算价格
     *
     * @param calCartPriceRestQuery
     * @return
     */
    CalCartPriceRpcReq convertCartPrice(CalCartPriceRestQuery calCartPriceRestQuery);

    /**
     * 从前端请求转换成rpc请求
     *
     * @param itemSkuIdWithQty
     * @return
     */
    @Mappings({@Mapping(source = "cartQty", target = "qty")})
    ItemSkuQty convertItemSkuQty(ItemSkuIdWithQty itemSkuIdWithQty);

    /**
     * 购物车列表查询请求
     *
     * @param pageLoginRestQuery
     * @return
     */
    CartQueryRpcReq convertCartList(QueryCartRestCommand pageLoginRestQuery);

    /****************************** order start *************************/

    /**
     * 订单确认请求
     *
     * @param confirmOrderRestQuery
     * @return
     */
    ConfirmOrderInfoRpcReq convertOrderConfirm(ConfirmOrderRestQuery confirmOrderRestQuery);

    /**
     * 结算 订单生成
     * @param createCheckOutOrderRestCommand
     * @return
     */
    CreateCheckOutOrderRpcReq convertOrderCheckOut(CreateCheckOutOrderRestCommand createCheckOutOrderRestCommand);

    /**
     * 创建订单请求
     *
     * @param createOrderRestCommand
     * @return
     */
    @Mapping(source = "orderGroupInfoList", target = "orderInfos")
    CreateOrderRpcReq convertOrderCreate(CreateOrderRestCommand createOrderRestCommand);

    /**
     * 主订单的读写操作
     *
     * @param primaryOrderRestCommand
     * @return
     */
    PrimaryOrderRpcReq convertPrimaryOrder(PrimaryOrderRestCommand primaryOrderRestCommand);

    /**
     * 多阶段订单操作
     */
    StepOrderHandleRpcReq convertStepOrderReq(StepOrderRestCommand stepOrderRestCommand);

    /**
     * 订单详情
     *
     * @param primaryOrderRestQuery
     * @return
     */
    OrderDetailQueryRpcReq convertGetDetail(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 收货地址转换
     *
     * @param customerAddressDTO
     * @return
     */
    AddressVO convertAddress(CustomerAddressDTO customerAddressDTO);

    /**
     * 收货地址转换
     *
     * @param addressVO
     * @return
     */
    @Mapping(source = "id", target = "receiverId")
    @Mapping(source = "provinceId", target = "provinceCode")
    @Mapping(source = "cityId", target = "cityCode")
    @Mapping(source = "areaId", target = "districtCode")
    @Mapping(source = "addressDetail", target = "deliveryAddr")
    @Mapping(source = "name", target = "receiverName")
    ReceiverDTO convertAddressVO(AddressVO addressVO);

    /**
     * 订单列表查询转换
     *
     * @param orderListRestQuery
     * @return
     */
    CustomerOrderQueryRpcReq convertOrderList(OrderListRestQuery orderListRestQuery);

    default OrderStatusInfo toOrderStatusInfo(String status) {
        if (StringUtils.isBlank(status)) {
            return null;
        }
        String[] parts = StringUtils.split(status, '_');
        OrderStatusInfo r = new OrderStatusInfo();
        r.setOrderStatus(Integer.parseInt(parts[0]));
        if (parts.length >= 3) {
            r.setStepNo(Integer.parseInt(parts[1]));
            r.setStepStatus(Integer.parseInt(parts[2]));
        }
        return r;
    }

    /**
     * 创建订单时进行支付
     *
     * @param createOrderRestCommand
     * @return
     */
    OrderPayRestCommand convertOrderCreatePay(CreateOrderRestCommand createOrderRestCommand);

    /**
     * 创建订单后合并支付
     *
     * @param createOrderRestCommand
     * @return
     */
    OrderMergePayRestCommand convertOrderCreateMergePay(CreateOrderRestCommand createOrderRestCommand);

    /**
     * 合并支付
     *
     * @param orderPayRestCommand
     * @return
     */
    OrderMergePayRestCommand convertMergePay(OrderPayRestCommand orderPayRestCommand);

    OrderPayRestCommand copy(OrderPayRestCommand source);
    /****************************** pay start *************************/

    /**
     * 收银台请求转换
     *
     * @param primaryOrderRestQuery
     * @return
     */
    PayRenderRpcReq convertPayRender(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 支付状态检查
     *
     * @param payCheckRestQuery
     * @return
     */
    ConfirmPayCheckRpcReq convertPayCheck(PayCheckRestQuery payCheckRestQuery);

    /**
     * 支付
     *
     * @param orderPayRestCommand
     * @return
     */
    OrderPayRpcReq convertToPay(OrderPayRestCommand orderPayRestCommand);

    /**
     * 支付
     *
     * @param orderPayRestCommand
     * @return
     */
    OrderPayV2RpcReq convertToPaymentV2(OrderPayRestCommand orderPayRestCommand);

    /**
     * 合并支付
     *
     * @param orderMergePayRestCommand
     * @return
     */
    OrderMergePayRpcReq convertToMergePay(OrderMergePayRestCommand orderMergePayRestCommand);

    /**
     * 支付回调
     *
     * @param payCallbackRestCommand
     * @return
     */
    OrderPayCallBackRpcReq convertCallback(PayCallbackRestCommand payCallbackRestCommand);
    /****************************** reversal start *************************/
    /**
     * 售后申请转订单查询
     *
     * @param reversalCheckRestQuery
     * @return
     */
    PrimaryOrderRestQuery convertOrderQuery(ReversalCheckRestQuery reversalCheckRestQuery);

    /**
     * 申请售后检查订单可申请数量
     *
     * @param reversalCheckRestQuery
     * @return
     */
    @Mappings({
            @Mapping(target = "reversalChannel", source = "channel"),
            @Mapping(target = "subOrderIds", source = "subOrderId", qualifiedByName = "toLongList")
    })
    CheckReversalRpcReq convertCheckOrder(ReversalCheckRestQuery reversalCheckRestQuery);

    @Named("toLongList")
    default List<Long> toLongList(Long value) {
        return value == null ? null : Lists.newArrayList(value);
    }

    /**
     * 创建售后单
     *
     * @param createReversalRestCommand
     * @return
     */
    @Mapping(source = "channel", target = "reversalChannel")
    CreateReversalRpcReq convertCreate(CreateReversalRestCommand createReversalRestCommand);

    @Mapping(target = "extra", expression = "java(toExtra(subOrder))")
    ReversalSubOrderInfo reversalSubOrderToReversalSubOrderInfo(ReversalSubOrder subOrder);

    default Map<String, String> toExtra(ReversalSubOrder order) {
        if (CollectionUtils.isEmpty(order.getCombineItems())) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put(ItemConstants.COMBINE_ITEM, JsonUtils.toJSONString(order.getCombineItems()));
        return map;
    }

    /**
     * 取消售后单
     *
     * @param modifyReversalRestCommand
     * @return
     */
    ReversalModifyRpcReq convertCancel(ModifyReversalRestCommand modifyReversalRestCommand);

    /**
     * 邮寄提交
     *
     * @param reversalDeliverRestCommand
     * @return
     */
    ReversalDeliverRpcReq convertDeliver(ReversalDeliverRestCommand reversalDeliverRestCommand);

    /**
     * 售后单列表查询
     *
     * @param reversalRestQuery
     * @return
     */
    ReversalQueryRpcReq convertQueryList(ReversalRestQuery reversalRestQuery);

    /****************************** trade other start *************************/

    /**
     * 订单评价
     *
     * @param subOrderEvaluationList
     * @return
     */
    List<EvaluationRpcReq> convertSubOrderEvaluation(List<SubOrderEvaluation> subOrderEvaluationList);

    /**
     * 买家确认退款
     * @param command
     * @return
     */
    ReversalBuyerConfirmReq convertBuyerConfirmReq(ReversalBuyerConfirmRestCommand command);

    default EvaluationRpcReq convertSubOrderEvaluation(SubOrderEvaluation subOrderEvaluation) {
        if (subOrderEvaluation == null) {
            return null;
        }
        EvaluationRpcReq target = new EvaluationRpcReq();
        _convertSubOrderEvaluation(target, subOrderEvaluation);

        target.extend().put(TradeExtendKeyConstants.EVALUATION_CUST_NAME, subOrderEvaluation.getCustName());
        target.extend().put(TradeExtendKeyConstants.EVALUATION_ITEM_TITLE, subOrderEvaluation.getItemTitle());
        target.extend().put(TradeExtendKeyConstants.EVALUATION_IS_SYSTEM, false);

        // 评价扩展
        target.extend().put(TradeExtendKeyConstants.EVALUATION_APPROVE_CONTENT, StringUtils.EMPTY);
        target.extend().put(TradeExtendKeyConstants.EVALUATION_APPROVE_STATUS, EvaluationApproveStatusEnum.NEED_APPROVE.getCode());
        target.extend().put(TradeExtendKeyConstants.CUSTOMER_IP, IpHolder.get());

        return target;
    }

    void _convertSubOrderEvaluation(@MappingTarget EvaluationRpcReq target, SubOrderEvaluation subOrderEvaluation);

    default EvaluationRpcReq convertMainOrderEvaluation(AddEvaluationRestCommand request) {
        EvaluationRpcReq mainOrderEvaluation = new EvaluationRpcReq();
        mainOrderEvaluation.setCustId(request.getCustId());
        mainOrderEvaluation.setSellerId(request.getSellerId());
        mainOrderEvaluation.setPrimaryOrderId(request.getPrimaryOrderId());
        mainOrderEvaluation.setOrderId(request.getPrimaryOrderId());
        mainOrderEvaluation.setRateScore(request.getServiceScore());
        mainOrderEvaluation.setRateDesc(request.getServiceDesc());
        mainOrderEvaluation.setCustName(request.getCustName());
        mainOrderEvaluation.setItemId(0L);
        mainOrderEvaluation.setReplyId(0L);

        Map<String, Object> extendMap = Maps.newHashMap();
        extendMap.put(TradeExtendKeyConstants.EVALUATION_LOGISTICS_RATE, request.getLogisticsScore());
        extendMap.put(TradeExtendKeyConstants.EVALUATION_LOGISTICS_DESC, request.getLogisticsDesc());

        extendMap.put(TradeExtendKeyConstants.EVALUATION_CUST_NAME, request.getCustName());
        extendMap.put(TradeExtendKeyConstants.EVALUATION_IS_SYSTEM, false);

        //评价扩展
        extendMap.put(TradeExtendKeyConstants.EVALUATION_APPROVE_CONTENT, StringUtils.EMPTY);
        extendMap.put(TradeExtendKeyConstants.EVALUATION_APPROVE_STATUS, EvaluationApproveStatusEnum.NEED_APPROVE.getCode());
        extendMap.put(TradeExtendKeyConstants.CUSTOMER_IP, IpHolder.get());

        mainOrderEvaluation.setExtend(extendMap);
        return mainOrderEvaluation;
    }


    /**
     * 支付转为topay原始返回参数
     *
     * @param orderPayVO
     * @return
     */
    OrderPayRpcResp convertToOrderPayRpcResp(OrderPayVO orderPayVO);
}
