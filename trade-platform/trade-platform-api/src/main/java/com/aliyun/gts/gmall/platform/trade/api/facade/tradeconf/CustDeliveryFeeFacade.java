package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明： 商户物流费用配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "客户费用配置", tags = "客户费用配置")
public interface CustDeliveryFeeFacade {

    @ApiOperation("查询客户物流费用list")
    RpcResponse<PageInfo<CustDeliveryFeeDTO>> queryCustDeliveryFee(CustDeliveryFeeQueryRpcReq req);

    @ApiOperation("保存客户物流费用")
    RpcResponse<CustDeliveryFeeDTO> saveCustDeliveryFee(CustDeliveryFeeRpcReq req);

    @ApiOperation("更新客户物流费用")
    RpcResponse<CustDeliveryFeeDTO> updateCustDeliveryFee(CustDeliveryFeeRpcReq req);

    @ApiOperation("查看客户物流费用")
    RpcResponse<CustDeliveryFeeDTO> custDeliveryFeeDetail(CustDeliveryFeeRpcReq req);
}
