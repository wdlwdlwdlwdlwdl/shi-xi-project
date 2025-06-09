package com.aliyun.gts.gmall.center.trade.core.extension.common;

import com.aliyun.gts.gmall.center.trade.core.domainservice.CombineItemService;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommonReversalCreateExt extends DefaultReversalCreateExt {

    @Autowired
    private CombineItemService combineItemService;

    @Override
    public void fillReversalInfo(MainReversal reversal, CreateReversalRpcReq req) {
        //this.combineItemService.fillReversal(reversal, req);
    }
}
