package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderExtendConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderExtendRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrderExtraServiceImpl implements OrderExtraService {

    @Autowired
    private OrderExtendConverter orderExtendConverter;

    @Autowired
    private TcOrderExtendRepository tcOrderExtendRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Override
    public List<OrderExtendDTO> queryOrderExtend(OrderExtendQueryRpcReq req) {
        TcOrderExtendDO params = orderExtendConverter.toTcOrderExtendDO(req);
        List<TcOrderExtendDO> list = tcOrderExtendRepository.queryByParams(params);
        return orderExtendConverter.toOrderExtendDTOList(list);
    }

    @Override
    @Transactional
    public void saveOrderExtras(OrderExtraSaveRpcReq req) {
        TcOrderDO order;
        if (req.getOrderId() != null) {
            order = tcOrderRepository.querySubByOrderId(req.getPrimaryOrderId(), req.getOrderId());
        } else {
            order = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
        }
        if (order == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        if (req.getCustId() != null && req.getCustId().longValue() != order.getCustId().longValue()) {
            throw new GmallException(OrderErrorCode.ORDER_USER_NOT_MATCH);
        }
        if (req.getSellerId() != null && req.getSellerId().longValue() != order.getSellerId().longValue()) {
            throw new GmallException(OrderErrorCode.ORDER_USER_NOT_MATCH);
        }

        if (MapUtils.isNotEmpty(req.getAddFeatures())
                || CollectionUtils.isNotEmpty(req.getRemoveFeatures())) {
            saveFeatures(req, order);
        }
        if (MapUtils.isNotEmpty(req.getAddExtends())
                || MapUtils.isNotEmpty(req.getRemoveExtendKeys())
                || CollectionUtils.isNotEmpty(req.getRemoveExtendTypes())) {
            saveExtends(req, order);
        }
        if (CollectionUtils.isNotEmpty(req.getAddTags())
                || CollectionUtils.isNotEmpty(req.getRemoveTags())) {
            saveTags(req, order);
        }
    }

    private void saveFeatures(OrderExtraSaveRpcReq req, TcOrderDO order) {
        Map<String, String> extMap = order.getOrderAttr().extras();
        if (MapUtils.isNotEmpty(req.getAddFeatures())) {
            extMap.putAll(req.getAddFeatures());
        }
        if (CollectionUtils.isNotEmpty(req.getRemoveFeatures())) {
            for (String key : req.getRemoveFeatures()) {
                extMap.remove(key);
            }
        }

        TcOrderDO up = new TcOrderDO();
        up.setOrderId(order.getOrderId());
        up.setPrimaryOrderId(order.getPrimaryOrderId());
        up.setVersion(order.getVersion());
        up.setOrderAttr(order.getOrderAttr());
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

    private void saveExtends(OrderExtraSaveRpcReq req, TcOrderDO order) {
        if (MapUtils.isNotEmpty(req.getAddExtends())) {
            List<TcOrderExtendDO> list = orderExtendConverter.toExtendList(req.getAddExtends());
            orderExtendConverter.fillOrder(list, order);
            for (TcOrderExtendDO ext : list) {
                tcOrderExtendRepository.insertOrUpdate(ext);
            }
        }
        if (MapUtils.isNotEmpty(req.getRemoveExtendKeys())) {
            tcOrderExtendRepository.deleteByKeys(order.getPrimaryOrderId(), order.getOrderId(), req.getRemoveExtendKeys());
        }
        if (CollectionUtils.isNotEmpty(req.getRemoveExtendTypes())) {
            tcOrderExtendRepository.deleteByTypes(order.getPrimaryOrderId(), order.getOrderId(), req.getRemoveExtendTypes());
        }
    }

    private void saveTags(OrderExtraSaveRpcReq req, TcOrderDO order) {
        Set<String> merge = new LinkedHashSet<>();
        List<String> exist = order.getOrderAttr().getTags();
        if (CollectionUtils.isNotEmpty(exist)) {
            merge.addAll(exist);
        }
        if (CollectionUtils.isNotEmpty(req.getAddTags())) {
            merge.addAll(req.getAddTags());
        }
        if (CollectionUtils.isNotEmpty(req.getRemoveTags())) {
            merge.removeAll(req.getRemoveTags());
        }
        order.getOrderAttr().setTags(new ArrayList<>(merge));

        TcOrderDO up = new TcOrderDO();
        up.setOrderId(order.getOrderId());
        up.setPrimaryOrderId(order.getPrimaryOrderId());
        up.setVersion(order.getVersion());
        up.setOrderAttr(order.getOrderAttr());
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }
}
