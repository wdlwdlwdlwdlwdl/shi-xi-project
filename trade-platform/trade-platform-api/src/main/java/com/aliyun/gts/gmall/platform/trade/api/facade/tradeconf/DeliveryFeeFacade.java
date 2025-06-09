package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明： 商户物流费用配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "商户费用配置", tags = "商户费用配置")
public interface DeliveryFeeFacade {

    @ApiOperation("查询商户物流费用list")
    RpcResponse<PageInfo<DeliveryFeeDTO>> queryDeliveryFee(DeliveryFeeQueryRpcReq req);

    @ApiOperation("保存商户物流费用")
    RpcResponse<DeliveryFeeDTO> saveDeliveryFee(DeliveryFeeRpcReq req);

    @ApiOperation("更新商户物流费用")
    RpcResponse<DeliveryFeeDTO> updateDeliveryFee(DeliveryFeeRpcReq req);

    @ApiOperation("查看商户物流费用")
    RpcResponse<DeliveryFeeDTO> deliveryFeeDetail(DeliveryFeeRpcReq req);
}
