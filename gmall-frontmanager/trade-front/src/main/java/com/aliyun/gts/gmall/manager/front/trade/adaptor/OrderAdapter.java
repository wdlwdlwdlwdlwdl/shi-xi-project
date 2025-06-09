package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.ConfirmOrderSplitReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.OrderExtFacade;
import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerQueryReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.constants.OrderCountKey;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CountOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.OrderListRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayCard;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderConfirmVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PrimaryOrderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OrderTabEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.EPayFacade;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CountOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderTaskDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerCardInfoCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerBankCardByAccountQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerBankCardByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerQueryOption;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerBankCardInfoDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.poi.hssf.record.OldCellRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.testng.collections.Maps;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * 交易依赖
 *
 * @author tiansong
 */
@Slf4j
@Service
public class OrderAdapter {

    private static final Integer trueValue = 1;

    private static final Integer falseValue = 0;

    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    @NacosValue(value = "${front-manager.promotion.newuser.group.id:100}", autoRefreshed = true)
    private Long SPECIFIC_GROUP_ID;

    @Autowired
    private OrderReadFacade orderReadFacade;
    @Autowired
    private OrderWriteFacade orderWriteFacade;
    @Autowired
    private TradeRequestConvertor tradeRequestConvertor;
    @Autowired
    private TradeResponseConvertor tradeResponseConvertor;
    @Autowired
    private OrderExtFacade orderExtFacade;
    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;
    @Autowired
    private CustomerReadFacade customerReadFacade;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;


    DubboBuilder tradeBuilder = DubboBuilder.builder().sysCode(TRADE_CENTER_ERROR).logger(log).build();

