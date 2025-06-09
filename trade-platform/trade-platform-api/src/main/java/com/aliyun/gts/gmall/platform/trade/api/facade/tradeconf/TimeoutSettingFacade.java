package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明： 超时配置
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "超时配置", tags = "超时配置")
public interface TimeoutSettingFacade {

    @ApiOperation("查询超时配置list")
    RpcResponse<PageInfo<TimeoutSettingDTO>> queryTimeoutSetting(TimeoutSettingQueryRpcReq req);

    @ApiOperation("保存超时配置")
    RpcResponse<TimeoutSettingDTO> saveTimeoutSetting(TimeoutSettingRpcReq req);

    @ApiOperation("更新超时配置")
    RpcResponse<TimeoutSettingDTO> updateTimeoutSetting(TimeoutSettingRpcReq req);

    @ApiOperation("查看超时配置")
    RpcResponse<TimeoutSettingDTO> timeoutSettingDetail(TimeoutSettingRpcReq req);

}
