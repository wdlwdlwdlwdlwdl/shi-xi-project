package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.GlobalConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.GlobalConfigRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerIdRpcReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "交易配置", tags = "交易配置")
public interface TradeConfigFacade {

    @ApiOperation("查询卖家的交易设置")
    RpcResponse<SellerConfigDTO> getSellerConfig(SellerIdRpcReq req);

    @ApiOperation("保存卖家交易设置")
    RpcResponse saveSellerConfig(SellerConfigDTO req);

    @ApiOperation("查询全局默认的交易设置")
    RpcResponse<GlobalConfigDTO> getDefaultConfig(GlobalConfigRpcReq req);

    @ApiOperation("保存全局默认的交易设置")
    RpcResponse saveDefaultConfig(GlobalConfigDTO req);
}
