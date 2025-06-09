package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.middleware.mq.esindex.entity.OrderDumpInfo;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalFeeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalMessageAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.*;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.ReversalAgreeTask;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.*;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.*;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@Slf4j
public class ReversalCreateServiceImpl implements ReversalCreateService {
    private static final long ONE_DAY_MILLIS = 24 * 3600 * 1000;
    public static final Integer INSERT = 1;
    public static final Integer UPDATE = 2;
    public static final Integer PRIMARY_ORDER_FLAG = 1;
    public static final Integer CANREFUNS_FLAG = 1;//可退款
    public static final Integer MAX_DAYS = 14;//申请售后天数

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderConfigService orderConfigService;

    @Autowired
    private ReversalConverter reversalConverter;

    @Autowired
    private GenerateIdService generateIdService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TcReversalReasonRepository tcReversalReasonRepository;

    @Autowired
    private ReversalFeeAbility reversalFeeAbility;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private ReversalAgreeTask reversalAgreeTask;

    @Autowired
    private ReversalMessageAbility reversalMessageAbility;

    @Autowired
    private ReversalCreateAbility reversalCreateAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;

    @Autowired
    private TcReversalFlowRepository tcReversalFlowRepository;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private CancelReasonRepository cancelReasonRepository;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Autowired
    private ReversalQueryService reversalQueryService;

    @Value("${search.type:opensearch}")
    private String searchType;

    @Value("${trade.reversal.limit.day:14}")
    private Integer maxDay;

    @Override
    public void fillOrderInfo(MainReversal reversal) {
        // 查询订单
        MainOrder mainOrder = orderQueryAbility.getMainOrder(
            reversal.getMainOrder().getPrimaryOrderId(),
            OrderQueryOption.builder().build()
        );
        CommUtils.assertNotNull(mainOrder, OrderErrorCode.ORDER_NOT_EXISTS);
        // 查询用户信息
        Customer customer = userRepository.getCustomerRequired(reversal.getCustId());
        CommUtils.assertNotNull(customer, OrderErrorCode.USER_NOT_EXISTS);
        mainOrder.setCustomer(customer);
        //填充订单
        fillOrderInfo(reversal, mainOrder);
    }

