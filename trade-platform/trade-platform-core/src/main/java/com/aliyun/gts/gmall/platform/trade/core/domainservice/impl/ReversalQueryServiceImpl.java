package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalFeatureKey;
import com.aliyun.gts.gmall.platform.trade.common.constants.SystemReversalReason;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalSearchAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalReasonRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReversalQueryServiceImpl implements ReversalQueryService {

    @Autowired
    private TcReversalReasonRepository tcReversalReasonRepository;
    @Autowired
    private ReversalConverter reversalConverter;
    @Autowired
    private TcReversalRepository tcReversalRepository;
    @Autowired
    private ReversalSearchAbility reversalSearchAbility;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private TcReversalFlowRepository tcReversalFlowRepository;

    @Override
    public List<ReversalReasonDTO> queryReason(Integer reversalType) {
        List<TcReversalReasonDO> list = tcReversalReasonRepository.queryByType(reversalType);
        return list.stream()
                .map(o -> reversalConverter.toReversalReasonDTO(o))
                .collect(Collectors.toList());
    }

    /**
     * 查询售后单
     */
    @Override
    public List<MainReversal> queryReversalByOrder(Long primaryOrderId) {
        List<MainReversal> result = new ArrayList<>();
        List<TcReversalDO> list = tcReversalRepository.queryByPrimaryOrderId(primaryOrderId);
        Map<Long, TcReversalDO> mainMap = new HashMap<>();
        Multimap<Long, TcReversalDO> subMap = HashMultimap.create();
        for (TcReversalDO r : list) {
            if (r.getIsReversalMain() != null && r.getIsReversalMain().booleanValue()) {
                mainMap.put(r.getPrimaryReversalId(), r);
            } else {
                subMap.put(r.getPrimaryReversalId(), r);
            }
        }
        for (TcReversalDO main : mainMap.values()) {
            Collection<TcReversalDO> subs = subMap.get(main.getPrimaryReversalId());
            if (subs != null && !subs.isEmpty()) {
                MainReversal mr = convert(main, subs);
                result.add(mr);
            }
        }
        return result;
    }

    @Override
    public MainReversal queryReversal(Long primaryReversalId, ReversalDetailOption option) {
        List<TcReversalDO> list = tcReversalRepository.queryByPrimaryReversalId(primaryReversalId);
        MainReversal result = toMainReversal(list);
        if (result == null) {
            return null;
        }

        // flow查询
        if (option != null && option.isIncludeFlows()) {
            fillFlows(List.of(result), Collections.singletonList(primaryReversalId));
        }

        // reason查询
        Integer reasonCode = result.getReversalReasonCode();
        if (option != null && option.isIncludeReason() && reasonCode != null) {
            fillReason(List.of(result));
        }

        // order查询
        if (option != null && option.isIncludeOrderInfo()) {
            fillOrderInfo(List.of(result), option.getOrderOption());
        }
        return result;
    }

    @Override
    public List<MainReversal> batchQueryReversal(List<Long> primaryReversalIds, ReversalDetailOption option) {
        List<TcReversalDO> list = tcReversalRepository.batchQueryByPrimaryReversalId(primaryReversalIds);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        List<MainReversal> result = toMainReversals(list);

        // flow查询
        if (option != null && option.isIncludeFlows()) {
            fillFlows(result, primaryReversalIds);
        }

        // reason查询
        if (option != null && option.isIncludeReason()) {
            fillReason(result);
        }

        // order查询
        if (option != null && option.isIncludeOrderInfo()) {
            fillOrderInfo(result, option.getOrderOption());
        }
        return result;
    }

    private List<MainReversal> toMainReversals(List<TcReversalDO> list) {
        Multimap<Long, TcReversalDO> mmap = HashMultimap.create();
        for (TcReversalDO re : list) {
            mmap.put(re.getPrimaryReversalId(), re);
        }
        List<MainReversal> result = new ArrayList<>();
        for (Long pid : mmap.keySet()) {
            Collection<TcReversalDO> primaryList = mmap.get(pid);
            MainReversal re = toMainReversal(primaryList);
            if (re != null) {
                result.add(re);
            }
        }
        return result;
    }

    private MainReversal toMainReversal(Collection<TcReversalDO> list) {
        if (list == null) {
            return null;
        }
        TcReversalDO main = null;
        List<TcReversalDO> subList = new ArrayList<>();
        for (TcReversalDO r : list) {
            if (r.getIsReversalMain() != null && r.getIsReversalMain().booleanValue()) {
                main = r;
            } else {
                subList.add(r);
            }
        }
        if (main == null || subList.isEmpty()) {
            return null;
        }
        return convert(main, subList);
    }

    private void fillFlows(List<MainReversal> list, List<Long> primaryReversalIds) {
        if (list.size() == 1) {
            MainReversal one = list.get(0);
            List<TcReversalFlowDO> flows = tcReversalFlowRepository.query(one.getPrimaryReversalId());
            for(TcReversalFlowDO reversalFlowDO :flows){
                reversalFlowDO.setOpName(OrderStatusEnum.codeOf(reversalFlowDO.getToReversalStatus()).getInner());
            }
            one.setFlows(flows);
            return;
        }
        // 批量
        List<TcReversalFlowDO> batch = tcReversalFlowRepository.batchQuery(primaryReversalIds);
        for(TcReversalFlowDO reversalFlowDO :batch){
            reversalFlowDO.setOpName(OrderStatusEnum.codeOf(reversalFlowDO.getToReversalStatus()).getInner());
        }
        Multimap<Long, TcReversalFlowDO> mmap = HashMultimap.create();
        for (TcReversalFlowDO flow : batch) {
            mmap.put(flow.getPrimaryReversalId(), flow);
        }
        for (MainReversal re : list) {
            Collection<TcReversalFlowDO> flows = mmap.get(re.getPrimaryReversalId());
            re.setFlows(new ArrayList<>(flows));
        }
    }

    private void fillReason(List<MainReversal> list) {
        Set<Integer> query = new HashSet<>();
        Map<Integer, String> map = new HashMap<>();
        for (MainReversal re : list) {
            Integer code = re.getReversalReasonCode();
            if (code == null) {
                continue;
            }
            SystemReversalReason sys = SystemReversalReason.codeOf(code);
            String sysReason = re.reversalFeatures().getExtend(ReversalFeatureKey.SYS_REASON);
            if (sys != null) {
                map.put(code, sys.getName());
            } else if (StringUtils.isNotBlank(sysReason)) {
                map.put(code, sysReason);
            } else {
                query.add(code);
            }
        }
        if (!query.isEmpty()) {
            List<TcReversalReasonDO> reasonList = tcReversalReasonRepository.batchQueryByCode(query);
            for (TcReversalReasonDO reason : reasonList) {
                map.put(reason.getReasonCode(), reason.getReasonMessage());
            }
        }
        for (MainReversal re : list) {
            String content = map.get(re.getReversalReasonCode());
            re.setReversalReasonContent(content);
        }
    }

    @Override
    public ReversalSearchResult searchReversal(ReversalSearchQuery query) {
        return reversalSearchAbility.search(query);
    }

    private MainReversal convert(TcReversalDO main, Collection<TcReversalDO> subs) {
        MainReversal mr = reversalConverter.toMainReversal(main);
        List<SubReversal> srList = new ArrayList<>();
        for (TcReversalDO sub : subs) {
            SubReversal sr = reversalConverter.toSubReversal(sub);
            srList.add(sr);
        }
        mr.setSubReversals(srList);
        return mr;
    }

    public void fillOrderInfo(List<MainReversal> reversalList) {
        fillOrderInfo(reversalList, OrderQueryOption.builder().build());
    }

    @Override
    public void fillOrderInfo(List<MainReversal> reversalList, OrderQueryOption option) {
        if (CollectionUtils.isEmpty(reversalList)) {
            return;
        }
        List<Long> ordIds = reversalList.stream()
            .filter(re -> re != null)
            .map(re -> {
                MainOrder mainReversal = re.getMainOrder();
                return mainReversal != null ? mainReversal.getPrimaryOrderId() : null;
            })
            .filter(Objects::nonNull) // 过滤掉 null 值
            .distinct()
            .collect(Collectors.toList());
        List<MainOrder> ordList = orderQueryAbility.batchGetMainOrder(ordIds, option);
        Map<Long, MainOrder> mainOrdMap = CommUtils.toMap(ordList, MainOrder::getPrimaryOrderId);
        Map<Long, SubOrder> subOrdMap = ordList
            .stream()
            .flatMap(ord -> ord.getSubOrders().stream())
            .collect(Collectors.toMap(ord -> ord.getOrderId(), ord -> ord));
        for (MainReversal re : reversalList) {
            if (re != null) {
                MainOrder mainOrd = mainOrdMap.get(re.getMainOrder().getPrimaryOrderId());
                if (mainOrd != null) {
                    re.setMainOrder(mainOrd);
                    re.setOrderCreate(mainOrd.getGmtCreate());
                }
                for (SubReversal sub : re.getSubReversals()) {
                    SubOrder subOrd = subOrdMap.get(sub.getSubOrder().getOrderId());
                    if (subOrd != null) {
                        sub.setSubOrder(subOrd);
                    }
                }
            }
        }
    }

    @Override
    public List<MainReversalDTO> queryRefundMerchant(ReversalRpcReq req) {
        if(req.getReversalReasonCode()!=null){
            //不是理论上售后 实际是订单取消
            List<MainReversalDTO> list = tcOrderRepository.statisticsSellerByCancelCodeAndTime(req);
            return list;
        }
        return null;
    }

    @Override
    public ReversalDTO statisticsReversal(ReversalRpcReq req) {
        ReversalDTO dto = new ReversalDTO();
        //查询指定时间区间订单数量 后续加缓存
        //查询退单数量
        dto.setSellerId(req.getSellerId());
        dto.setOrderCount(tcOrderRepository.statisticsOrderByTime(req));
        dto.setReversalCount(tcOrderRepository.statisticsOrderByCancelCodeAndTime(req));
        return dto;
    }

}
