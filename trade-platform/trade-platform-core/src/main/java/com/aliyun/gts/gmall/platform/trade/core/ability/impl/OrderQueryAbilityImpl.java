package com.aliyun.gts.gmall.platform.trade.core.ability.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultCustomOrderQueryExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.CustomOrderQueryExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderExtendRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepTemplateRepository;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Ability(
    code = OrderQueryAbilityImpl.ORDER_QUERY_ABILITY,
    fallback = DefaultCustomOrderQueryExt.class,
    description = "订单查询能力，可基于基础查询能力新增扩展查询"
)
@Component
public class OrderQueryAbilityImpl extends BaseAbility<BizCodeEntity, CustomOrderQueryExt> implements OrderQueryAbility {

    public static final String ORDER_QUERY_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.impl.OrderQueryAbilityImpl";

    @Autowired
    private SearchClient searchClient;
    @Autowired
    private TcOrderExtendRepository orderExtendRepository;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private TcStepTemplateRepository tcStepTemplateRepository;
    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;

    @Override
    public MainOrder getMainOrder(Long primaryOrderId) {
        return getMainOrder(primaryOrderId, OrderQueryOption.builder().includeExtends(true).build());
    }

    @Override
    public MainOrder getMainOrder(Long primaryOrderId, OrderQueryOption option) {
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return toMainOrder(list, primaryOrderId, option);
    }

    @Override
    public List<MainOrder> batchGetMainOrder(List<Long> primaryOrderIds) {
        return batchGetMainOrder(primaryOrderIds, OrderQueryOption.builder().build());
    }

