package com.aliyun.gts.gmall.platform.trade.server.flow.handler.logistics;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
import com.aliyun.gts.gmall.middleware.logistics.constants.KdnLogisticsCompanyEnum;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.ReceiverInfoDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LogisticsDetailQueryHandler implements ProcessFlowNodeHandler<LogisticsDetailQueryRpcReq, FlowNodeResult> {

    @Autowired
    TcLogisticsRepository tcLogisticsRepository;

    @Autowired
    LogisticsComponent logisticsComponent;

    @Override
    public FlowNodeResult<List<LogisticsDetailDTO>> handleBiz(Map<String, Object> map,
        LogisticsDetailQueryRpcReq req) {

        List<TcLogisticsDO> list = tcLogisticsRepository.queryByPrimaryId(req.getPrimaryOrderId(), req.getPrimaryReversalId());
        List<LogisticsDetailDTO> result = new ArrayList<>();
        if(list != null){
            for(TcLogisticsDO tcLogisticsDO : list){
                if(req.getCustId() != null ){
                    if(!req.getCustId().equals(tcLogisticsDO.getCustId())){
                        throw new GmallException(OrderErrorCode.LOGISTICS_USER_NOT_MATCH);
                    }
                }
                if(req.getSellerId() != null ){
                    if(!req.getSellerId().equals(tcLogisticsDO.getSellerId())){
                        throw new GmallException(OrderErrorCode.LOGISTICS_USER_NOT_MATCH);
                    }
                }
                LogisticsDetailDTO logisticsDetailDTO = new LogisticsDetailDTO();
                logisticsDetailDTO.setPrimaryOrderId(req.getPrimaryOrderId());
                ReceiverInfoDTO receiverInfoDTO = new ReceiverInfoDTO();
                logisticsDetailDTO.setReceiverInfo(receiverInfoDTO);
                receiverInfoDTO.setReceiverAddr(tcLogisticsDO.getReceiverAddr());
                receiverInfoDTO.setReceiverName(tcLogisticsDO.getReceiverName());
                receiverInfoDTO.setReceiverPhone(tcLogisticsDO.getReceiverPhone());
                logisticsDetailDTO.setLogisticsAttr(tcLogisticsDO.getLogisticsAttr());
                logisticsDetailDTO.setDeliveryId(tcLogisticsDO.getLogisticsId());

                KdnLogisticsCompanyEnum company = KdnLogisticsCompanyEnum.codeOf(tcLogisticsDO.getCompanyType());
                if (company != null) {
                    logisticsDetailDTO.setDeliveryCompany(company.getName());
                    List<LogisticsFlowDTO> traces = queryTrace(tcLogisticsDO, company);
                    logisticsDetailDTO.setFlowList(traces);
                }
                result.add(logisticsDetailDTO);
            }
        }
        return FlowNodeResult.ok(result);
    }

    private List<LogisticsFlowDTO> queryTrace(TcLogisticsDO logistics, KdnLogisticsCompanyEnum company) {
        LogisticsQueryReq req = new LogisticsQueryReq();
        req.setCompanyCode(company.getCompanyCode());
        req.setLogisticsCode(logistics.getLogisticsId());
        req.setCustomerPhone(logistics.getReceiverPhone());

        List<com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsFlowDTO> list
                = logisticsComponent.queryTrace(req);

        List<LogisticsFlowDTO> result = new ArrayList<>();
        for (com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsFlowDTO flow : list) {
            LogisticsFlowDTO convert = new LogisticsFlowDTO();
            BeanUtils.copyProperties(flow, convert);
            result.add(convert);
        }
        return result;
    }
}
