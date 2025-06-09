package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherModifyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherSearchRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.EvoucherFacade;
import com.aliyun.gts.gmall.center.trade.core.converter.EvoucherConverter;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherProcessService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherQueryService;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EvoucherFacadeImpl implements EvoucherFacade {

    @Autowired
    private EvoucherProcessService evoucherProcessService;
    @Autowired
    private EvoucherConverter evoucherConverter;
    @Autowired
    private EvoucherQueryService evoucherQueryService;

    @Override
    public RpcResponse<List<EvoucherDTO>> queryEvouchers(EvoucherQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            List<EvoucherInstance> list = evoucherQueryService.queryEvouchers(req);
            List<EvoucherDTO> dtoList = evoucherConverter.toDtoList(list);
            return RpcResponse.ok(dtoList);
        }, "EvoucherFacadeImpl.queryEvouchers");
    }

    @Override
    public RpcResponse<PageInfo<EvoucherDTO>> queryEvouchersBySearch(EvoucherSearchRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            PageInfo<EvoucherInstance> search = evoucherQueryService.searchEvouchers(req);
            PageInfo<EvoucherDTO> result = new PageInfo<>();
            result.setTotal(search.getTotal());
            result.setList(evoucherConverter.toDtoList(search.getList()));
            return RpcResponse.ok(result);
        }, "EvoucherFacadeImpl.queryEvouchersBySearch");
    }

    @Override
    public RpcResponse writeOff(EvoucherModifyRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            evoucherProcessService.writeOff(req);
            return RpcResponse.ok(null);
        }, "EvoucherFacadeImpl.writeOff");
    }
}
