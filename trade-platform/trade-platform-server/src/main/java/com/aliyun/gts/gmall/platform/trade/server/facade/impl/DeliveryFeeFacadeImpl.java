package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFeeFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.DeliveryFeeFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonFeeService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明：商户物流费用配置实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class DeliveryFeeFacadeImpl implements DeliveryFeeFacade {

    @Autowired
    private DeliveryFeeService deliveryFeeService;

    @Override
    public RpcResponse<PageInfo<DeliveryFeeDTO>> queryDeliveryFee(DeliveryFeeQueryRpcReq req) {
        return RpcResponse.ok(deliveryFeeService.queryDeliveryFee(req));
    }

    @Override
    public RpcResponse<DeliveryFeeDTO> saveDeliveryFee(DeliveryFeeRpcReq req) {
        DeliveryFeeDTO dto = deliveryFeeService.saveDeliveryFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<DeliveryFeeDTO> updateDeliveryFee(DeliveryFeeRpcReq req) {
        DeliveryFeeDTO dto = deliveryFeeService.updateDeliveryFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<DeliveryFeeDTO> deliveryFeeDetail(DeliveryFeeRpcReq req) {
        return RpcResponse.ok(deliveryFeeService.deliveryFeeDetail(req));
    }
}
