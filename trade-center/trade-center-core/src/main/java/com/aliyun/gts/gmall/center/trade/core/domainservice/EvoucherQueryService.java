package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherSearchRpcReq;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;

import java.util.List;

public interface EvoucherQueryService {

    /**
     * 查询
     */
    List<EvoucherInstance> queryEvouchers(EvoucherQueryRpcReq req);

    /**
     * 搜索
     */
    PageInfo<EvoucherInstance> searchEvouchers(EvoucherSearchRpcReq req);
}