    /**
     * 填充订单
     * @param reversal
     * @param mainOrder
     */
    private void fillOrderInfo(MainReversal reversal, MainOrder mainOrder) {
        // 订单对象
        reversal.setMainOrder(mainOrder);
        Integer subOrderStatus = null;
        // 回填非退款中的子订单
        if (reversal.getSubReversals() == null) {
            List<SubReversal> list = new ArrayList<>();
            for (SubOrder sub : mainOrder.getSubOrders()) {
                if (OrderStatusEnum.codeOf(sub.getOrderStatus()) == OrderStatusEnum.REVERSAL_DOING) {
                    continue;
                }
                SubReversal sr = new SubReversal();
                sr.setBinOrIin(mainOrder.getSeller().getBinOrIin());
                sr.setFirstName(mainOrder.getCustomer().getFirstName());
                sr.setLastName(mainOrder.getCustomer().getLastName());
                sr.setSubOrder(sub);
                list.add(sr);
                if (subOrderStatus == null) {
                    subOrderStatus = sub.getOrderStatus();
                }
            }
            reversal.setSubReversals(list);
        }
        // 回填指定子订单
        else {
            Map<Long, SubOrder> subOrderMap = CommUtils.toMap(mainOrder.getSubOrders(), SubOrder::getOrderId);
            for (SubReversal sub : reversal.getSubReversals()) {
                SubOrder subOrder = subOrderMap.get(sub.getSubOrder().getOrderId());
                CommUtils.assertNotNull(subOrder, OrderErrorCode.ORDER_NOT_EXISTS);
                subOrder.setBinOrIin(mainOrder.getSeller().getBinOrIin());
                subOrder.setFirstName(mainOrder.getCustomer().getFirstName());
                subOrder.setLastName(mainOrder.getCustomer().getLastName());
                sub.setSubOrder(subOrder);
                if (Objects.isNull(subOrderStatus)) {
                    subOrderStatus = subOrder.getOrderStatus();
                }
                else if (subOrderStatus.intValue() != subOrder.getOrderStatus().intValue()) {
                    throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_ILLEGAL_ORDER_STATUS);
                }
            }
        }
        // 填features
        if (Objects.isNull(reversal.getReversalFeatures())) {
            reversal.setReversalFeatures(new ReversalFeatureDO());
        }
        reversal.getReversalFeatures().setOrderStatus(subOrderStatus);
        reversal.getReversalFeatures().setOrderStage(mainOrder.orderAttr().getOrderStage());
        // 卖家
        reversal.setSellerId(mainOrder.getSeller().getSellerId());
        reversal.setFirstName(mainOrder.getCustomer().getFirstName());
        reversal.setLastName(mainOrder.getCustomer().getLastName());
        if(mainOrder.getCustomer().getIin() != null){
            reversal.setBinOrIin(mainOrder.getCustomer().getIin());
        } else {
            reversal.setBinOrIin(mainOrder.getCustomer().getUid());
        }
    }

    @Override
    public void checkCustomer(MainReversal reversal) {
        // 订单用户和入参用户必须相同
        CommUtils.assertEqual(
            reversal.getMainOrder().getCustomer().getCustId(),
            reversal.getCustId(),
            OrderErrorCode.ORDER_USER_NOT_MATCH
        );
        // 用户查询
        Customer customer = userRepository.getCustomerRequired(reversal.getCustId());
        reversal.setCustName(customer.getCustName());
        reversal.setFirstName(customer.getFirstName());
        reversal.setLastName(customer.getLastName());
    }

    @Override
    public List<ReversalCheckResult> checkTime(MainReversal reversal) {
        MainOrder ord = reversal.getMainOrder();
        Date received = ord.getOrderAttr().getConfirmReceiveTime();
        if (received != null) {
            //默认收货14天内可申请售后
            SellerTradeConfig config = orderConfigService.getSellerConfig(ord.getSeller().getSellerId());
            long maxMillis ;
            if(config != null){
                maxMillis = config.getCreateReversalMaxDays() * ONE_DAY_MILLIS;
            }else{
                maxMillis = maxDay * ONE_DAY_MILLIS;
            }
            long currMillis = System.currentTimeMillis() - received.getTime();
            if (currMillis > maxMillis) {
                return ReversalCheckResult.allSub(reversal, ReversalErrorCode.CREATE_REVERSAL_OUT_OF_TIME);
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 获取可以退单的子单数组
     * @param reversal
     * @return
     */
    @Override
    public List<ReversalCheckResult> checkStatus(MainReversal reversal) {
        MainOrder ord = reversal.getMainOrder();
        OrderStatusEnum orderStatus = OrderStatusEnum.codeOf(ord.getPrimaryOrderStatus());
        // 订单状态只能是完成 或者部分退单 其余状态均不可退单
        if (!(orderStatus == OrderStatusEnum.COMPLETED ||
            orderStatus == OrderStatusEnum.REFUND_PART_SUCCESS)) {
            return ReversalCheckResult.allSub(reversal, ReversalErrorCode.CREATE_REVERSAL_ILLEGAL_ORDER_STATUS);
        }
        // 子单售后进行中，不允许再申请
        List<ReversalCheckResult> list = new ArrayList<>();
        for (SubReversal sub : reversal.getSubReversals()) {
            OrderStatusEnum subStatus = OrderStatusEnum.codeOf(sub.getSubOrder().getOrderStatus());
            if (subStatus == OrderStatusEnum.WAITING_FOR_ACCEPT ||
                subStatus == OrderStatusEnum.WAITING_FOR_RETURN ||
                subStatus == OrderStatusEnum.WAITING_FOR_REFUND ||
                subStatus == OrderStatusEnum.REFUND_REQUESTED ||
                subStatus == OrderStatusEnum.REFUND_APPROVED) {
                list.add(ReversalCheckResult.of(sub, ReversalErrorCode.CREATE_REVERSAL_ILLEGAL_ORDER_STATUS));
            }
        }
        return list;
    }

    @Override
    public void checkCancelQty(MainReversal reversal) {
        // 这里只校验基本数量, 历史退货数量校验 及 默认值填充在 checkCancelAmt 中
        for (SubReversal subReversal : reversal.getSubReversals()) {
//            SubOrder subOrder = subReversal.getSubOrder();
//            // 设置缺省值全退
//            if (subReversal.getCancelQty() != null
//                    && (subReversal.getCancelQty().intValue() <= 0
//                    || subReversal.getCancelQty() > subOrder.getOrderQty())) {
//                throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_OUT_OF_QTY);
//            }
        }
    }

    @Override
    public void checkReversalReason(MainReversal reversal) {
        Integer code = reversal.getReversalReasonCode();
        TcCancelReasonDO reason = cancelReasonRepository.queryTcCancelReasonByCode(String.valueOf(code));
        if (reason == null ) {
            throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_REASON_ILLEGAL);
        }
    }

    @Override
    public void checkCancelAmt(MainReversal reversal) {
        reversalFeeAbility.divideCancelAmt(reversal);
    }

    /**
     * 生成ID
     */
    @Override
    public void generateReversalIds(MainReversal reversal) {
        Long custId = reversal.getCustId();
        List<Long> ids = generateIdService.nextReversalIds(custId, reversal.getSubReversals().size());
        int idx = 0;
        long mainId = ids.get(idx++);
        reversal.setPrimaryReversalId(mainId);
        for (SubReversal sub : reversal.getSubReversals()) {
            sub.setReversalId(ids.get(idx++));
            sub.setPrimaryReversalId(mainId);
        }
    }

    /**
     * 保存售后单（含退款单）信息
     */
    @Override
    public void saveReversal(MainReversal reversal) {
        // 发起退单
        saveReversal(reversal, true);
    }

    /**
     * 发起退单
     * @param reversal
     * @param createAgreeTask
     */
    private void saveReversal(MainReversal reversal,  boolean createAgreeTask) {
        // 保存退单前的check
        reversalCreateAbility.beforeSave(reversal);
        // 等待卖家接受状态
        ReversalStatusEnum reversalStatus = ReversalStatusEnum.WAITING_FOR_ACCEPT;
        // 退单数组
        List<TcReversalDO> reversalList = new ArrayList<>();
        // 更新订单
        List<TcOrderDO> orderUpdateList = new ArrayList<>();
        Date now = new Date();
        //判断商品类目是否可退款
        //○ 若支持，则订单状态流转到[WAITING_FOR_RETURN]
        //○ 若不支持，则订单状态流转到[WAITING_FOR_ACCEPT] ，商家在商家后台售后列表操作：
        // ■ 若商家接受，订单状态流转到[WAITING_FOR_RETURN]
        // ■ 若商家拒绝，订单状态流转到本次售后的发起状态，并关闭此次售后。
        // 查询订单
        TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(reversal.getMainOrder().getPrimaryOrderId());
        for (SubReversal subReversal : reversal.getSubReversals()) {
            // 查询每个退单的子单
            TcOrderDO orderDO = tcOrderRepository.querySubByOrderId(
                subReversal.getSubOrder().getPrimaryOrderId(),
                subReversal.getSubOrder().getOrderId()
            );
            // 订单扩展里面 是否允许退单，如果不允许 则是需要卖家审批，允许直接等待退货
            // 理论上 不会出现允许和不允许的一起退的问题
            if(Objects.nonNull(orderDO.getOrderAttr())){
                if(orderDO.getOrderAttr().getCanRefunds().equals(CANREFUNS_FLAG)){
                    reversalStatus = ReversalStatusEnum.WAITING_FOR_RETURN;
                } else {
                    reversalStatus = ReversalStatusEnum.WAITING_FOR_ACCEPT;
                }
            } else {
                reversalStatus = ReversalStatusEnum.WAITING_FOR_ACCEPT;
            }
            break;
        }
        // 主单
        TcReversalDO mainDO = reversalConverter.toTcReversalDO(reversal);
        mainDO.setGmtCreate(now);
        mainDO.setGmtModified(now);
        mainDO.setReversalStatus(reversalStatus.getCode());
        mainDO.setFirstName(reversal.getFirstName());
        mainDO.setLastName(reversal.getLastName());
        mainDO.setBinOrIin(dbOrderDO.getBinOrIin());
        /**if (reversal.getReversalType().equals(ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND.getCode())) {
            mainDO.setCancelAmt(0L);
            ReversalFeatureDO reversalFeatures = mainDO.getReversalFeatures();
            reversalFeatures.setCancelPointAmt(0L);
            reversalFeatures.setCancelPointCount(0L);
            reversalFeatures.setCancelRealAmt(0L);
            mainDO.setReversalFeatures(reversalFeatures);
        }**/
        reversalList.add(mainDO);
        // 主订单
        TcOrderDO mainOrdUp = new TcOrderDO();
        mainOrdUp.setOrderId(reversal.getMainOrder().getPrimaryOrderId());
        mainOrdUp.setPrimaryOrderId(reversal.getMainOrder().getPrimaryOrderId());
        mainOrdUp.setOrderStatus(reversalStatus.getCode());
        mainOrdUp.setPrimaryOrderStatus(reversalStatus.getCode());
        mainOrdUp.setReversalType(reversal.getReversalType());
        if (!OrderStatusEnum.isReversal(reversal.getMainOrder().getPrimaryOrderStatus())) {
            OrderAttrDO orderAttr = reversal.getMainOrder().orderAttr();
            orderAttr.setReversalOrderStatus(reversal.getMainOrder().getPrimaryOrderStatus());
            orderAttr.setReversalStartTime(now);
            mainOrdUp.setOrderAttr(orderAttr);
        }
        mainOrdUp.setVersion(reversal.getMainOrder().getVersion());
        orderUpdateList.add(mainOrdUp);
        // 售后子单、子订单
        Set<Long> reversalSubOrders = new HashSet<>();
        for (SubReversal subReversal : reversal.getSubReversals()) {
            reversalSubOrders.add(subReversal.getSubOrder().getOrderId());
            // 退单子单 对象构建
            TcReversalDO subDO = reversalConverter.toTcReversalDO(subReversal);
            subDO.setCustId(reversal.getCustId());
            subDO.setCustName(reversal.getCustName());
            subDO.setGmtCreate(now);
            subDO.setGmtModified(now);
            subDO.setReversalStatus(reversalStatus.getCode());
            subDO.setReversalType(reversal.getReversalType());
            subDO.setSellerId(mainDO.getSellerId());
            subDO.setSellerName(mainDO.getSellerName());
            subDO.setReversalChannel(mainDO.getReversalChannel());
            subDO.setReversalReason(mainDO.getReversalReason());
            subDO.setFirstName(reversal.getFirstName());
            subDO.setLastName(reversal.getLastName());
            subDO.setBinOrIin(dbOrderDO.getBinOrIin());
            reversalList.add(subDO);
            // 下单子单构建
            TcOrderDO subOrdUp = new TcOrderDO();
            subOrdUp.setOrderId(subReversal.getSubOrder().getOrderId());
            subOrdUp.setPrimaryOrderId(subReversal.getSubOrder().getPrimaryOrderId());
            subOrdUp.setOrderStatus(reversalStatus.getCode());
            subOrdUp.setPrimaryOrderStatus(reversalStatus.getCode());
            subOrdUp.setReversalType(reversal.getReversalType());
            if (!OrderStatusEnum.isReversal(subReversal.getSubOrder().getOrderStatus())) {
                OrderAttrDO orderAttr = subReversal.getSubOrder().orderAttr();
                orderAttr.setReversalOrderStatus(subReversal.getSubOrder().getOrderStatus());
                orderAttr.setReversalStartTime(now);
                subOrdUp.setOrderAttr(orderAttr);
            }
            subOrdUp.setVersion(subReversal.getSubOrder().getVersion());
            orderUpdateList.add(subOrdUp);
        }
        // 没申请售后的子订单 一起更新
        for (SubOrder subOrder : reversal.getMainOrder().getSubOrders()) {
            if (reversalSubOrders.contains(subOrder.getOrderId())) {
                continue;
            }
            TcOrderDO subOrdUp = new TcOrderDO();
            subOrdUp.setOrderId(subOrder.getOrderId());
            subOrdUp.setPrimaryOrderId(subOrder.getPrimaryOrderId());
            subOrdUp.setOrderStatus(subOrder.getOrderStatus());
            subOrdUp.setVersion(subOrder.getVersion());
            orderUpdateList.add(subOrdUp);
        }
        // 保存 开启事务
        TransactionStatus tr = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            saveDb(reversalList, orderUpdateList);
            transactionManager.commit(tr);
        } catch (Throwable t) {
            transactionManager.rollback(tr);
            ErrorUtils.throwUndeclared(t);
        }
        // 写下单流程
        addOrderFlow(orderUpdateList, reversalStatus, reversalStatus.getInner(),true);
        // 写退单流程
        addFlow(reversal, dbOrderDO, reversalStatus, reversalStatus.getInner(),true);
        // 查一下退单信息
        MainReversal newReversal = reversalQueryService.queryReversal(
            reversal.getPrimaryReversalId(),
            ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        //同步更新主订单
        Long primaryOrderId = reversalList.get(0).getPrimaryOrderId();
        //创建自动取消任务
        orderTaskAbility.reversalTask(newReversal,reversalStatus.getCode());
        // 发送同步售后单到es消息
        sendOrderAddUpdateEsMq(primaryOrderId);
        // 发送消息
        reversalMessageAbility.sendStatusChangedMessage(newReversal, reversalStatus);
    }

    /**
     * 写退单流程
     * @param reversal
     * @param re
     * @param targetStatus
     * @param name
     * @param isCustomer
     */
    protected void addFlow(MainReversal reversal,TcOrderDO re, ReversalStatusEnum targetStatus,String name, boolean isCustomer) {
        TcReversalFlowDO flow = new TcReversalFlowDO();
        flow.setPrimaryReversalId(reversal.getPrimaryReversalId());
        flow.setFromReversalStatus(re.getOrderStatus());
        flow.setToReversalStatus(targetStatus.getCode());
        flow.setCustOrSeller(isCustomer ? 1 : 0);
        flow.setOpName(name);
        flow.setGmtCreate(new Date());
        tcReversalFlowRepository.insert(flow);
    }

    /**
     * 写下单流程
     * @param orderUpdateList
     * @param targetStatus
     * @param name
     * @param isCustomer
     */
    protected void addOrderFlow(List<TcOrderDO> orderUpdateList, ReversalStatusEnum targetStatus,String name, boolean isCustomer) {
       for(TcOrderDO subOrdUp:orderUpdateList){
           if(PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode().equals(subOrdUp.getPrimaryOrderFlag())) {
               TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
               flow.setPrimaryOrderId(subOrdUp.getPrimaryOrderId());
               flow.setToOrderStatus(targetStatus.getCode());
               flow.setOperatorType(isCustomer ? 1 : 0);
               flow.setOpName(name);
               flow.setGmtCreate(new Date());
               flow.setGmtModified(new Date());
               tcOrderOperateFlowRepository.create(flow);
           }
       }
    }

    /**
     * 变更ES状态 订单和退单都要改
     * @param primaryOrderId
     */
    private void sendOrderAddUpdateEsMq(Long primaryOrderId) {
        if (!CommonConstant.OPENSEARCH.equals(searchType)) {
            List<TcReversalDO> tcReversalDOList = tcReversalRepository.queryByPrimaryOrderId(primaryOrderId);
            Map<Long, TcOrderDO> orderDataMap = getOrderDataMap(primaryOrderId);
            Long id = getPrimaryTcReversalLongId(tcReversalDOList);
            for (TcReversalDO tcReversalDO : tcReversalDOList) {
                // 退单发起 生成退单记录
                TcOrderDO tcOrderDO = orderDataMap.get(tcReversalDO.getOrderId());
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcReversalDO.getReversalId(), INSERT);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getAddReversalOrderMap(tcReversalDO, tcOrderDO, id);
                orderParam.putAll(orderMap);
                orderParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                orderEsIndexMqClient.push(orderDumpInfo);

                // 订单同步
                OrderDumpInfo updateOrderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), UPDATE);
                Map<String, String> updateOrderParam = updateOrderDumpInfo.getOrderParam();
                Map<String, String> updateOrderMap = getUpdateOrderMap(tcOrderDO);
                updateOrderParam.putAll(updateOrderMap);
                updateOrderParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                orderEsIndexMqClient.push(updateOrderDumpInfo);
            }
        }
    }

    private Map<String, String> getUpdateOrderMap(TcOrderDO tcOrderDO) {
        Map<String, String> updateOrderMap = new HashMap<>();
        updateOrderMap.put("orderStatus", String.valueOf(tcOrderDO.getOrderStatus()));
        updateOrderMap.put("gmtModified", JSONObject.toJSONString(tcOrderDO.getGmtModified()));
        updateOrderMap.put("reversalType", JSONObject.toJSONString(tcOrderDO.getReversalType()));
        return updateOrderMap;
    }

    private Map<Long, TcOrderDO> getOrderDataMap(Long primaryOrderId) {
        List<TcOrderDO> list = tcOrderRepository.queryBoughtDetailByPrimaryId(primaryOrderId);
        Map<Long, TcOrderDO> map = new HashMap<>();
        for (TcOrderDO tcOrderDO : list) {
            map.put(tcOrderDO.getOrderId(), tcOrderDO);
        }
        return map;
    }

    private Map<String, String> getAddReversalOrderMap(TcReversalDO tcReversalDO, TcOrderDO tcOrderDO, Long id) {
        Map<String, String> orderMap = new HashMap<>();
        OrderAttrDO orderAttrDO = tcOrderDO.getOrderAttr();
        ReversalFeatureDO reversalFeatureDO = tcReversalDO.getReversalFeatures();
        String isReversalMain = getIsReversalMain(tcReversalDO.getIsReversalMain());
        orderMap.put("gmtModified", JSONObject.toJSONString(tcReversalDO.getGmtModified()));
        orderMap.put("gmtCreate", JSONObject.toJSONString(tcReversalDO.getGmtCreate()));
        orderMap.put("custId", String.valueOf(tcReversalDO.getCustId()));
        orderMap.put("isReversalMain", isReversalMain);
        orderMap.put("orderStatus", String.valueOf(tcOrderDO.getOrderStatus()));
        orderMap.put("orderId", String.valueOf(tcReversalDO.getOrderId()));
        orderMap.put("primaryReversalId", String.valueOf(tcReversalDO.getPrimaryReversalId()));
        JSONObject relationType = getRelationType(tcOrderDO, id);
        orderMap.put("relationType", relationType.toJSONString());
        orderMap.put("reversalId", String.valueOf(tcReversalDO.getReversalId()));
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getConfirmReceiveTime())) {
            orderMap.put("reversalConfirmReceiveTime", JSONObject.toJSONString(orderAttrDO.getConfirmReceiveTime()));
        }
        if (!ObjectUtils.isEmpty(reversalFeatureDO) && !CollectionUtils.isEmpty(reversalFeatureDO.getLogisticsNos())) {
            orderMap.put("reversalLogisticsNo", JSONObject.toJSONString(reversalFeatureDO.getLogisticsNos()));
        }
        if (!ObjectUtils.isEmpty(reversalFeatureDO) && !ObjectUtils.isEmpty(reversalFeatureDO.getSendTime())) {
            orderMap.put("reversalSendTime", JSONObject.toJSONString(reversalFeatureDO.getSendTime()));
        }
        orderMap.put("reversalStatus", String.valueOf(tcReversalDO.getReversalStatus()));
        orderMap.put("reversalType", String.valueOf(tcReversalDO.getReversalType()));
        orderMap.put("sellerId", String.valueOf(tcReversalDO.getSellerId()));
        orderMap.put("orderTime", JSONObject.toJSONString(tcOrderDO.getGmtCreate()));
        if(!ObjectUtils.isEmpty(tcReversalDO.getBinOrIin())){
            orderMap.put("binOrIin", String.valueOf(tcReversalDO.getBinOrIin()));
        }
        if(!ObjectUtils.isEmpty(tcOrderDO.getFirstName())){
            orderMap.put("firstName", String.valueOf(tcReversalDO.getFirstName()));
        }
        if(!ObjectUtils.isEmpty(tcOrderDO.getLastName())){
            orderMap.put("lastName", String.valueOf(tcReversalDO.getLastName()));
        }
        if(!ObjectUtils.isEmpty(tcReversalDO.getSellerId())){
            Seller seller = userRepository.getSeller(tcReversalDO.getSellerId());
            orderMap.put("sellerName", seller.getSellerName());
        }
        if (!ObjectUtils.isEmpty(orderAttrDO.getCurrentStepNo())) {
            orderMap.put("stepNo", String.valueOf(orderAttrDO.getCurrentStepNo()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO.getCurrentStepStatus())) {
            orderMap.put("stepStatus", String.valueOf(orderAttrDO.getCurrentStepStatus()));
        }
        if (!ObjectUtils.isEmpty(reversalFeatureDO) && !ObjectUtils.isEmpty(reversalFeatureDO.getOrderStatus())) {
            orderMap.put("fromOrderStatus", String.valueOf(reversalFeatureDO.getOrderStatus()));
        }
        if (!ObjectUtils.isEmpty(tcOrderDO.getItemTitle())) {
            orderMap.put("itemTitle", tcOrderDO.getItemTitle());
        }
        if (!ObjectUtils.isEmpty(reversalFeatureDO) &&
            !ObjectUtils.isEmpty(reversalFeatureDO.getFeature()) &&
            !ObjectUtils.isEmpty(reversalFeatureDO.getFeature().get("sale_type"))) {
            orderMap.put("saleType", reversalFeatureDO.getFeature().get("sale_type"));
        }
        if (!ObjectUtils.isEmpty(reversalFeatureDO) &&
            !ObjectUtils.isEmpty(reversalFeatureDO.getFeature()) &&
            !ObjectUtils.isEmpty(reversalFeatureDO.getFeature().get("tags"))) {
            orderMap.put("tags", reversalFeatureDO.getFeature().get("tags"));
        }
        return orderMap;
    }

    private String getIsReversalMain(Boolean isReversalMain) {
        if (isReversalMain) {
            return "1";
        }
        return "0";
    }

    private Long getPrimaryTcReversalLongId(List<TcReversalDO> tcReversalDOList) {
        Long id = null;
        for (TcReversalDO tcReversal : tcReversalDOList) {
            if (tcReversal.getIsReversalMain()) {
                id = tcReversal.getReversalId();
            }
        }
        return id;
    }

    private JSONObject getRelationType(TcOrderDO tcOrderDO, Long id) {
        JSONObject relationType = new JSONObject();
        Integer primaryOrderFlag = tcOrderDO.getPrimaryOrderFlag();
        if (PRIMARY_ORDER_FLAG == primaryOrderFlag){
            relationType.put("name", "reversal_primary");
            relationType.put("parent", String.valueOf(tcOrderDO.getOrderId()));
        }
        else {
            relationType.put("name", "reversal_child");
            relationType.put("parent", String.valueOf(id));
        }
        return relationType;
    }

    /**
     * 退单记录保存
     * @param reList
     * @param ordUpList
     */
    private void saveDb(List<TcReversalDO> reList, List<TcOrderDO> ordUpList) {
        for (TcReversalDO re : reList) {
            tcReversalRepository.insert(re);
        }
        for (TcOrderDO up : ordUpList) {
            boolean success = tcOrderRepository.updateByOrderIdVersion(up);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }

    @Override
    public MainReversal createSystemRefund(SystemRefund sys) {
        MainReversal reversal = new MainReversal();
        reversal.setCustId(sys.getMainOrder().getCustomer().getCustId());
        reversal.setCustName(sys.getMainOrder().getCustomer().getCustName());
        reversal.setSellerId(sys.getMainOrder().getSeller().getSellerId());
        reversal.setReversalReasonCode(sys.getReasonCode());
        reversal.setReversalReasonContent(sys.getReasonContent());
        reversal.setReversalType(sys.getReversalType());
        reversal.setReversalChannel(sys.getChannel());
        reversal.reversalFeatures().putExtend(ReversalFeatureKey.SYS_REASON, sys.getReasonContent());

        fillOrderInfo(reversal, sys.getMainOrder());

        // 退款金额、数量
        if (sys.getBeforeSetAmt() != null) {
            sys.getBeforeSetAmt().accept(reversal);
        }
        checkCancelQty(reversal);   // 填充默认全退
        checkCancelAmt(reversal);   // 填充默认全退
        if (sys.getAfterSetAmt() != null) {
            sys.getAfterSetAmt().accept(reversal);
        }

        // 退款金额为0, 不退
        if (NumUtils.getNullZero(reversal.getCancelAmt()) == 0) {
            return null;
        }
        generateReversalIds(reversal);
        // 保存
        if (sys.getBeforeSave() != null) {
            sys.getBeforeSave().accept(reversal);
        }
        saveReversal(reversal,  false);

        // 执行退款
        if (StepOrderUtils.isMultiStep(reversal)) {
            for (Integer stepNo : StepOrderUtils.getSteps(reversal)) {
                orderPayRepository.createRefund(reversal, stepNo);
            }
        } else {
            orderPayRepository.createRefund(reversal, null);
        }
        return reversal;
    }
}
