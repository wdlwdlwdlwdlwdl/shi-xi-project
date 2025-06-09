package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CityDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryAccessDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CityFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 说明：城市配置实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class CityFacadeImpl implements CityFacade {

    @Autowired
    private CityService cityService;

    @Override
    public RpcResponse<PageInfo<CityDTO>> queryCity(CityQueryRpcReq req) {
        return RpcResponse.ok(cityService.queryCity(req));
    }

    @Override
    public RpcResponse<List<CityDTO>> queryCityList(CityRpcReq req){
        if(req.getCodes().isEmpty()){
            throw new GmallException(OrderErrorCode.ORDER_QUERY_PARAM_ERROR);
        }
        return RpcResponse.ok(cityService.queryCityList(req));
    }

    @Override
    public RpcResponse<DeliveryAccessDTO> queryDeliveryAccess(CityRpcReq req) {
        if(!cityService.exist(req)){
            throw new GmallException(CommonResponseCode.NotFound);
        }
        boolean isAccess = cityService.isAccess(req);
        DeliveryAccessDTO dto = new DeliveryAccessDTO();
        dto.setAccess(isAccess);
        return RpcResponse.ok(dto);
    }

    @Override
    public RpcResponse<CityDTO> saveCity(CityRpcReq req) {
        if(cityService.exist(req)){
            throw new GmallException(CommonResponseCode.AlreadyExists);
        }
        CityDTO dto = cityService.saveCity(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CityDTO> updateCity(CityRpcReq req) {
        if (StringUtils.isNotEmpty(req.getCityCode())) {
            // 校验城市
            CityRpcReq cityRpcReq = new CityRpcReq();
            List<String> codes = new ArrayList<>();
            codes.add(req.getCityCode());
            cityRpcReq.setCodes(codes);
            List<CityDTO> cityDTOList = cityService.queryCityList(cityRpcReq);
            if (CollectionUtils.isNotEmpty(cityDTOList)) {
                CityDTO checkObj = cityDTOList.stream()
                    .filter(Objects::nonNull)
                    .filter(cityDTO -> !cityDTO.getId().equals(req.getId()))
                    .findFirst()
                    .orElse(null);
                if (Objects.nonNull(checkObj)) {
                    throw new GmallException(CommonResponseCode.AlreadyExists);
                }
            }
        }
        CityDTO dto = cityService.updateCity(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CityDTO> cityDetail(CityRpcReq req) {
        return RpcResponse.ok(cityService.cityDetail(req));
    }
}
