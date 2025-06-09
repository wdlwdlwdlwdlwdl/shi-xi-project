package com.aliyun.gts.gmall.platform.trade.api.facade.impl;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.BizScope;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcLogisticsDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcLogisticsReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcLogisticsConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by auto-generated on 2021/02/04.
 */

@Service
@Slf4j
public class TcLogisticsReadFacadeImpl implements TcLogisticsReadFacade {

//    @Autowired
//    private TcLogisticsRepository tcLogisticsRepository;

//    @Autowired
//    private TcLogisticsConverter tcLogisticsConverter;

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private TcLogisticsConverter tcLogisticsConverter;

    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @Override
    public RpcResponse<List<LogisticsDetailDTO>> queryLogisticsDetail(LogisticsDetailQueryRpcReq req) {
        Map<String, Object> context = new HashMap<>();
        context.put("logisticsDetailQueryRpcReq",req);
        FlowNodeResult result = new BizScope<FlowNodeResult>(BizCodeEntity.buildByReq(req)) {
            @Override
            protected FlowNodeResult execute() {
                try {
                    return (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowProperties.getLogisticsQuery(), context);
                } catch (Exception e) {
                    log.error("workflowEngine.invokeAndGetResult occurred exceptions!", e);
                    throw (RuntimeException) e;
                }
            }
        }.invoke();
        if (!result.isSuccess()) {
            log.error("TcLogisticsReadFacadeImpl.queryLogisticsDetail return occurred exceptions!");
            return RpcResponse.fail(result.getFail());
        }
        return RpcResponse.ok((List<LogisticsDetailDTO>)result.getNodeData());
    }

    @Override
    public RpcResponse<TcLogisticsDTO> queryLogistics(LogisticsDetailQueryRpcReq req) {
        TcLogisticsDO logisticsDO =tcLogisticsRepository.queryLogisticsByPrimaryId(req.getPrimaryOrderId(),null);
        return RpcResponse.ok((tcLogisticsConverter.tcLogisticsDOToDTO(logisticsDO)));
    }

}
