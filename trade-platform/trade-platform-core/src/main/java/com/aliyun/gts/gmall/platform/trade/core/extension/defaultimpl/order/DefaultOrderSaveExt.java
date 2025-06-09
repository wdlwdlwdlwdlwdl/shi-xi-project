package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.middleware.mq.esindex.entity.OrderDumpInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.PayChannelEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderExtendConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderSaveExt;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.CloseUnpaidOrderTask;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderFeeAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReceiverInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OssRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderExtendRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Component
public class DefaultOrderSaveExt implements OrderSaveExt {

    @Value("${trade.snapshot.enable:true}")
    private boolean snapshotEnable;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private OssRepository ossRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;

    @Autowired
    private TcOrderExtendRepository tcOrderExtendRepository;

    @Autowired
    private OrderConverter orderConverter;

    @Autowired
    private OrderExtendConverter orderExtendConverter;

    @Autowired
    private CloseUnpaidOrderTask closeUnpaidOrderTask;

    @Autowired
    private TransactionProxy transactionProxy;

    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;

    public static final Integer INSERT = 1;
    public static final Integer PRIMARY_ORDER_FLAG = 1;

    public static final Integer DELETE = 0;

    @Override
    public Map beforeSaveOrder(CreatingOrder order) {
        SaveList saveList = new SaveList();
        Date now = new Date();
        String snap = null;
//        if (snapshotEnable) {
//            snap = ossRepository.saveOrderSnapshot(order);
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("now", now);
        map.put("snap", snap);
        map.put("saveList", saveList);
        return map;
    }

    @Override
    public void convertOrderForConfirm(MainOrder main, CreatingOrder order, Map context) {
        bizProcessBeforeSave(main);
        Date now = (Date) context.get("now");
        String snap = (String) context.get("snap");
        SaveList saveList = (SaveList) context.get("saveList");

        // 主订单
        TcOrderDO mainOrder = orderConverter.convertMainOrder(main, order);
        mainOrder.setSnapshotPath(snap);
        saveList.orderList.add(mainOrder);
        
        // 扩展表
        orderExtendConverter.fillMainOrder(main.getOrderExtendList(), main);
        saveList.orderExtendList.addAll(main.getOrderExtendList());

        // 子订单
        for (SubOrder sub : main.getSubOrders()) {
            TcOrderDO subOrder = orderConverter.convertSubOrder(sub, main, order);
            //subOrder.setSnapshotPath(snap);
            saveList.orderList.add(subOrder);
            //扩展表
            orderExtendConverter.fillSubOrder(sub.getOrderExtendList(), sub, main);
            saveList.orderExtendList.addAll(sub.getOrderExtendList());
        }

        // 阶段单
        if (CollectionUtils.isNotEmpty(main.getStepOrders())) {
            for (StepOrder step : main.getStepOrders()) {
                TcStepOrderDO stepOrder = orderConverter.toTcStepOrderDO(step);
                stepOrder.setPrimaryOrderId(main.getPrimaryOrderId());
                saveList.stepOrderList.add(stepOrder);
            }
        }
    }

    @Override
    public void convertOrder(MainOrder main, CreatingOrder order, Map context) {
        bizProcessBeforeSave(main);
//        Date now = (Date) context.get("now");
//        String snap = (String) context.get("snap");
        SaveList saveList = (SaveList) context.get("saveList");
        // 主订单
        TcOrderDO mainOrder = orderConverter.convertMainOrder(main, order);
//        mainOrder.setSnapshotPath(snap);
        saveList.orderList.add(mainOrder);
        // 定时任务
//        ScheduleTask task = closeUnpaidOrderTask.buildTask(new TaskParam(
//            main.getSeller().getSellerId(),
//            main.getPrimaryOrderId(),
//            BizCodeEntity.getOrderBizCode(main))
//        );
//        task.setMainOrder(main);
//        saveList.taskList.add(task);
        // 扩展表
        orderExtendConverter.fillMainOrder(main.getOrderExtendList(), main);
        saveList.orderExtendList.addAll(main.getOrderExtendList());
        // 子订单
        for (SubOrder sub : main.getSubOrders()) {
            TcOrderDO subOrder = orderConverter.convertSubOrder(sub, main, order);
            //subOrder.setSnapshotPath(snap);
            saveList.orderList.add(subOrder);
            //扩展表
            orderExtendConverter.fillSubOrder(sub.getOrderExtendList(), sub, main);
            saveList.orderExtendList.addAll(sub.getOrderExtendList());
        }
        // 阶段单
        if (CollectionUtils.isNotEmpty(main.getStepOrders())) {
            for (StepOrder step : main.getStepOrders()) {
                TcStepOrderDO stepOrder = orderConverter.toTcStepOrderDO(step);
                stepOrder.setPrimaryOrderId(main.getPrimaryOrderId());
                saveList.stepOrderList.add(stepOrder);
            }
        }
    }

