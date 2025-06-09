package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherModifyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherSearchRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("电子凭证接口")
public interface EvoucherFacade {

    @ApiOperation("查询电子凭证信息")
    RpcResponse<List<EvoucherDTO>> queryEvouchers(EvoucherQueryRpcReq req);

    @ApiOperation("电子凭证分页搜索")
    RpcResponse<PageInfo<EvoucherDTO>> queryEvouchersBySearch(EvoucherSearchRpcReq req);

    @ApiOperation("核销")
    RpcResponse writeOff(EvoucherModifyRpcReq req);

}