    /**
     * 订单确认
     * 调用交易模块 计算订单信息
     * @param confirmOrderRestQuery 订单确认请求
     * @return 订单确认
     */
    public ConfirmOrderDTO confirm(ConfirmOrderRestQuery confirmOrderRestQuery) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_confirm)
            .queryFunc((Function<ConfirmOrderRestQuery, RpcResponse<ConfirmOrderDTO>>) request -> {
                // 入参转换
                ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = tradeRequestConvertor.convertOrderConfirm(request);
                // 接受地址信息
                // confirmOrderInfoRpcReq.setReceiver(tradeRequestConvertor.convertAddressVO(request.getAddressVO()));
                // 计算订单数据
                return orderReadFacade.confirmOrderInfo(confirmOrderInfoRpcReq);
            })
            .bizCode(ORDER_CONFIRM_ERROR)
            .query(confirmOrderRestQuery);
    }


    /**
     * 订单确认
     * 调用交易模块 计算订单信息
     * @param confirmOrderRestQuery 订单确认请求
     * @return 订单确认
     */
    public ConfirmOrderDTO confirmNew(ConfirmOrderRestQuery confirmOrderRestQuery) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_confirm)
            .queryFunc((Function<ConfirmOrderRestQuery, RpcResponse<ConfirmOrderDTO>>) request -> {
                // 入参转换
                ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = tradeRequestConvertor.convertOrderConfirm(request);
                // 计算订单数据
                return orderReadFacade.confirmOrderInfoNew(confirmOrderInfoRpcReq);
            })
            .bizCode(ORDER_CONFIRM_ERROR)
            .query(confirmOrderRestQuery);
    }

    /**
     * 订单确认
     * 调用交易模块 计算订单信息
     * @param  createCheckOutOrderRestCommand
     * @return 订单确认
     * 2025-1-3 16:29:47
     */
    public CheckOutOrderResultDTO checkOutOrderNew(CreateCheckOutOrderRestCommand createCheckOutOrderRestCommand) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_checkout)
            .queryFunc((Function<CreateCheckOutOrderRestCommand, RpcResponse<CheckOutOrderResultDTO>>) request -> {
                // 入参转换
                CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq = tradeRequestConvertor.convertOrderCheckOut(request);
                // 计算订单数据
                return orderWriteFacade.createCheckOutOrder(createCheckOutOrderRpcReq);
            })
            .bizCode(ORDER_CONFIRM_ERROR)
            .query(createCheckOutOrderRestCommand);
    }

    /**
     * 是否是新人分组的标识
     * @param custId
     * @return
     */
    private Boolean isNewUser(Long custId) {
        // 判断是否新人
        NewCustomerQueryReq req = new NewCustomerQueryReq();
        req.setLimit(limit);
        req.setId(custId);
        RpcResponse<Boolean> rpcResponse = customerReadExtFacade.queryIsNewCustomer(req);
        if (rpcResponse.isSuccess()) {
            return rpcResponse.getData();
        }
        return false;
    }

    /**
     * 创建订单
     * @param createOrderRestCommand 创建订单请求
     * @return 主订单列表
     */
    public List<PrimaryOrderVO> createOrder(CreateOrderRestCommand createOrderRestCommand) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_create)
            .queryFunc((Function<CreateOrderRestCommand, RpcResponse<List<PrimaryOrderVO>>>) request -> {
                CreateOrderRpcReq createOrderRpcReq = tradeRequestConvertor.convertOrderCreate(request);
                //账期支付的话  把备注放进扩展信息里
                if(PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(createOrderRestCommand.getPayChannel())) {
                    Map<String, String> extraMap = createOrderRpcReq.getExtra();
                    if (extraMap == null) {
                        extraMap = new HashMap<>();
                    }
                    extraMap.put(CreatingOrderParamConstants.ACCOUNT_PERIOD_MEMO, createOrderRestCommand.getAccountPeriod());
                    createOrderRpcReq.setExtra(extraMap);
                }

                // 判断是否新用户，新用户透传标识与新人分组id
                if (isNewUser(createOrderRestCommand.getCustId())){
                    Map<String, String> extraMap = createOrderRpcReq.getExtra();
                    if (extraMap == null) {
                        extraMap = new HashMap<>();
                    }
                    extraMap.put("isNewUser", String.valueOf(true));
                    extraMap.put("specificGroupId", String.valueOf(SPECIFIC_GROUP_ID));
                    createOrderRpcReq.setExtra(extraMap);
                }

                if (request.getOrderInvoice() != null) {
                    // 补充发票信息
                    Map<String, String> invoiceMap = Maps.newHashMap();
                    invoiceMap.put(BizConst.EXTEND_KEY_ORDER_INVOICE, JSON.toJSONString(request.getOrderInvoice()));
                    Map<String, Map<String, String>> expendStruct = Maps.newHashMap();
                    expendStruct.put(BizConst.EXTEND_KEY_ORDER_INVOICE, invoiceMap);
                    createOrderRpcReq.getOrderInfos().forEach(orderGroupInfo -> {
                        orderGroupInfo.setExpendStruct(expendStruct);
                        //关闭申领入口
                        orderGroupInfo.getExtraFeature().put("hide_apply_button", "true");
                    });
                }
                RpcResponse<CreateOrderResultDTO> rpcResponse = orderWriteFacade.createOrder(createOrderRpcReq);
                if (!rpcResponse.isSuccess() ||
                    rpcResponse.getData() == null ||
                    CollectionUtils.isEmpty(rpcResponse.getData().getOrders())) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                return RpcResponse.ok(tradeResponseConvertor.convertOrderCreate(rpcResponse.getData().getOrders()));
            })
            .bizCode(ORDER_CREATE_ERROR)
            .query(createOrderRestCommand);
    }


    /**
     * 创建订单
     * @param createOrderRestCommand 创建订单请求
     * @return 主订单列表
     */
    public List<PrimaryOrderVO> createOrderNew(CreateOrderRestCommand createOrderRestCommand) {
        //下边创建DubboDataSource对象的目的仅仅是封装，DubboDataSource是对远程调用的统一封装和管理，统一处理日志、异常等等，
        //也可以不创建DubboDataSource，但是需要写一堆重复的日志、异常；
        //当然这边最核心的代码只有一行——dubbo远程调用platform项目中创建订单的代码。
        return tradeBuilder.create() //创建了DubboDataSource对象
            .id(DsIdConst.trade_order_create)
//            .queryFunc((Function<CreateOrderRestCommand,CreateOrderRestCommand>) request ->{
//                return null;
//            })

            //下边代码中的 request 是 CreateOrderRestCommand 类型 ， 通过 .query(createOrderRestCommand) 将createOrderRestCommand传入 .queryFunc()中
            .queryFunc((Function<CreateOrderRestCommand, RpcResponse<List<PrimaryOrderVO>>>) request -> {
                CreateOrderRpcReq createOrderRpcReq = tradeRequestConvertor.convertOrderCreate(request);
                // 判断是否新用户，新用户透传标识与新人分组id
                if (isNewUser(createOrderRestCommand.getCustId())){
                    Map<String, String> extraMap = createOrderRpcReq.getExtra();
                    if (extraMap == null) {
                        extraMap = new HashMap<>();
                    }
                    extraMap.put("isNewUser", String.valueOf(true));
                    extraMap.put("specificGroupId", String.valueOf(SPECIFIC_GROUP_ID));
                    createOrderRpcReq.setExtra(extraMap);
                }
                RpcResponse<CreateOrderResultDTO> rpcResponse = orderWriteFacade.createOrderNew(createOrderRpcReq);
                if (!rpcResponse.isSuccess() ||
                    rpcResponse.getData() == null ||
                    CollectionUtils.isEmpty(rpcResponse.getData().getOrders())) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                return RpcResponse.ok(tradeResponseConvertor.convertOrderCreate(rpcResponse.getData().getOrders()));
            })
            .bizCode(ORDER_CREATE_ERROR)
            .query(createOrderRestCommand);
    }

    /**
     * 订单详情
     *
     * @param primaryOrderRestQuery 订单详情请求
     * @return 订单详情
     */
    public MainOrderDetailDTO getDetail(PrimaryOrderRestQuery primaryOrderRestQuery) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_getDetail)
            .queryFunc((Function<PrimaryOrderRestQuery, RpcResponse<MainOrderDetailDTO>>) request -> {
                OrderDetailQueryRpcReq req = tradeRequestConvertor.convertGetDetail(request);
                req.setIncludeOrderTaskTypes(Lists.newArrayList(
                    OrderTaskDTO.TASK_TYPE_CLOSE_UNPAID,
                    OrderTaskDTO.TASK_TYPE_SYS_CONFIRM)
                );
                return orderReadFacade.queryOrderDetail(req);
            })
            .bizCode(TradeFrontResponseCode.ORDER_DETAIL_ERROR)
            .query(primaryOrderRestQuery);
    }


    /**
     * 订单详情(传primaryOrderIdList列表)
     *
     * @param primaryOrderRestQuery 订单详情请求
     * @return 订单详情
     */
    public List<MainOrderDetailDTO> getDetailNew(PrimaryOrderRestQuery primaryOrderRestQuery) {
        return tradeBuilder.create()
                .id(DsIdConst.trade_order_getDetail)
                .queryFunc((Function<PrimaryOrderRestQuery, RpcResponse<List<MainOrderDetailDTO>>>) request -> {
                    OrderDetailQueryRpcReq req = tradeRequestConvertor.convertGetDetail(request);
                    req.setIncludeOrderTaskTypes(Lists.newArrayList(
                            OrderTaskDTO.TASK_TYPE_CLOSE_UNPAID,
                            OrderTaskDTO.TASK_TYPE_SYS_CONFIRM)
                    );
                    return orderReadFacade.queryOrderDetailNew(req);
                })
                .bizCode(TradeFrontResponseCode.ORDER_DETAIL_ERROR)
                .query(primaryOrderRestQuery);
    }


    /**
     * 订单列表
     *
     * @param orderListRestQuery 订单列表请求
     * @return 订单列表分页
     */
    public PageInfo<MainOrderDTO> getList(OrderListRestQuery orderListRestQuery) {
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_getList)
            .queryFunc((Function<OrderListRestQuery, RpcResponse<PageInfo<MainOrderDTO>>>) request -> {
                CustomerOrderQueryRpcReq rpcReq;
                if (OrderTabEnum.PENDING_EVALUATION.equals(request.getOrderTab())) {
                    rpcReq = CustomerOrderQueryRpcReq.getNotEvaluatedReq(request.getCustId());
                    rpcReq.setItemTitle(request.getItemTitle());
                    rpcReq.setPrimaryOrderIds(request.getPrimaryOrderIds());
                } else {
                    rpcReq = tradeRequestConvertor.convertOrderList(request);
                }
                rpcReq.setPageSize(orderListRestQuery.getPage().getPageSize());
                rpcReq.setCurrentPage(orderListRestQuery.getPage().getPageNo());
                return orderReadFacade.queryCustOrderList(rpcReq);
            }
        ).
        bizCode(ORDER_LIST_ERROR)
        .query(orderListRestQuery);
    }

    /**
     * 取消订单
     *
     * @param primaryOrderRestCommand 取消订单请求
     */
    public void cancelOrder(PrimaryOrderRestCommand primaryOrderRestCommand) {
        tradeBuilder.create()
            .id(DsIdConst.trade_order_cancel)
            .queryFunc((Function<PrimaryOrderRestCommand, RpcResponse>)
                request -> orderWriteFacade.cancelOrder(tradeRequestConvertor.convertPrimaryOrder(request))
            )
            .bizCode(ORDER_CANCEL_ERROR)
            .query(primaryOrderRestCommand);
    }

    /**
     * 删除订单
     *
     * @param primaryOrderRestCommand 删除订单请求
     */
    public void delOrder(PrimaryOrderRestCommand primaryOrderRestCommand) {
        tradeBuilder.create()
            .id(DsIdConst.trade_order_del)
            .queryFunc((Function<PrimaryOrderRestCommand, RpcResponse>)
                request -> orderWriteFacade.deleteOrderByCust(tradeRequestConvertor.convertPrimaryOrder(request))
            )
            .bizCode(ORDER_DEL_ERROR)
            .query(primaryOrderRestCommand);
    }

    /**
     * 确认收货
     *
     * @param primaryOrderRestCommand 确认收货请求
     */
    public void confirmReceipt(PrimaryOrderRestCommand primaryOrderRestCommand) {
        tradeBuilder.create().id(DsIdConst.trade_order_receipt).queryFunc(
                (Function<PrimaryOrderRestCommand, RpcResponse>) request ->
                        orderWriteFacade.confirmReceiveOrder(tradeRequestConvertor.convertPrimaryOrder(request))
        ).bizCode(ORDER_CONFIRM_RECEIPT_ERROR).query(primaryOrderRestCommand);
    }

    /**
     * 多阶段用户确认
     */
    public void confirmStepOrder(StepOrderRestCommand stepOrderRestCommand) {
        tradeBuilder.create().id(DsIdConst.trade_order_confirmStepOrder).queryFunc(
                (Function<StepOrderRestCommand, RpcResponse>) request ->
                        orderWriteFacade.confirmStepOrderByCustomer(tradeRequestConvertor.convertStepOrderReq(request))
        ).bizCode(ORDER_CONFIRM_RECEIPT_ERROR).query(stepOrderRestCommand);
    }

    /**
     * 获取各个订单状态的订单数量
     *
     * @param restQuery 订单数量请求
     * @return 订单数量
     */
    public Map<Integer, Integer> count(CountOrderRestQuery restQuery) {
        CountOrderQueryRpcReq rpcQuery = new CountOrderQueryRpcReq();
        rpcQuery.setCustId(restQuery.getCustId());
        rpcQuery.setStatus(restQuery.getStatus());
        return tradeBuilder.create().id(DsIdConst.trade_count_query).queryFunc(
                (Function<CountOrderQueryRpcReq, RpcResponse<Map<Integer, Integer>>>) request -> {
                    RpcResponse<Map<Integer, Integer>> r = orderReadFacade.countOrderByStatus(request);
                    RpcResponse<PageInfo<MainOrderDTO>> notEvaluatedOrders = orderReadFacade.queryCustOrderList(
                            CustomerOrderQueryRpcReq.getNotEvaluatedReq(request.getCustId()));
                    Map<Integer, Integer> result = new HashMap<>(r.getData());
                    result.put(OrderCountKey.waitEvaluateKey, notEvaluatedOrders.getData().getTotal().intValue());
                    return RpcResponse.ok(result);
                }).bizCode(TRADE_CNT_ERROR).query(rpcQuery);
    }

    public ConfirmOrderSplitDTO checkSplit(ConfirmOrderSplitReq req) {
        Function<ConfirmOrderSplitReq, ?> fn = orderExtFacade::checkSplit;
        return tradeBuilder.create()
                .id(DsIdConst.trade_order_checkSplit)
                .queryFunc(fn)
                .bizCode(ORDER_CONFIRM_ERROR)
                .query(req);
    }



}