    @Override
    public void saveOrder(Map context) {
        SaveList saveList = (SaveList) context.get("saveList");
        transactionProxy.callTx(() -> insertDb(saveList));
    }

    /**
     * 保存订单数据
     * @param saveList
     */
    protected void insertDb(SaveList saveList) {
        // 保存订单
        tcOrderRepository.batchCreate(saveList.orderList);
        // 订单扩展
        saveList.orderExtendList.stream().forEach(p -> {
            tcOrderExtendRepository.create(p);
        });
        // 分布操作订单
        for (TcStepOrderDO step : saveList.stepOrderList) {
            tcStepOrderRepository.insert(step);
        }
        orderTaskService.createScheduledTask(saveList.taskList);
    }

    protected static class SaveList {
        public Map<String, Object> extras = new HashMap<>();
        public List<TcOrderDO> orderList = new ArrayList<>();
        public List<ScheduleTask> taskList = new ArrayList<>();
        public List<TcOrderExtendDO> orderExtendList = new ArrayList<>();
        public List<TcStepOrderDO> stepOrderList = new ArrayList<>();
    }


    // 业务扩展逻辑
    protected void bizProcessBeforeSave(MainOrder main) {

    }

    /**
     * 下单后订单信息推送ES
     * @param creatingOrder
     * @return
     */
    public void pushOrderMessage(CreatingOrder creatingOrder) {
        if (creatingOrder == null || CollectionUtils.isEmpty(creatingOrder.getMainOrders())) {
            return;
        }
        for(MainOrder mainOrder : creatingOrder.getMainOrders()) {
            Long primaryOrderId = mainOrder.getPrimaryOrderId();
            // 查询订单信息
            List<TcOrderDO> orderDOList = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
            Long orderId = orderDOList.stream().filter(tcOrderDO -> PRIMARY_ORDER_FLAG == tcOrderDO.getPrimaryOrderFlag()).findFirst().get().getOrderId();
            for (TcOrderDO tcOrderDO : orderDOList) {
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), INSERT);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getOrderMap(tcOrderDO);
                orderParam.putAll(orderMap);
                // 消息发送体对象
                addEsOrderParam(orderParam, tcOrderDO, orderId);
                orderEsIndexMqClient.push(orderDumpInfo);
            }
        }
    }


    /**
     * 删除下单后订单信息推送ES
     * @param primaryOrderId
     * @return
     */
    @Override
    public void deleteOrderMessage(Long primaryOrderId) {
        if (primaryOrderId == null) {
            return;
        }
        // 查询订单信息
        List<TcOrderDO> orderDOList = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        Long orderId = orderDOList.stream().filter(tcOrderDO -> PRIMARY_ORDER_FLAG.equals(tcOrderDO.getPrimaryOrderFlag())).findFirst().get().getOrderId();
        for (TcOrderDO tcOrderDO : orderDOList) {
            OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), DELETE);
            Map<String, String> orderParam = orderDumpInfo.getOrderParam();
            Map<String, String> orderMap = getOrderMap(tcOrderDO);
            orderParam.putAll(orderMap);
            // 消息发送体对象
            addEsOrderParam(orderParam, tcOrderDO, orderId);
            orderEsIndexMqClient.push(orderDumpInfo);
        }
    }

    /**
     * 对象转MAP
     * @param tcOrderDO
     * @return
     */
    private Map<String, String> getOrderMap(TcOrderDO tcOrderDO) {
        Map<String, String> orderMap = new HashMap<>();
        try {
            Class<?> clazz = tcOrderDO.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object object = field.get(tcOrderDO);
                if (!ObjectUtils.isEmpty(object)) {
                    orderMap.put(field.getName(), JSONObject.toJSONString(field.get(tcOrderDO)));
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("解析 tcOrderDO 异常", e);
        }
        return orderMap;
    }


    /**
     *
     * @param orderParam
     * @param tcOrderDO
     * @param id
     */
    protected void addEsOrderParam(Map<String, String> orderParam, TcOrderDO tcOrderDO, Long id) {
        OrderAttrDO orderAttrDO = tcOrderDO.getOrderAttr();
        ReceiverInfoDO receiveInfo = tcOrderDO.getReceiveInfo();
        SalesInfoDO salesInfo = tcOrderDO.getSalesInfo();
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getPayType())) {
            String payChannel = PayChannelEnum.getCodeByFuzzyName(orderAttrDO.getPayType().split("_")[0]);
            orderParam.put("payChannel", payChannel);
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getSendTime())) {
            orderParam.put("orderSendTime", JSONObject.toJSONString(orderAttrDO.getSendTime()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getConfirmReceiveTime())) {
            orderParam.put("orderConfirmReceiveTime", JSONObject.toJSONString(orderAttrDO.getConfirmReceiveTime()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getLogisticsNos())) {
            orderParam.put("orderLogisticsNo", JSONObject.toJSONString(orderAttrDO.getLogisticsNos()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getLogisticsType())) {
            orderParam.put("logisticsType", String.valueOf(orderAttrDO.getLogisticsType()));
        }
        if (null != receiveInfo) {
            orderParam.put("deliveryAddr", receiveInfo.getDeliveryAddr());
            orderParam.put("destinationCity", receiveInfo.getCity());
            orderParam.put("destinationCityCode", receiveInfo.getCityCode());
        }
        if (null != salesInfo) {
            orderParam.put("merchantAddress",salesInfo.getDeliveryAddr());
            orderParam.put("dispatchCity",salesInfo.getCity());
            orderParam.put("dispatchCityCode",salesInfo.getCityCode());
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getOrderType())) {
            orderParam.put("orderType", String.valueOf(orderAttrDO.getOrderType()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getCurrentStepNo())) {
            orderParam.put("stepNo", String.valueOf(orderAttrDO.getCurrentStepNo()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getCurrentStepStatus())) {
            orderParam.put("stepStatus", String.valueOf(orderAttrDO.getCurrentStepStatus()));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) &&
            !ObjectUtils.isEmpty(orderAttrDO.getExtras()) &&
            !ObjectUtils.isEmpty(orderAttrDO.getExtras().get("sale_type"))) {
            orderParam.put("saleType", orderAttrDO.getExtras().get("sale_type"));
        }
        if (!ObjectUtils.isEmpty(orderAttrDO) && !ObjectUtils.isEmpty(orderAttrDO.getTags())) {
            orderParam.put("tags", String.valueOf(orderAttrDO.getTags()));
        }
        OrderFeeAttrDO orderFeeAttr = tcOrderDO.getOrderFeeAttr();
        if (!ObjectUtils.isEmpty(orderFeeAttr) && !ObjectUtils.isEmpty(orderFeeAttr.getItemOriginAmt())) {
            orderParam.put("subtotal", String.valueOf(orderFeeAttr.getItemOriginAmt()));
        }
        if (!ObjectUtils.isEmpty(orderFeeAttr) && !ObjectUtils.isEmpty(orderFeeAttr.getOrderRealAmt())) {
            orderParam.put("totalPrice", String.valueOf(orderFeeAttr.getTotalAmt()));
        }
        if (!ObjectUtils.isEmpty(orderFeeAttr) && !ObjectUtils.isEmpty(orderFeeAttr.getFreightAmt())) {
            orderParam.put("deliveryFee", String.valueOf(orderFeeAttr.getFreightAmt()));
        }
        if (!ObjectUtils.isEmpty(orderFeeAttr) && !ObjectUtils.isEmpty(orderFeeAttr.getOrderPromotionAmt())) {
            orderParam.put("discountAmt", String.valueOf(orderFeeAttr.getItemDisCountAmt()));
        }
        // 卖家运费默认是0
        orderParam.put("deliveryMerchantFee", "0");
        // 取值覆盖
        if (!ObjectUtils.isEmpty(orderFeeAttr) && !ObjectUtils.isEmpty(orderFeeAttr.getDeliveryMerchantFee())) {
            orderParam.put("deliveryMerchantFee", String.valueOf(orderFeeAttr.getDeliveryMerchantFee()));
        }
        JSONObject relationType = new JSONObject();
        Integer primaryOrderFlag = tcOrderDO.getPrimaryOrderFlag();
        if (PRIMARY_ORDER_FLAG == primaryOrderFlag){
            relationType.put("name", "primary");
        } else {
            relationType.put("name", "child");
            relationType.put("parent", String.valueOf(id));
        }
        orderParam.put("relationType", relationType.toJSONString());
        orderParam.put("primaryOrderId", String.valueOf(tcOrderDO.getPrimaryOrderId()));
        orderParam.put("displayOrderId", tcOrderDO.getDisplayOrderId());
    }
}
