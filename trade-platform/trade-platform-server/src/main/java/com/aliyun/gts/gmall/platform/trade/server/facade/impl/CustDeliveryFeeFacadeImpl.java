package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CustDeliveryFeeFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CustDeliveryFeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 说明：客户物流费用配置实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class CustDeliveryFeeFacadeImpl implements CustDeliveryFeeFacade {

    @Autowired
    private CustDeliveryFeeService custDeliveryFeeService;

    @Override
    public RpcResponse<PageInfo<CustDeliveryFeeDTO>> queryCustDeliveryFee(CustDeliveryFeeQueryRpcReq req) {
        return RpcResponse.ok(custDeliveryFeeService.queryCustDeliveryFee(req));
    }

    @Override
    public RpcResponse<CustDeliveryFeeDTO> saveCustDeliveryFee(CustDeliveryFeeRpcReq req) {
        log.info("saveCustDeliveryFee req={}",req);
        if(custDeliveryFeeService.exist(req)){
            throw new GmallException(CommonResponseCode.AlreadyExists);
        }
        CustDeliveryFeeDTO dto = custDeliveryFeeService.saveCustDeliveryFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CustDeliveryFeeDTO> updateCustDeliveryFee(CustDeliveryFeeRpcReq req) {
        if (Objects.nonNull(req.getDeliveryType()) && Objects.nonNull(req.getDeliveryRoute())) {
            // 重复校验
            CustDeliveryFeeQueryRpcReq custDeliveryFeeQueryRpcReq = new CustDeliveryFeeQueryRpcReq();
            custDeliveryFeeQueryRpcReq.setDeliveryType(req.getDeliveryType());
            custDeliveryFeeQueryRpcReq.setDeliveryRoute(req.getDeliveryRoute());
            List<CustDeliveryFeeDTO> list = custDeliveryFeeService.queryCustDeliveryFeeList(custDeliveryFeeQueryRpcReq);
            if (CollectionUtils.isNotEmpty(list)) {
                CustDeliveryFeeDTO exist = list.stream()
                    .filter(Objects::nonNull)
                    .filter(custDeliveryFeeDTO -> !custDeliveryFeeDTO.getId().equals(req.getId()))
                    .findFirst()
                    .orElse(null);
                if (Objects.nonNull(exist)) {
                    throw new GmallException(CommonResponseCode.AlreadyExists);
                }
            }
        }
        CustDeliveryFeeDTO dto = custDeliveryFeeService.updateCustDeliveryFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CustDeliveryFeeDTO> custDeliveryFeeDetail(CustDeliveryFeeRpcReq req) {
        return RpcResponse.ok(custDeliveryFeeService.custDeliveryFeeDetail(req));
    }
}
