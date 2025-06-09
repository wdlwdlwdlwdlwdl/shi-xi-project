package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.item.api.dto.output.EvoucherPeriodDTO;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.trade.common.constants.EvoucherStatusEnum;
import com.aliyun.gts.gmall.center.trade.core.constants.EvoucherErrorCode;
import com.aliyun.gts.gmall.center.trade.core.converter.EvoucherConverter;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherCreateService;
import com.aliyun.gts.gmall.center.trade.core.task.biz.EvoucherTimeoutTask;
import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.center.trade.core.util.ItemUtils;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherTemplate;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.sequence.Sequence;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import io.netty.util.internal.ThreadLocalRandom;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.center.trade.common.util.DateUtils.ON_DAY_MILLIS;
import static com.aliyun.gts.gmall.center.trade.common.util.DateUtils.toDayEnd;

@Service
public class EvoucherCreateServiceImpl implements EvoucherCreateService {
    private static final String CODE_SEQ_NAME = "evoucher_code";
    private static final long CODE_SEQ_MASK = 10000;
    private static final int CODE_RAND = 10000;

    private static final String KEY_EV_TEMPLATE = "ev_template";

    @Autowired
    private EvoucherConverter evoucherConverter;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private Sequence shardSequence;
    @Autowired
    private EvoucherTimeoutTask evoucherTimeoutTask;
    @Autowired
    private OrderTaskService orderTaskService;

    @Override
    public EvoucherTemplate getEvTemplate(ItemSku itemSku) {
        EvoucherPeriodDTO itemEv = getItemEvoucherInfo(itemSku);
        if (itemEv == null) {
            throw new GmallException(EvoucherErrorCode.ORDER_ITEM_MISS_EV_INFO);
        }
        return evoucherConverter.toEvTemplate(itemEv);
    }

    @Override
    public void addEvFeature(Map<String, String> features, EvoucherTemplate template) {
        features.put(KEY_EV_TEMPLATE, JSON.toJSONString(template));
    }

    @Override
    public EvoucherTemplate getFromEvFeature(SubOrder subOrder) {
        if (subOrder == null
            || subOrder.getOrderAttr() == null
            || subOrder.getOrderAttr().getExtras() == null) {
            return null;
        }
        Map<String, String> extras = subOrder.getOrderAttr().getExtras();
        String evInfo = extras.get(KEY_EV_TEMPLATE);
        if (StringUtils.isBlank(evInfo)) {
            return null;
        }
        return JSON.parseObject(evInfo, EvoucherTemplate.class);
    }

    @Override
    public List<EvoucherInstance> createEvInstance(SubOrder subOrder, MainOrder mainOrder) {
        EvoucherTemplate template = getFromEvFeature(subOrder);
        if (template == null) {
            throw new GmallException(EvoucherErrorCode.ORDER_MISS_EV_TEMPLATE);
        }
        return create(template, subOrder, mainOrder);
    }

    @Override
    @Transactional
    public void saveEv(MainOrder order, List<EvoucherInstance> evList) {
        // 保存电子凭证
        Map<Long, List<EvoucherInstance>> evMap = evList.stream()
                .collect(Collectors.groupingBy(EvoucherInstance::getOrderId));
        long maxEndTime = 0L;
        for (SubOrder subOrder : order.getSubOrders()) {
            List<TcEvoucherDO> exist = tcEvoucherRepository.queryByOrderId(subOrder.getOrderId());
            if (CollectionUtils.isNotEmpty(exist)) {
                throw new GmallException(EvoucherErrorCode.REPEAT_SEND_EV);
            }
            List<EvoucherInstance> orderEv = evMap.get(subOrder.getOrderId());
            if (CollectionUtils.isEmpty(orderEv)) {
                throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG,
                        I18NMessageUtils.getMessage("missing.voucher")+":" + subOrder.getOrderId());  //# "缺少电子凭证
            }
            for (EvoucherInstance ev : orderEv) {
                tcEvoucherRepository.create(ev);
                if (ev.getEndTime() != null) {
                    maxEndTime = Math.max(maxEndTime, ev.getEndTime().getTime());
                }
            }
        }

        // 保存订单状态
        OrderStatus status = new OrderStatus();
        status.setPrimaryOrderId(order.getPrimaryOrderId());
        status.setCheckStatus(OrderStatusEnum.ORDER_WAIT_DELIVERY);
        status.setStatus(OrderStatusEnum.ORDER_SENDED);
        TradeBizResult<List<TcOrderDO>> result = orderStatusService.changeOrderStatus(status);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(result.getFail()));
        }

        // 创建定时任务
        if (maxEndTime > 0) {
            ScheduleTimeTaskParam param = new ScheduleTimeTaskParam();
            param.setPrimaryOrderId(order.getPrimaryOrderId());
            param.setSellerId(order.getSeller().getSellerId());
            param.setBizCodes(BizCodeEntity.getOrderBizCode(order));
            param.setScheduleTime(new Date(maxEndTime));
            ScheduleTask task = evoucherTimeoutTask.buildTask(param);
            orderTaskService.createScheduledTask(Arrays.asList(task));
        }

        // 发消息、记流水
        orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
                .orderList(result.getData())
                .op(OrderChangeOperateEnum.SELLER_SEND)
                .build());
    }

    private List<EvoucherInstance> create(EvoucherTemplate template, SubOrder order, MainOrder main) {
        Date start = null;
        Date end = null;
        switch (template.getType().intValue()) {
            case EvoucherTemplate.TYPE_1:  // 购买后day天有效
                long date = System.currentTimeMillis() + template.getDay() * ON_DAY_MILLIS;
                end = new Date(toDayEnd(date));
                break;
            case EvoucherTemplate.TYPE_2:  // 购买后到end有效
                end = template.getEnd();
                break;
            case EvoucherTemplate.TYPE_3:  // start到end有效
                start = template.getStart();
                end = template.getEnd();
                break;
            case EvoucherTemplate.TYPE_4:  // 长期有效
                break;
            default:
                throw new GmallException(EvoucherErrorCode.ORDER_EV_TEMPLATE_ERROR);
        }

        int total = order.getOrderQty();
        List<EvoucherInstance> result = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            EvoucherInstance inst = new EvoucherInstance();
            inst.setStartTime(start);
            inst.setEndTime(end);
            inst.setOrderId(order.getOrderId());
            inst.setStatus(EvoucherStatusEnum.NOT_USED.getCode());
            inst.setEvCode(nextCode(order.getOrderId()));
            inst.setCustId(main.getCustomer().getCustId());
            inst.setCustName(main.getCustomer().getCustName());
            inst.setSellerId(main.getSeller().getSellerId());
            inst.setSellerName(main.getSeller().getSellerName());
            result.add(inst);
        }
        return result;
    }

    // 规则: [seq] + [4位随机数] + [4位订单尾号]
    private Long nextCode(Long orderId) {
        long seq = shardSequence.nextValue(CODE_SEQ_NAME);
        int rand = ThreadLocalRandom.current().nextInt(CODE_RAND);
        return (seq * CODE_RAND + rand) * CODE_SEQ_MASK + (orderId % CODE_SEQ_MASK);
    }

    private static EvoucherPeriodDTO getItemEvoucherInfo(ItemSku itemSku) {
        return ItemUtils.getExtendObject(itemSku, ItemExtendConstant.EVOUCHER_PERIOD, EvoucherPeriodDTO.class);
    }
}