    @Override
    public List<MainOrder> batchGetMainOrder(List<Long> primaryOrderIds, OrderQueryOption option) {
        List<MainOrder> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(primaryOrderIds)) {
            return result;
        }
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryIds(primaryOrderIds);
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Multimap<Long, TcOrderDO> mmap = HashMultimap.create();
        for (TcOrderDO ord : list) {
            mmap.put(ord.getPrimaryOrderId(), ord);
        }
        // Extends 用批量接口来补
        OrderQueryOption clone = OrderQueryOption.builder().build();
        if (option != null) {
            BeanUtils.copyProperties(option, clone);
        }
        clone.setIncludeExtends(false);
        for (Long primaryOrderId : mmap.keySet()) {
            Collection<TcOrderDO> mainList = mmap.get(primaryOrderId);
            MainOrder mainOrder = toMainOrder(mainList, primaryOrderId, clone);
            result.add(mainOrder);
        }
        if (option != null && option.isIncludeExtends()) {
            fillOrderExtends(result);
        }
        return result;
    }

    @Override
    public MainOrder getBoughtMainOrder(Long primaryOrderId, OrderQueryOption option) {
        List<TcOrderDO> list = tcOrderRepository.queryBoughtDetailByPrimaryId(primaryOrderId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return toMainOrder(list, primaryOrderId, option);
    }

    @Override
    public void fillOrderExtends(List<MainOrder> orders) {
        Multimap<Long /*custId*/, Long /*primaryOrderId*/> reqMap = HashMultimap.create();
        orders.stream().forEach(ord -> reqMap.put(ord.getCustomer().getCustId(), ord.getPrimaryOrderId()));
        List<TcOrderExtendDO> allList = new ArrayList<>();
        for (Long custId : reqMap.keySet()) {
            List<TcOrderExtendDO> list = orderExtendRepository.queryExtendsByPrimaryOrderIds(
                custId,
                new ArrayList<>(reqMap.get(custId))
            );
            allList.addAll(list);
        }
        Map<Long, List<TcOrderExtendDO>> resMap = allList.stream()
            .collect(Collectors.groupingBy(TcOrderExtendDO::getOrderId));
        orders.stream().forEach(main -> {
            List<TcOrderExtendDO> mainList = resMap.get(main.getPrimaryOrderId());
            if (mainList != null) {
                main.setOrderExtendList(mainList);
            }
            main.getSubOrders().stream().forEach(sub -> {
                List<TcOrderExtendDO> subList = resMap.get(sub.getOrderId());
                if (subList != null) {
                    sub.setOrderExtendList(subList);
                }
            });
        });
    }

    /**
     * 分步订单
     *     预售 定金尾款
     * @param orders
     */
    @Override
    public void fillStepOrders(List<MainOrder> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        for (MainOrder main : orders) {
            // 多步骤订单
            if (OrderTypeEnum.codeOf(main.getOrderType()) == OrderTypeEnum.MULTI_STEP_ORDER) {
                fillStepOrders(main);
            }
        }
    }

    /**
     * 主单对象转换
     * @param list
     * @param primaryOrderId
     * @param option
     * @return
     */
    private MainOrder toMainOrder(Collection<TcOrderDO> list, Long primaryOrderId, OrderQueryOption option) {
        TcOrderDO main = null;
        List<TcOrderDO> subList = new ArrayList<>();
        for (TcOrderDO ord : list) {
            PrimaryOrderFlagEnum flag = PrimaryOrderFlagEnum.codeOf(ord.getPrimaryOrderFlag());
            if (flag == PrimaryOrderFlagEnum.PRIMARY_ORDER) {
                main = ord;
            } else if (flag == PrimaryOrderFlagEnum.SUB_ORDER) {
                subList.add(ord);
            }
        }
        MainOrder mainEntity = orderConverter.convertMainOrder(main);
        mainEntity.setSubOrders(new ArrayList<>());
        Map<Long, Integer> subReversalStatus = new HashMap<>();
        if (option != null && option.isIncludeReversalInfo()) {
            subReversalStatus = assembleReversalStatus(primaryOrderId);
        }
        for (TcOrderDO sub : subList) {
            SubOrder subEntity = orderConverter.convertSubOrder(sub);
            subEntity.setReversalStatus(subReversalStatus.get(sub.getOrderId()));
            mainEntity.getSubOrders().add(subEntity);
        }
        List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(mainEntity);
        for (BizCodeEntity bizCode : bizCodes) {
            this.executeExt(bizCode,
                extension -> extension.enrichMainOrder(mainEntity),
                CustomOrderQueryExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        if (Objects.nonNull(option) && option.isIncludeExtends()) {
            fillOrderExtends(List.of(mainEntity));
        }
        // 多阶段
        if (OrderTypeEnum.codeOf(mainEntity.getOrderType()) == OrderTypeEnum.MULTI_STEP_ORDER &&
            Objects.nonNull(option) &&
            option.isIncludeStepOrder()) {
            fillStepOrders(mainEntity);
        }
        return mainEntity;
    }

    private Map<Long, Integer> assembleReversalStatus(Long primaryOrderId) {
        Map<Long, Integer> subIdToReversalStatus = new HashMap<>();
        Map<Long, Date> subToReversalDate = new HashMap<>();
        List<MainReversal> reversalList = reversalQueryService.queryReversalByOrder(primaryOrderId);
        if (reversalList != null && reversalList.size() > 0) {
            for (MainReversal mainReversal : reversalList) {
                for (SubReversal useful : mainReversal.getSubReversals()) {
                    Long subOrderId = useful.getSubOrder().getOrderId();
                    Date reversalDate = subToReversalDate.get(subOrderId);
                    if (reversalDate == null || reversalDate.before(mainReversal.getGmtCreate())) {
                        subIdToReversalStatus.put(subOrderId, mainReversal.getReversalStatus());
                        subToReversalDate.put(subOrderId, mainReversal.getGmtCreate());
                    }
                }
            }
        }
        return subIdToReversalStatus;
    }

    /**
     * 多步骤订单
     * @param mainOrder
     */
    private void fillStepOrders(MainOrder mainOrder) {
        // 模版
        String name = mainOrder.getStepTemplate().getTemplateName();
        // 步骤模板
        StepTemplate template = tcStepTemplateRepository.queryParsedTemplateWithCache(name);
        if (template == null) {
            throw new GmallException(
                CommonErrorCode.SERVER_ERROR_WITH_ARG,
                I18NMessageUtils.getMessage("multi.stage.template.not.exist")+":" + name
            );  //# "多阶段模版不存在
        }
        mainOrder.setStepTemplate(template);
        // 阶段单信息
        List<TcStepOrderDO> stepList = tcStepOrderRepository.queryByPrimaryId(mainOrder.getPrimaryOrderId());
        List<StepOrder> convertList = new ArrayList<>();
        for (TcStepOrderDO step : stepList) {
            StepOrder stepOrder = orderConverter.toStepOrder(step);
            convertList.add(stepOrder);
        }
        mainOrder.setStepOrders(convertList);
    }
}
