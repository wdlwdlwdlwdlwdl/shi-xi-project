package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CityDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryAccessDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 说明： 城市配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "城市配置", tags = "城市配置")
public interface CityFacade {

    @ApiOperation("查询城市list")
    RpcResponse<PageInfo<CityDTO>> queryCity(CityQueryRpcReq req);

    @ApiOperation("保存城市")
    RpcResponse<CityDTO> saveCity(CityRpcReq req);

    @ApiOperation("更新城市")
    RpcResponse<CityDTO> updateCity(CityRpcReq req);

    @ApiOperation("查看城市")
    RpcResponse<CityDTO> cityDetail(CityRpcReq req);

    RpcResponse<List<CityDTO>> queryCityList(CityRpcReq req);

    RpcResponse<DeliveryAccessDTO> queryDeliveryAccess(CityRpcReq req);


}
