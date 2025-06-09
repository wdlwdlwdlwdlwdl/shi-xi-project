package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherModifyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherSearchRpcReq;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;

import java.util.List;

public interface EvoucherProcessService {

    /**
     * 核销
     */
    void writeOff(EvoucherModifyRpcReq req);

    /**
     * 销毁, 退货触发
     */
    void makeDisabled(Long evCode);
}
